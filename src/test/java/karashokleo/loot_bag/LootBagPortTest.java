package karashokleo.loot_bag;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.Unpooled;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.bag.*;
import karashokleo.loot_bag.api.common.content.*;
import karashokleo.loot_bag.api.common.icon.*;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.packet.*;
import net.minecraft.Bootstrap;
import net.minecraft.SharedConstants;
import net.minecraft.component.ComponentMap;
import net.minecraft.component.DataComponentTypes;
import net.minecraft.component.type.AttributeModifierSlot;
import net.minecraft.component.type.NbtComponent;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.registry.*;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.registry.entry.RegistryEntryList;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class LootBagPortTest
{
    private static DynamicRegistryManager registries;
    private static RegistryEntry<Enchantment> looting;
    private static final Bag.Color COLOR = new Bag.Color(0x123456, 0xabcdef);
    private final LootBagManager manager = LootBagManager.getInstance();

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.createGameVersion();
        Bootstrap.initialize();
        new LootBagMod().onInitialize();
        SimpleRegistry<Enchantment> enchantments = new SimpleRegistry<>(RegistryKeys.ENCHANTMENT, Lifecycle.stable());
        looting = Registry.registerReference(enchantments, Identifier.of("minecraft", "looting"),
                new Enchantment(Text.literal("Looting"), Enchantment.definition(
                        RegistryEntryList.of(Items.DIAMOND_SWORD.getRegistryEntry()), 1, 3,
                        Enchantment.constantCost(1), Enchantment.constantCost(10), 1,
                        new AttributeModifierSlot[]{AttributeModifierSlot.MAINHAND}),
                        RegistryEntryList.of(), ComponentMap.EMPTY));
        enchantments.freeze();
        List<Registry<?>> all = new ArrayList<>();
        Registries.REGISTRIES.forEach(all::add);
        all.add(enchantments);
        registries = new DynamicRegistryManager.ImmutableImpl(all);
    }

    @BeforeEach
    void reset()
    {
        manager.clearAllContentEntries();
        manager.clearAllBagEntries();
    }

    private ContentEntry content(String name)
    {
        Identifier id = Identifier.of("test", name);
        manager.putContent(id, new ItemContent(Items.DIAMOND));
        return manager.getContentEntry(id);
    }

    private ItemStack parseStack(String json)
    {
        return CodecUtil.ITEM_STACK_CODEC.parse(registries.getOps(JsonOps.INSTANCE), JsonParser.parseString(json)).getOrThrow();
    }

    @Test
    void emptyManagerHasNoDefaultBags()
    {
        assertTrue(manager.getAllBagEntries().isEmpty());
        assertTrue(manager.getAllContentEntries().isEmpty());
    }

    @Test
    void simpleItemShorthandRoundTrips()
    {
        ItemStack stack = parseStack("\"minecraft:beef\"");
        assertTrue(stack.isOf(Items.BEEF));
        assertEquals(1, stack.getCount());
        assertEquals("minecraft:beef", CodecUtil.ITEM_STACK_CODEC.encodeStart(registries.getOps(JsonOps.INSTANCE), stack).getOrThrow().getAsString());
    }

    @Test
    void legacyCountDamageEnchantmentsAndCustomDataSurvive()
    {
        ItemStack stack = parseStack("""
                {"id":"minecraft:diamond_sword","Count":2,"tag":{"Damage":66,
                "Enchantments":[{"id":"minecraft:looting","lvl":3}],"BagId":"test:bag","Unbreakable":1}}
                """);
        assertTrue(stack.isOf(Items.DIAMOND_SWORD));
        assertEquals(2, stack.getCount());
        assertEquals(66, stack.getDamage());
        assertNotNull(stack.get(DataComponentTypes.UNBREAKABLE));
        assertEquals(3, EnchantmentHelper.getLevel(looting, stack));
        assertEquals("test:bag", stack.getOrDefault(DataComponentTypes.CUSTOM_DATA, NbtComponent.DEFAULT).copyNbt().getString("BagId"));
        ItemStack decoded = CodecUtil.ITEM_STACK_CODEC.parse(registries.getOps(JsonOps.INSTANCE),
                CodecUtil.ITEM_STACK_CODEC.encodeStart(registries.getOps(JsonOps.INSTANCE), stack).getOrThrow()).getOrThrow();
        assertTrue(ItemStack.areEqual(stack, decoded));
    }

    @Test
    void legacyEmptyAirAndLargeCountsRemainValid()
    {
        assertTrue(parseStack("{\"id\":\"minecraft:diamond\",\"Count\":0}").isEmpty());
        assertTrue(parseStack("{\"id\":\"minecraft:diamond\",\"Count\":-1}").isEmpty());
        assertTrue(parseStack("{\"id\":\"minecraft:air\",\"Count\":1}").isEmpty());
        for (int count : new int[]{100, 127, 256, Integer.MAX_VALUE})
        {
            ItemStack stack = parseStack("{\"id\":\"minecraft:diamond\",\"Count\":" + count + "}");
            assertEquals(count, stack.getCount());
            var encoded = CodecUtil.ITEM_STACK_CODEC.encodeStart(registries.getOps(JsonOps.INSTANCE), stack).getOrThrow();
            assertEquals(count, CodecUtil.ITEM_STACK_CODEC.parse(registries.getOps(JsonOps.INSTANCE), encoded).getOrThrow().getCount());
        }
    }

    @Test
    void emptyStackSerializationRoundTrips()
    {
        var encoded = CodecUtil.ITEM_STACK_CODEC.encodeStart(registries.getOps(JsonOps.INSTANCE), ItemStack.EMPTY).getOrThrow();
        assertTrue(CodecUtil.ITEM_STACK_CODEC.parse(registries.getOps(JsonOps.INSTANCE), encoded).getOrThrow().isEmpty());
    }

    @Test
    void modernComponentStacksRoundTrip()
    {
        ItemStack stack = parseStack("""
                {"id":"minecraft:diamond_sword","count":3,"components":{"minecraft:damage":17}}
                """);
        assertEquals(3, stack.getCount());
        assertEquals(17, stack.getDamage());
    }

    @Test
    void itemContentRewardsCopies()
    {
        class ExposedItemContent extends ItemContent
        {
            ExposedItemContent(ItemStack stack) { super(stack); }
            List<ItemStack> rewardStacks() { return getLootStacks(null); }
        }
        ExposedItemContent content = new ExposedItemContent(new ItemStack(Items.DIAMOND, 3));
        ItemStack reward = content.rewardStacks().getFirst();
        reward.decrement(1);
        assertEquals(3, content.getStack().getCount());
        assertEquals(2, reward.getCount());
    }

    @Test
    void singleAndOptionalSelectionRemainUnchanged()
    {
        ContentEntry first = content("first"), second = content("second");
        SingleBag single = new SingleBag(first, Rarity.COMMON, COLOR);
        OptionalBag optional = new OptionalBag(List.of(first, second), Rarity.RARE, COLOR);
        assertSame(first.content(), single.getContent(new OpenBagContext(Random.create(1), 99)).orElseThrow());
        assertSame(second.content(), optional.getContent(new OpenBagContext(Random.create(1), 1)).orElseThrow());
        assertTrue(optional.getContent(new OpenBagContext(Random.create(1), 2)).isEmpty());
        assertTrue(single.getType().quick());
        assertFalse(optional.getType().quick());
    }

    @Test
    void randomWeightsAndZeroWeightBehaviorRemainUnchanged()
    {
        ContentEntry first = content("first"), second = content("second");
        RandomBag bag = new RandomBag(List.of(new RandomBag.Entry(first, 0), new RandomBag.Entry(second, 3)), Rarity.EPIC, COLOR);
        for (int seed = 0; seed < 50; seed++)
            assertSame(second.content(), bag.getContent(new OpenBagContext(Random.create(seed), 0)).orElseThrow());
        RandomBag zero = new RandomBag(List.of(new RandomBag.Entry(first, 0), new RandomBag.Entry(second, 0)), Rarity.COMMON, COLOR);
        assertSame(first.content(), zero.getContent(new OpenBagContext(Random.create(), 0)).orElseThrow());
        assertTrue(new RandomBag(List.of(), Rarity.COMMON, COLOR).getContent(new OpenBagContext(Random.create(), 0)).isEmpty());
        assertTrue(bag.getType().quick());
    }

    @Test
    void bagIdCustomDataAndDynamicRarityMatchMaster()
    {
        Identifier id = Identifier.of("test", "bag");
        manager.putBag(id, new SingleBag(content("reward"), Rarity.RARE, COLOR));
        ItemStack stack = LootBagItemRegistry.LOOT_BAG.getStack(id);
        assertEquals(id.toString(), stack.get(DataComponentTypes.CUSTOM_DATA).copyNbt().getString("BagId"));
        assertEquals(id, LootBagItemRegistry.LOOT_BAG.getBagEntry(stack).orElseThrow().id());
        assertEquals(Rarity.RARE, stack.getRarity());
        manager.putBag(id, new SingleBag(content("reward"), Rarity.EPIC, COLOR));
        assertEquals(Rarity.EPIC, stack.getRarity());
        assertEquals(16, stack.getMaxCount());
        assertEquals(COLOR.bagBody(), LootBagItemRegistry.LOOT_BAG.getBag(stack).orElseThrow().getColor().byTintIndex(0));
        assertTrue(LootBagItemRegistry.LOOT_BAG.getBagEntry(LootBagItemRegistry.LOOT_BAG.getDefaultStack()).isEmpty());
    }

    @Test
    void rarityKeepsVanillaEnchantmentPromotion()
    {
        Identifier id = Identifier.of("test", "bag");
        manager.putBag(id, new SingleBag(content("reward"), Rarity.COMMON, COLOR));
        ItemStack stack = LootBagItemRegistry.LOOT_BAG.getStack(id);
        stack.addEnchantment(looting, 1);
        assertEquals(Rarity.RARE, stack.getRarity());
        assertEquals(Rarity.COMMON, new ItemStack(Items.STONE).getRarity());
    }

    @Test
    void bundledLegacyExamplesDecodeAllTypes() throws Exception
    {
        Path root = Path.of("example/data/loot-bag/loot-bag");
        try (var paths = Files.list(root.resolve("content")))
        {
            for (Path path : paths.toList())
            {
                Content content = Content.CODEC.parse(registries.getOps(JsonOps.INSTANCE), JsonParser.parseString(Files.readString(path))).getOrThrow();
                manager.putContent(Identifier.of("loot-bag", path.getFileName().toString().replace(".json", "")), content);
            }
        }
        assertEquals(7, manager.getAllContentEntries().size());
        try (var paths = Files.list(root.resolve("bag")))
        {
            for (Path path : paths.toList())
            {
                Bag bag = Bag.CODEC.parse(registries.getOps(JsonOps.INSTANCE), JsonParser.parseString(Files.readString(path))).getOrThrow();
                manager.putBag(Identifier.of("loot-bag", path.getFileName().toString().replace(".json", "")), bag);
            }
        }
        assertEquals(3, manager.getAllBagEntries().size());
        ItemContent sword = (ItemContent) manager.getContentEntry(Identifier.of("loot-bag", "diamond_sword")).content();
        assertEquals(66, sword.getStack().getDamage());
        EffectContent effect = (EffectContent) manager.getContentEntry(Identifier.of("loot-bag", "effect")).content();
        assertEquals(StatusEffects.ABSORPTION, effect.getEffects().getFirst().getInstance().getEffectType());
    }

    @Test
    void unknownReferencesStillFailDecoding()
    {
        assertTrue(BagEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"test:missing\"")).error().isPresent());
        assertTrue(ContentEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"test:missing\"")).error().isPresent());
    }

    @Test
    void customLootEntryKeepsBagField()
    {
        var json = JsonParser.parseString("{\"bag\":\"test:bag\",\"weight\":3,\"quality\":2}");
        LootBagEntry entry = LootBagEntry.CODEC.codec().parse(registries.getOps(JsonOps.INSTANCE), json).getOrThrow();
        var encoded = LootBagEntry.CODEC.codec().encodeStart(registries.getOps(JsonOps.INSTANCE), entry).getOrThrow().getAsJsonObject();
        assertEquals("test:bag", encoded.get("bag").getAsString());
        assertEquals(3, encoded.get("weight").getAsInt());
        assertEquals(2, encoded.get("quality").getAsInt());
    }

    @Test
    void simplePacketsRoundTrip()
    {
        RegistryByteBuf buf = new RegistryByteBuf(Unpooled.buffer(), registries);
        try
        {
            OpenBagPacket.CODEC.encode(buf, new OpenBagPacket(40, 2));
            assertEquals(new OpenBagPacket(40, 2), OpenBagPacket.CODEC.decode(buf));
            SetScreenPacket.CODEC.encode(buf, new SetScreenPacket(1, Identifier.of("test", "bag")));
            assertEquals(new SetScreenPacket(1, Identifier.of("test", "bag")), SetScreenPacket.CODEC.decode(buf));
            SyncDataPackets.AckPacket.CODEC.encode(buf, new SyncDataPackets.AckPacket(7));
            assertEquals(7, SyncDataPackets.AckPacket.CODEC.decode(buf).count());
        } finally { buf.release(); }
    }

    @Test
    void contentThenBagSynchronizationRoundTrips()
    {
        ContentEntry reward = content("reward");
        Identifier bagId = Identifier.of("test", "bag");
        manager.putBag(bagId, new SingleBag(reward, Rarity.RARE, COLOR));
        RegistryByteBuf contents = new RegistryByteBuf(Unpooled.buffer(), registries);
        RegistryByteBuf bags = new RegistryByteBuf(Unpooled.buffer(), registries);
        try
        {
            SyncDataPackets.SYNC_CONTENT_CODEC.encode(contents, new SyncDataPackets.SyncContentPacket(manager.getAllContentEntries()));
            SyncDataPackets.SYNC_BAG_CODEC.encode(bags, new SyncDataPackets.SyncBagPacket(manager.getAllBagEntries()));
            manager.clearAllContentEntries();
            manager.clearAllBagEntries();
            for (ContentEntry entry : SyncDataPackets.SYNC_CONTENT_CODEC.decode(contents).entries())
                manager.putContent(entry.id(), entry.content());
            for (BagEntry entry : SyncDataPackets.SYNC_BAG_CODEC.decode(bags).entries())
                manager.putBag(entry.id(), entry.bag());
            assertEquals(1, manager.getAllBagEntries().size());
            assertEquals(1, manager.getAllContentEntries().size());
            assertEquals(Rarity.RARE, manager.getBagEntry(bagId).bag().getRarity());
        } finally { contents.release(); bags.release(); }
    }
}
