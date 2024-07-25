package karashokleo.lootbag.data;

import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.LootBagRegistryKeys;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.bag.OptionalBag;
import karashokleo.lootbag.api.common.bag.RandomBag;
import karashokleo.lootbag.api.common.bag.SingleBag;
import karashokleo.lootbag.api.common.content.CommandContent;
import karashokleo.lootbag.api.common.content.Content;
import karashokleo.lootbag.api.common.content.ItemContent;
import karashokleo.lootbag.api.common.content.LootTableContent;
import karashokleo.lootbag.fabric.LootBagMod;
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

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBagData implements DataGeneratorEntrypoint
{
    public static final RegistryKey<Content> BEEF = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("beef"));
    public static final RegistryKey<Content> DIAMOND_SWORD = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("diamond_sword"));
    public static final RegistryKey<Content> STONE = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("stone"));
    public static final RegistryKey<Content> ZOMBIE = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("zombie"));
    public static final RegistryKey<Content> SKELETON = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("skeleton"));
    public static final RegistryKey<Content> CREEPER = RegistryKey.of(LootBagRegistryKeys.CONTENT_KEY, LootBagMod.id("creeper"));
    public static final RegistryKey<Bag> SINGLE = RegistryKey.of(LootBagRegistryKeys.BAG_KEY, LootBagMod.id("single"));
    public static final RegistryKey<Bag> OPTIONAL = RegistryKey.of(LootBagRegistryKeys.BAG_KEY, LootBagMod.id("optional"));
    public static final RegistryKey<Bag> RANDOM = RegistryKey.of(LootBagRegistryKeys.BAG_KEY, LootBagMod.id("random"));

    public static final Map<RegistryKey<Content>, Content> CONTENTS = new HashMap<>();
    public static final Map<RegistryKey<Bag>, Bag> BAGS = new HashMap<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(LootBagProvider::new);
        pack.addProvider(LanguageProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder)
    {
        registryBuilder.addRegistry(LootBagRegistryKeys.CONTENT_KEY, this::bootstrapContent);
        registryBuilder.addRegistry(LootBagRegistryKeys.BAG_KEY, this::bootstrapBag);
    }

    private void bootstrapContent(Registerable<Content> registerable)
    {
        ItemContent beef = new ItemContent(Items.BEEF.getDefaultStack(), new Content.Icon(new Identifier("textures/item/beef.png")), 0);
        ItemStack diamondSwordStack = Items.DIAMOND_SWORD.getDefaultStack();
        diamondSwordStack.setDamage(66);
        ItemContent diamondSword = new ItemContent(diamondSwordStack, new Content.Icon(new Identifier("textures/item/diamond_sword.png")), 1);
        LootTableContent stone = new LootTableContent(new Identifier("blocks/stone"), new Content.Icon(new Identifier("textures/block/stone.png")), 2);
        LootTableContent zombie = new LootTableContent(new Identifier("entities/zombie"), new Content.Icon(new Identifier("textures/item/rotten_flesh.png")), 3);
        CommandContent skeleton = new CommandContent("/kill", new Content.Icon(new Identifier("textures/item/bone.png")), 4);
        CommandContent creeper = new CommandContent("/summon minecraft:creeper ~ ~ ~", new Content.Icon(new Identifier("textures/item/gunpowder.png")), 5);

        CONTENTS.put(BEEF, beef);
        CONTENTS.put(DIAMOND_SWORD, diamondSword);
        CONTENTS.put(STONE, stone);
        CONTENTS.put(ZOMBIE, zombie);
        CONTENTS.put(SKELETON, skeleton);
        CONTENTS.put(CREEPER, creeper);

        CONTENTS.forEach(registerable::register);
    }

    private void bootstrapBag(Registerable<Bag> registerable)
    {
        RegistryEntryLookup<Content> lookup = registerable.getRegistryLookup(LootBagRegistryKeys.CONTENT_KEY);

        SingleBag single = new SingleBag(lookup.getOrThrow(BEEF), Rarity.COMMON, new Bag.Color(0x3a3a3a, 0x8b8b8b));
        OptionalBag optional = new OptionalBag(List.of(lookup.getOrThrow(DIAMOND_SWORD), lookup.getOrThrow(STONE)), Rarity.RARE, new Bag.Color(0x00fffa, 0x00ffff));
        RandomBag random = new RandomBag(List.of(new RandomBag.Entry(lookup.getOrThrow(ZOMBIE), 3), new RandomBag.Entry(lookup.getOrThrow(SKELETON), 2), new RandomBag.Entry(lookup.getOrThrow(CREEPER), 1)), Rarity.EPIC, new Bag.Color(0x000000, 0xffffff));

        BAGS.put(SINGLE, single);
        BAGS.put(OPTIONAL, optional);
        BAGS.put(RANDOM, random);

        BAGS.forEach(registerable::register);
    }
}
