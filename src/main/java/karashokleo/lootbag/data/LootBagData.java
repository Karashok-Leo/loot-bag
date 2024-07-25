package karashokleo.lootbag.data;

import karashokleo.lootbag.LootBag;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.content.logic.bag.Bag;
import karashokleo.lootbag.content.logic.bag.OptionalBag;
import karashokleo.lootbag.content.logic.bag.RandomBag;
import karashokleo.lootbag.content.logic.bag.SingleBag;
import karashokleo.lootbag.content.logic.content.CommandContent;
import karashokleo.lootbag.content.logic.content.Content;
import karashokleo.lootbag.content.logic.content.ItemContent;
import karashokleo.lootbag.content.logic.content.LootTableContent;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.registry.Registerable;
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryEntryLookup;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;

public class LootBagData implements DataGeneratorEntrypoint
{
    public static final RegistryKey<Content> BEEF = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("beef"));
    public static final RegistryKey<Content> DIAMOND_SWORD = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("diamond_sword"));
    public static final RegistryKey<Content> STONE = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("stone"));
    public static final RegistryKey<Content> ZOMBIE = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("zombie"));
    public static final RegistryKey<Content> SKELETON = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("skeleton"));
    public static final RegistryKey<Content> CREEPER = RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("creeper"));
    public static final RegistryKey<Bag> SINGLE = RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("single"));
    public static final RegistryKey<Bag> OPTIONAL = RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("optional"));
    public static final RegistryKey<Bag> RANDOM = RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("random"));

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(LootBagProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder)
    {
        registryBuilder.addRegistry(LootBagRegistry.CONTENT_KEY, this::bootstrapContent);
        registryBuilder.addRegistry(LootBagRegistry.BAG_KEY, this::bootstrapBag);
    }

    private void bootstrapContent(Registerable<Content> registerable)
    {
        ItemContent beef = new ItemContent(Items.BEEF.getDefaultStack(), new Content.Icon(new Identifier("textures/item/beef.png")), 0);
        ItemStack diamondSwordStack = Items.DIAMOND_SWORD.getDefaultStack();
        diamondSwordStack.setDamage(66);
        ItemContent diamondSword = new ItemContent(diamondSwordStack, new Content.Icon(new Identifier("textures/item/bell.png")), 1);
        LootTableContent stone = new LootTableContent(new Identifier("blocks/stone"), new Content.Icon(new Identifier("textures/block/stone.png")), 2);
        LootTableContent zombie = new LootTableContent(new Identifier("entities/zombie"), new Content.Icon(new Identifier("textures/item/rotten_flesh.png")), 3);
        CommandContent skeleton = new CommandContent("/kill", new Content.Icon(new Identifier("textures/item/bone.png")), 4);
        CommandContent creeper = new CommandContent("/summon minecraft:creeper ~ ~ ~", new Content.Icon(new Identifier("textures/item/gunpowder.png")), 5);

        registerable.register(BEEF, beef);
        registerable.register(DIAMOND_SWORD, diamondSword);
        registerable.register(STONE, stone);
        registerable.register(ZOMBIE, zombie);
        registerable.register(SKELETON, skeleton);
        registerable.register(CREEPER, creeper);
    }

    private void bootstrapBag(Registerable<Bag> registerable)
    {
        RegistryEntryLookup<Content> lookup = registerable.getRegistryLookup(LootBagRegistry.CONTENT_KEY);

        SingleBag single = new SingleBag(lookup.getOrThrow(BEEF), Rarity.COMMON, new Bag.Color(0x000000, 0xffffff));
        OptionalBag optional = new OptionalBag(List.of(lookup.getOrThrow(DIAMOND_SWORD), lookup.getOrThrow(STONE)), Rarity.RARE, new Bag.Color(0x00fffa, 0xffffff));
        RandomBag random = new RandomBag(List.of(new RandomBag.Entry(lookup.getOrThrow(ZOMBIE), 3), new RandomBag.Entry(lookup.getOrThrow(SKELETON), 2), new RandomBag.Entry(lookup.getOrThrow(CREEPER), 1)), Rarity.EPIC, new Bag.Color(0x000000, 0xffffff));

        registerable.register(SINGLE, single);
        registerable.register(OPTIONAL, optional);
        registerable.register(RANDOM, random);
    }
}
