package karashokleo.loot_bag.validation;

import com.google.gson.JsonParser;
import com.mojang.serialization.JsonOps;
import io.netty.buffer.Unpooled;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.bag.*;
import karashokleo.loot_bag.api.common.content.*;
import karashokleo.loot_bag.api.common.icon.*;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.packet.*;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.registry.Registries;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import net.minecraftforge.data.event.GatherDataEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

/** Runs inside Forge's transformed environment. Never part of the release jar. */
@Mod.EventBusSubscriber(modid = LootBagMod.FORGE_MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class LootBagValidation
{
    private static final List<String> PASSED = new ArrayList<>();
    private static final LootBagManager MANAGER = LootBagManager.getInstance();
    private static final Bag.Color COLOR = new Bag.Color(0x123456, 0xabcdef);

    @SubscribeEvent
    public static void validate(GatherDataEvent event) throws Exception
    {
        check(MANAGER.getAllBagEntries().isEmpty() && MANAGER.getAllContentEntries().isEmpty(), "default jar has no built-in bags");
        check(LootBagRegistry.CONTENT_TYPE_REGISTRY.size() == 4 && LootBagRegistry.BAG_TYPE_REGISTRY.size() == 3
                && LootBagRegistry.ICON_TYPE_REGISTRY.size() == 2, "all three synced type registries");
        check(Registries.ITEM.getId(LootBagItemRegistry.LOOT_BAG).equals(LootBagMod.id("loot_bag"))
                && Registries.LOOT_POOL_ENTRY_TYPE.getId(LootBagEntry.TYPE).equals(LootBagMod.id("loot_bag")),
                "legacy item and custom loot-entry identifiers");
        Path examples = Path.of(System.getProperty("loot_bag.validation.examples"));
        try (var files = Files.list(examples.resolve("data/loot-bag/loot-bag/content")))
        {
            for (Path file : files.toList())
            {
                Content content = Content.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file)))
                        .getOrThrow(false, message -> { throw new AssertionError(message); });
                MANAGER.putContent(LootBagMod.id(file.getFileName().toString().replace(".json", "")), content);
            }
        }
        check(MANAGER.getAllContentEntries().size() == 7, "all original example content JSON decodes");
        try (var files = Files.list(examples.resolve("data/loot-bag/loot-bag/bag")))
        {
            for (Path file : files.toList())
            {
                Bag bag = Bag.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString(Files.readString(file)))
                        .getOrThrow(false, message -> { throw new AssertionError(message); });
                MANAGER.putBag(LootBagMod.id(file.getFileName().toString().replace(".json", "")), bag);
            }
        }
        check(MANAGER.getAllBagEntries().size() == 3, "all original example bag JSON decodes");
        for (ContentEntry entry : MANAGER.getAllContentEntries())
            require(Content.CODEC.parse(JsonOps.INSTANCE, Content.CODEC.encodeStart(JsonOps.INSTANCE, entry.content()).result().orElseThrow()).result().isPresent());
        check(true, "four content types and both icon types round-trip");
        for (BagEntry entry : MANAGER.getAllBagEntries())
            require(Bag.CODEC.parse(JsonOps.INSTANCE, Bag.CODEC.encodeStart(JsonOps.INSTANCE, entry.bag()).result().orElseThrow()).result().isPresent());
        check(true, "three bag types round-trip");
        ContentEntry beef = MANAGER.getContentEntry(LootBagMod.id("beef"));
        ContentEntry sword = MANAGER.getContentEntry(LootBagMod.id("diamond_sword"));
        require(new SingleBag(beef, Rarity.COMMON, COLOR).getContent(context(0)).orElseThrow() == beef.content());
        OptionalBag optional = new OptionalBag(List.of(beef, sword), Rarity.RARE, COLOR);
        require(optional.getContent(context(1)).orElseThrow() == sword.content());
        require(optional.getContent(context(2)).isEmpty());
        check(SingleBag.TYPE.quick() && RandomBag.TYPE.quick() && !OptionalBag.TYPE.quick(), "single choice, optional bounds and quick-open policy");
        RandomBag random = new RandomBag(List.of(new RandomBag.Entry(beef, 0), new RandomBag.Entry(sword, 5)), Rarity.EPIC, COLOR);
        for (int i = 0; i < 200; i++) require(random.getContent(context(i)).orElseThrow() == sword.content());
        require(new RandomBag(List.of(), Rarity.COMMON, COLOR).getContent(context(0)).isEmpty());
        require(new RandomBag(List.of(new RandomBag.Entry(beef, 0), new RandomBag.Entry(sword, 0)), Rarity.COMMON, COLOR)
                .getContent(context(0)).orElseThrow() == beef.content());
        check(true, "weighted random and inherited empty/zero-weight behavior");
        for (BagEntry entry : MANAGER.getAllBagEntries())
        {
            ItemStack stack = LootBagItemRegistry.LOOT_BAG.getStack(entry);
            require(LootBagItemRegistry.LOOT_BAG.getBagEntry(stack).orElseThrow().id().equals(entry.id()));
            require(LootBagItemRegistry.LOOT_BAG.getRarity(stack) == entry.bag().getRarity());
            require(stack.getNbt().getString("BagId").equals(entry.id().toString()));
        }
        check(LootBagItemRegistry.LOOT_BAG.getMaxCount() == 16 && COLOR.byTintIndex(0) == 0x123456
                && COLOR.byTintIndex(1) == 0xabcdef, "BagId NBT, stack size, rarity and tints");
        check(LootBagItemRegistry.LOOT_BAG.getBag(LootBagItemRegistry.LOOT_BAG.getDefaultStack()).isEmpty()
                && LootBagItemRegistry.LOOT_BAG.getBag(LootBagItemRegistry.LOOT_BAG.getStack(new Identifier("missing", "bag"))).isEmpty(),
                "invalid and missing bag identifiers");
        ItemStack swordStack = ((ItemContent)sword.content()).getStack();
        require(swordStack.getDamage() == 66);
        ItemStack decoded = CodecUtil.ITEM_STACK_CODEC.parse(JsonOps.INSTANCE,
                CodecUtil.ITEM_STACK_CODEC.encodeStart(JsonOps.INSTANCE, swordStack).result().orElseThrow()).result().orElseThrow();
        require(decoded.getDamage() == 66 && decoded.isOf(Items.DIAMOND_SWORD));
        check(((ItemIcon)sword.content().getIcon()).getStack().hasEnchantments(), "item NBT and enchanted icon preserved");
        PacketByteBuf buffer = new PacketByteBuf(Unpooled.buffer());
        try {
            new OpenBagPacket(40, 2).write(buffer);
            require(OpenBagPacket.read(buffer).equals(new OpenBagPacket(40, 2)));
            new SetScreenPacket(4, LootBagMod.id("optional")).write(buffer);
            require(SetScreenPacket.read(buffer).equals(new SetScreenPacket(4, LootBagMod.id("optional"))));
            new SyncDataPackets.AckPacket(7).write(buffer);
            require(SyncDataPackets.AckPacket.read(buffer).count() == 7);
        } finally { buffer.release(); }
        check(true, "open, screen and ACK packet layouts");
        buffer = new PacketByteBuf(Unpooled.buffer());
        try {
            new SyncDataPackets.SyncContentPacket(MANAGER.getAllContentEntries()).write(buffer);
            require(SyncDataPackets.readContent(buffer).entries().size() == 7);
            new SyncDataPackets.SyncBagPacket(MANAGER.getAllBagEntries()).write(buffer);
            require(SyncDataPackets.readBag(buffer).entries().size() == 3);
        } finally { buffer.release(); }
        check(true, "content and bag synchronization packet codecs");
        require(BagEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"missing:bag\"")).error().isPresent());
        require(ContentEntry.CODEC.parse(JsonOps.INSTANCE, JsonParser.parseString("\"missing:content\"")).error().isPresent());
        boolean rejected = false;
        try { context(-1); } catch (IllegalArgumentException expected) { rejected = true; }
        check(rejected, "unknown references and inherited negative-index rejection");
        MANAGER.clearAllBagEntries();
        MANAGER.clearAllContentEntries();
        Path report = Path.of(System.getProperty("loot_bag.validation.report"));
        Files.createDirectories(report.getParent());
        Files.writeString(report, new com.google.gson.GsonBuilder().setPrettyPrinting().create().toJson(
                java.util.Map.of("passed", PASSED.size(), "failed", 0, "checks", PASSED)) + "\n");
        System.out.println("Loot Bag: " + PASSED.size() + " Forge runtime regression checks passed.");
    }

    private static OpenBagContext context(int selected) { return new OpenBagContext(Random.create(123), selected); }
    private static void require(boolean value) { if (!value) throw new AssertionError("Loot Bag regression assertion failed"); }
    private static void check(boolean value, String name) { require(value); PASSED.add(name); }
}
