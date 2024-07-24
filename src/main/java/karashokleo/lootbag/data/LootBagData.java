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
import net.minecraft.registry.RegistryBuilder;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LootBagData implements DataGeneratorEntrypoint
{
    public static final Map<RegistryKey<Content>, Content> CONTENTS = new HashMap<>();
    public static final Map<RegistryKey<Bag>, Bag> BAGS = new HashMap<>();

    static
    {
        ItemContent beef = new ItemContent(Items.BEEF.getDefaultStack(), new Content.Icon(new Identifier("textures/item/beef.png")), 0);
        ItemStack diamondSwordStack = Items.DIAMOND_SWORD.getDefaultStack();
        diamondSwordStack.setDamage(66);
        ItemContent diamondSword = new ItemContent(diamondSwordStack, new Content.Icon(new Identifier("textures/item/bell.png")), 1);
        LootTableContent stone = new LootTableContent(new Identifier("blocks/stone"), new Content.Icon(new Identifier("textures/block/stone.png")), 2);
        LootTableContent zombie = new LootTableContent(new Identifier("entities/zombie"), new Content.Icon(new Identifier("textures/item/rotten_flesh.png")), 3);
        CommandContent skeleton = new CommandContent("/kill", new Content.Icon(new Identifier("textures/item/bone.png")), 4);
        CommandContent creeper = new CommandContent("/summon minecraft:creeper ~ ~ ~", new Content.Icon(new Identifier("textures/item/gunpowder.png")), 5);

        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("beef")), beef);
        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("diamond_sword")), diamondSword);
        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("stone")), stone);
        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("zombie")), zombie);
        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("skeleton")), skeleton);
        CONTENTS.put(RegistryKey.of(LootBagRegistry.CONTENT_KEY, LootBag.id("creeper")), creeper);

        SingleBag single = new SingleBag(LootBag.id("beef"), Rarity.COMMON, new Bag.Color(0x000000, 0xffffff));
        OptionalBag optional = new OptionalBag(List.of(LootBag.id("diamond_sword"), LootBag.id("stone")), Rarity.RARE, new Bag.Color(0x00fffa, 0xffffff));
        RandomBag random = new RandomBag(List.of(new RandomBag.Entry(LootBag.id("zombie"), 3), new RandomBag.Entry(LootBag.id("skeleton"), 2), new RandomBag.Entry(LootBag.id("creeper"), 1)), Rarity.EPIC, new Bag.Color(0x000000, 0xffffff));

        BAGS.put(RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("single")), single);
        BAGS.put(RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("optional")), optional);
        BAGS.put(RegistryKey.of(LootBagRegistry.BAG_KEY, LootBag.id("random")), random);
    }

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(LootBagProvider::new);
    }

    @Override
    public void buildRegistry(RegistryBuilder registryBuilder)
    {
        registryBuilder.addRegistry(LootBagRegistry.CONTENT_KEY, registerable -> CONTENTS.forEach(registerable::register));
        registryBuilder.addRegistry(LootBagRegistry.BAG_KEY, registerable -> BAGS.forEach(registerable::register));
    }
}
