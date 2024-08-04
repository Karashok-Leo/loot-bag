package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.bag.*;
import karashokleo.loot_bag.api.common.content.*;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint;
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.ArrayList;
import java.util.List;

public class LootBagDataGenerator implements DataGeneratorEntrypoint
{
    public static final List<ContentEntry> CONTENTS = new ArrayList<>();
    public static final List<BagEntry> BAGS = new ArrayList<>();

    @Override
    public void onInitializeDataGenerator(FabricDataGenerator generator)
    {
        bootstrap();
        FabricDataGenerator.Pack pack = generator.createPack();
        pack.addProvider(LanguageProvider::new);
        pack.addProvider(ContentProvider::new);
        pack.addProvider(BagProvider::new);
    }

    private static void bootstrap()
    {
        ItemStack diamondSwordStack = Items.DIAMOND_SWORD.getDefaultStack();
        diamondSwordStack.setDamage(66);

        ContentEntry beef = new ContentEntry(
                LootBagMod.id("beef"),
                new ItemContent(
                        Items.BEEF.getDefaultStack(),
                        new Content.Icon(Items.BEEF)
                )
        );
        ContentEntry diamondSword = new ContentEntry(
                LootBagMod.id("diamond_sword"),
                new ItemContent(
                        diamondSwordStack,
                        new Content.Icon(Items.DIAMOND_SWORD)
                )
        );
        ContentEntry stone = new ContentEntry(
                LootBagMod.id("stone"),
                new LootTableContent(
                        new Identifier("blocks/stone"),
                        new Content.Icon(new Identifier("textures/block/stone.png"))
                )
        );
        ContentEntry effect = new ContentEntry(
                LootBagMod.id("effect"),
                new EffectContent(
                        List.of(
                                new EffectContent.Effect(StatusEffects.ABSORPTION, 2400),
                                new EffectContent.Effect(StatusEffects.REGENERATION, 100, 1)
                        ),
                        new Content.Icon(Items.GOLDEN_APPLE)
                )
        );
        ContentEntry zombie = new ContentEntry(
                LootBagMod.id("zombie"),
                new LootTableContent(
                        new Identifier("entities/zombie"),
                        new Content.Icon(new Identifier("rotten_flesh"), 16, 16)
                )
        );
        ContentEntry skeleton = new ContentEntry(
                LootBagMod.id("skeleton"),
                new CommandContent(
                        "/kill @s",
                        new Content.Icon(new Identifier("textures/item/bone.png"))
                )
        );
        ContentEntry creeper = new ContentEntry(
                LootBagMod.id("creeper"),
                new CommandContent(
                        "/summon minecraft:creeper ~ ~ ~",
                        new Content.Icon(new Identifier("textures/item/gunpowder.png"))
                )
        );

        BagEntry single = new BagEntry(
                LootBagMod.id("single"),
                new SingleBag(
                        beef,
                        Rarity.COMMON,
                        new Bag.Color(0xFFDA76, 0xFF4E88)
                )
        );
        BagEntry optional = new BagEntry(
                LootBagMod.id("optional"),
                new OptionalBag(
                        List.of(diamondSword, stone, effect),
                        Rarity.RARE,
                        new Bag.Color(0x28DF99, 0x493323)
                )
        );
        BagEntry random = new BagEntry(
                LootBagMod.id("random"),
                new RandomBag(
                        List.of(
                                new RandomBag.Entry(zombie, 3),
                                new RandomBag.Entry(skeleton, 2),
                                new RandomBag.Entry(creeper, 1)
                        ),
                        Rarity.EPIC,
                        new Bag.Color(0xAF47D2, 0xFFDB00)
                )
        );

        CONTENTS.add(beef);
        CONTENTS.add(diamondSword);
        CONTENTS.add(stone);
        CONTENTS.add(effect);
        CONTENTS.add(zombie);
        CONTENTS.add(skeleton);
        CONTENTS.add(creeper);
        BAGS.add(single);
        BAGS.add(optional);
        BAGS.add(random);
    }
}
