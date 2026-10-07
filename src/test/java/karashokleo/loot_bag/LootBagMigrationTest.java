package karashokleo.loot_bag;

import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.Lifecycle;
import io.netty.buffer.Unpooled;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.bag.*;
import karashokleo.loot_bag.api.common.content.*;
import karashokleo.loot_bag.api.common.icon.*;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import karashokleo.loot_bag.internal.network.packet.*;
import net.minecraft.SharedConstants;
import net.minecraft.core.*;
import net.minecraft.core.component.DataComponentMap;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.Bootstrap;
import net.minecraft.util.RandomSource;
import net.minecraft.world.entity.EquipmentSlotGroup;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryContainer;
import org.junit.jupiter.api.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;

class LootBagMigrationTest
{
    private static RegistryAccess registries;
    private static Holder<Enchantment> looting;
    private final LootBagManager manager = LootBagManager.getInstance();

    @BeforeAll
    static void bootstrap()
    {
        SharedConstants.tryDetectVersion();
        Bootstrap.bootStrap();
        MappedRegistry<Enchantment> enchantments = new MappedRegistry<>(Registries.ENCHANTMENT, Lifecycle.stable());
        looting = Registry.registerForHolder(enchantments, ResourceLocation.withDefaultNamespace("looting"), new Enchantment(
                Component.literal("Looting"), Enchantment.definition(HolderSet.direct(Items.DIAMOND_SWORD.builtInRegistryHolder()),
                1, 3, Enchantment.constantCost(1), Enchantment.constantCost(10), 1, EquipmentSlotGroup.MAINHAND),
                HolderSet.direct(), DataComponentMap.EMPTY));
        enchantments.freeze();
        List<Registry<?>> all = new ArrayList<>();
        BuiltInRegistries.REGISTRY.forEach(all::add);
        all.add(enchantments);
        registries = new RegistryAccess.ImmutableRegistryAccess(all);
    }

    @BeforeEach
    void reset()
    {
        manager.clearAllContentEntries();
        manager.clearAllBagEntries();
    }

    private void loadExamples() throws Exception
    {
        Path path = Path.of(System.getProperty("lootBag.projectDir"), "example/data/loot-bag/loot-bag");
        try (var files = Files.list(path.resolve("content")))
        {
            for (Path file : files.toList()) manager.putContent(LootBagMod.id(file.getFileName().toString().replace(".json", "")),
                    Content.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(Files.readString(file))).getOrThrow());
        }
        try (var files = Files.list(path.resolve("bag")))
        {
            for (Path file : files.toList()) manager.putBag(LootBagMod.id(file.getFileName().toString().replace(".json", "")),
                    Bag.CODEC.parse(registries.createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(Files.readString(file))).getOrThrow());
        }
    }

    @Test
    void allNativeExamplesAndNamespacesLoad() throws Exception
    {
        loadExamples();
        assertEquals(7, manager.getAllContentEntries().size());
        assertEquals(3, manager.getAllBagEntries().size());
        assertEquals("loot-bag:loot_bag", BuiltInRegistries.ITEM.getKey(LootBagItemRegistry.LOOT_BAG).toString());
        assertEquals("loot-bag:bag_type", LootBagRegistry.BAG_TYPE_KEY.location().toString());
        assertEquals("loot_bag", LootBagMod.LOADER_ID);
    }

    @Test
    void nativeExampleDamageAndEnchantmentSurvive() throws Exception
    {
        loadExamples();
        ItemContent content = (ItemContent) manager.getContentEntry(LootBagMod.id("diamond_sword")).content();
        assertEquals(66, content.getStack().getDamageValue());
        assertEquals(3, EnchantmentHelper.getEnchantmentsForCrafting(((ItemIcon) content.getIcon()).getStack()).getLevel(looting));
    }

    @Test
    void shorthandAndModernComponentStacksRoundTrip()
    {
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        ItemStack plain = CodecUtil.ITEM_STACK_CODEC.parse(ops, JsonParser.parseString("\"minecraft:beef\"")).getOrThrow();
        assertTrue(plain.is(Items.BEEF));
        assertEquals(1, plain.getCount());
        ItemStack sword = Items.DIAMOND_SWORD.getDefaultInstance();
        sword.setDamageValue(66);
        sword.enchant(looting, 3);
        JsonElement json = CodecUtil.ITEM_STACK_CODEC.encodeStart(ops, sword).getOrThrow();
        assertTrue(json.getAsJsonObject().has("components"));
        assertTrue(ItemStack.matches(sword, CodecUtil.ITEM_STACK_CODEC.parse(ops, json).getOrThrow()));
    }

    @Test
    void nativeCustomDataNamesAndUnbreakableRoundTrip()
    {
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        JsonElement json = JsonParser.parseString("""
                {"id":"minecraft:diamond_sword","count":2,"components":{
                  "minecraft:custom_data":{"CustomFlag":42},
                  "minecraft:unbreakable":{}}}
                """);
        json.getAsJsonObject().getAsJsonObject("components").addProperty("minecraft:custom_name", "\"Component name\"");
        ItemStack stack = CodecUtil.ITEM_STACK_CODEC.parse(ops, json).getOrThrow();
        assertEquals(2, stack.getCount());
        assertEquals(42, stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag().getInt("CustomFlag"));
        assertEquals("Component name", stack.getHoverName().getString());
        assertNotNull(stack.get(DataComponents.UNBREAKABLE));
        assertTrue(ItemStack.matches(stack, CodecUtil.ITEM_STACK_CODEC.parse(ops,
                CodecUtil.ITEM_STACK_CODEC.encodeStart(ops, stack).getOrThrow()).getOrThrow()));
    }

    @Test
    void stackObjectsUseVanillaCountValidation()
    {
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        assertEquals(99, CodecUtil.ITEM_STACK_CODEC.parse(ops,
                JsonParser.parseString("{\"id\":\"minecraft:stone\",\"count\":99}")).getOrThrow().getCount());
        for (int count : List.of(0, 100, 256))
        {
            JsonElement json = JsonParser.parseString("{\"id\":\"minecraft:stone\",\"count\":" + count + "}");
            assertTrue(ItemStack.matches(ItemStack.CODEC.parse(ops, json).getOrThrow(),
                    CodecUtil.ITEM_STACK_CODEC.parse(ops, json).getOrThrow()));
        }
    }

    @Test
    void bagIdComponentNameAndDynamicRaritySurviveReload() throws Exception
    {
        loadExamples();
        var item = LootBagItemRegistry.LOOT_BAG;
        var id = LootBagMod.id("optional");
        ItemStack stack = item.getStack(id);
        assertEquals(id.toString(), stack.get(DataComponents.CUSTOM_DATA).copyTag().getString("BagId"));
        assertEquals("bag.loot-bag.optional", item.getName(stack).getString());
        assertEquals(Rarity.RARE, item.getRarity(stack));
        assertEquals(Rarity.RARE, stack.getRarity());
        OptionalBag original = (OptionalBag) manager.getBagEntry(id).bag();
        manager.putBag(id, new OptionalBag(original.getOptions(), Rarity.EPIC, original.getColor()));
        assertEquals(Rarity.EPIC, item.getRarity(stack));
        assertEquals(Rarity.EPIC, stack.getRarity());
        assertEquals(16, stack.getMaxStackSize());
    }

    @Test
    void enchantedBagRarityPromotionIsPreserved() throws Exception
    {
        loadExamples();
        ItemStack single = LootBagItemRegistry.LOOT_BAG.getStack(LootBagMod.id("single"));
        single.enchant(looting, 1);
        assertEquals(Rarity.RARE, single.getRarity());
        ItemStack optional = LootBagItemRegistry.LOOT_BAG.getStack(LootBagMod.id("optional"));
        optional.enchant(looting, 1);
        assertEquals(Rarity.EPIC, optional.getRarity());
        ItemStack vanilla = Items.DIAMOND.getDefaultInstance();
        assertEquals(Rarity.COMMON, vanilla.getRarity());
    }

    @Test
    void emptyPackInvalidAndUnknownBagRemainEmpty()
    {
        assertTrue(manager.getAllBagEntries().isEmpty());
        assertTrue(LootBagItemRegistry.LOOT_BAG.getBag(LootBagItemRegistry.LOOT_BAG.getDefaultInstance()).isEmpty());
        assertTrue(LootBagItemRegistry.LOOT_BAG.getBag(LootBagItemRegistry.LOOT_BAG.getStack(LootBagMod.id("missing"))).isEmpty());
        assertTrue(BagEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"loot-bag:missing\"")).error().isPresent());
    }

    @Test
    void singleAndOptionalSelectionStayUnchanged() throws Exception
    {
        loadExamples();
        SingleBag single = (SingleBag) manager.getBagEntry(LootBagMod.id("single")).bag();
        assertSame(single.getContent().content(), single.getContent(new OpenBagContext(RandomSource.create(0), 999)).orElseThrow());
        OptionalBag optional = (OptionalBag) manager.getBagEntry(LootBagMod.id("optional")).bag();
        for (int i = 0; i < 3; i++) assertSame(optional.getOptions().get(i).content(), optional.getContent(new OpenBagContext(RandomSource.create(0), i)).orElseThrow());
        assertTrue(optional.getContent(new OpenBagContext(RandomSource.create(0), 3)).isEmpty());
        assertTrue(single.getType().quick());
        assertFalse(optional.getType().quick());
    }

    @Test
    void randomWeightAlgorithmRetainsDeterministicResults() throws Exception
    {
        loadExamples();
        RandomBag random = (RandomBag) manager.getBagEntry(LootBagMod.id("random")).bag();
        RandomSource expected = RandomSource.create(42), actual = RandomSource.create(42);
        for (int i = 0; i < 1000; i++)
        {
            int value = expected.nextInt(6);
            var entry = random.getPool().get(value < 3 ? 0 : value < 5 ? 1 : 2);
            assertSame(entry.getContent(), random.getContent(new OpenBagContext(actual, 0)).orElseThrow());
        }
        assertTrue(new RandomBag(List.of(), Rarity.COMMON, random.getColor()).getContent(new OpenBagContext(actual, 0)).isEmpty());
    }

    @Test
    void contentAndBagNetworkPayloadsRoundTripInOriginalOrder() throws Exception
    {
        loadExamples();
        var contentBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        var bagBuf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        SyncDataPackets.SyncContentPacket.STREAM_CODEC.encode(contentBuf, new SyncDataPackets.SyncContentPacket(List.copyOf(manager.getAllContentEntries())));
        SyncDataPackets.SyncBagPacket.STREAM_CODEC.encode(bagBuf, new SyncDataPackets.SyncBagPacket(List.copyOf(manager.getAllBagEntries())));
        reset();
        var contents = SyncDataPackets.SyncContentPacket.STREAM_CODEC.decode(contentBuf);
        contents.entries().forEach(entry -> manager.putContent(entry.id(), entry.content()));
        var bags = SyncDataPackets.SyncBagPacket.STREAM_CODEC.decode(bagBuf);
        bags.entries().forEach(entry -> manager.putBag(entry.id(), entry.bag()));
        assertEquals(7, manager.getAllContentEntries().size());
        assertEquals(3, manager.getAllBagEntries().size());
        assertEquals(0, contentBuf.readableBytes());
        assertEquals(0, bagBuf.readableBytes());
        contentBuf.release(); bagBuf.release();
    }

    @Test
    void emptySyncPayloadsRoundTrip()
    {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        SyncDataPackets.SyncContentPacket.STREAM_CODEC.encode(buf, new SyncDataPackets.SyncContentPacket(List.of()));
        assertTrue(SyncDataPackets.SyncContentPacket.STREAM_CODEC.decode(buf).entries().isEmpty());
        SyncDataPackets.SyncBagPacket.STREAM_CODEC.encode(buf, new SyncDataPackets.SyncBagPacket(List.of()));
        assertTrue(SyncDataPackets.SyncBagPacket.STREAM_CODEC.decode(buf).entries().isEmpty());
        buf.release();
    }

    @Test
    void actionAndScreenPayloadValuesAreUnchanged()
    {
        var buf = new RegistryFriendlyByteBuf(Unpooled.buffer(), registries);
        var open = new OpenBagPacket(40, 2);
        OpenBagPacket.STREAM_CODEC.encode(buf, open);
        assertEquals(open, OpenBagPacket.STREAM_CODEC.decode(buf));
        var screen = new SetScreenPacket(0, LootBagMod.id("optional"));
        SetScreenPacket.STREAM_CODEC.encode(buf, screen);
        assertEquals(screen, SetScreenPacket.STREAM_CODEC.decode(buf));
        var ack = new SyncDataPackets.AckPacket(7);
        SyncDataPackets.AckPacket.STREAM_CODEC.encode(buf, ack);
        assertEquals(ack, SyncDataPackets.AckPacket.STREAM_CODEC.decode(buf));
        buf.release();
    }

    @Test
    void customLootEntryCodecPreservesBagId()
    {
        var ops = registries.createSerializationContext(JsonOps.INSTANCE);
        var entry = LootBagEntry.builder(LootBagMod.id("single")).setWeight(3).setQuality(2).build();
        JsonElement encoded = net.minecraft.world.level.storage.loot.entries.LootPoolEntries.CODEC.encodeStart(ops, entry).getOrThrow();
        assertEquals("loot-bag:loot_bag", encoded.getAsJsonObject().get("type").getAsString());
        assertEquals("loot-bag:single", encoded.getAsJsonObject().get("bag").getAsString());
        assertEquals(encoded, net.minecraft.world.level.storage.loot.entries.LootPoolEntries.CODEC.encodeStart(ops, net.minecraft.world.level.storage.loot.entries.LootPoolEntries.CODEC.parse(ops, encoded).getOrThrow()).getOrThrow());
    }

    @Test
    void effectAndCommandExampleDetailsUnchanged() throws Exception
    {
        loadExamples();
        var effects = (EffectContent) manager.getContentEntry(LootBagMod.id("effect")).content();
        assertEquals(2400, effects.getEffects().getFirst().duration());
        assertEquals(1, effects.getEffects().get(1).amplifier());
        assertEquals("/kill @s", ((CommandContent) manager.getContentEntry(LootBagMod.id("skeleton")).content()).getCommand());
        assertEquals("/summon minecraft:creeper ~ ~ ~", ((CommandContent) manager.getContentEntry(LootBagMod.id("creeper")).content()).getCommand());
    }
}
