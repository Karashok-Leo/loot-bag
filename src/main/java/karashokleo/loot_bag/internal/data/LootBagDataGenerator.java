package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.bag.*;
import karashokleo.loot_bag.api.common.content.*;
import karashokleo.loot_bag.api.common.icon.ItemIcon;
import karashokleo.loot_bag.api.common.icon.TextureIcon;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.neoforged.neoforge.data.event.GatherDataEvent;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.metadata.PackMetadataGenerator;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.network.chat.Component;
import net.minecraft.util.InclusiveRange;
import java.util.Optional;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Rarity;

import java.util.ArrayList;
import java.util.List;

public class LootBagDataGenerator
{
    public static final List<ContentEntry> CONTENTS = new ArrayList<>();
    public static final List<BagEntry> BAGS = new ArrayList<>();

    public static void gatherData(GatherDataEvent event)
    {
        var ready = event.getLookupProvider().thenApply(registries ->
        {
            bootstrap(registries);
            return registries;
        });
        var generator = event.getGenerator();
        var output = generator.getPackOutput();
        generator.addProvider(true, new PackMetadataGenerator(output).add(PackMetadataSection.TYPE,
                new PackMetadataSection(Component.literal("Loot Bag Example"), 48,
                        Optional.of(new InclusiveRange<>(34, 48)))));
        generator.addProvider(event.includeClient(), new LanguageProvider(output)
        {
            @Override
            public java.util.concurrent.CompletableFuture<?> run(net.minecraft.data.CachedOutput cache)
            {
                return ready.thenCompose(registries -> super.run(cache));
            }
        });
        generator.addProvider(event.includeServer(), new ContentProvider(output, ready));
        generator.addProvider(event.includeServer(), new BagProvider(output, ready));
    }

    private static void bootstrap(HolderLookup.Provider registries)
    {
        ContentEntry beef = new ContentEntry(
                LootBagMod.id("beef"),
                new ItemContent(
                        Items.BEEF.getDefaultInstance(),
                        new ItemIcon(Items.BEEF.getDefaultInstance(), 0.5F)
                )
        );
        ItemStack contentDiamondSword = Items.DIAMOND_SWORD.getDefaultInstance();
        contentDiamondSword.setDamageValue(66);
        ItemStack iconDiamondSword = Items.DIAMOND_SWORD.getDefaultInstance();
        iconDiamondSword.enchant(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.LOOTING), 3);
        ContentEntry diamondSword = new ContentEntry(
                LootBagMod.id("diamond_sword"),
                new ItemContent(
                        contentDiamondSword,
                        new ItemIcon(iconDiamondSword, 1)
                )
        );
        ContentEntry stone = new ContentEntry(
                LootBagMod.id("stone"),
                new LootTableContent(
                        ResourceLocation.parse("blocks/stone"),
                        new ItemIcon(Items.STONE)
                )
        );
        ContentEntry effect = new ContentEntry(
                LootBagMod.id("effect"),
                new EffectContent(
                        List.of(
                                new EffectContent.Effect(MobEffects.ABSORPTION, 2400),
                                new EffectContent.Effect(MobEffects.REGENERATION, 100, 1)
                        ),
                        new TextureIcon(ResourceLocation.parse("textures/block/stone.png"))
                )
        );
        ContentEntry zombie = new ContentEntry(
                LootBagMod.id("zombie"),
                new LootTableContent(
                        ResourceLocation.parse("entities/zombie"),
                        new TextureIcon(ResourceLocation.parse("textures/item/rotten_flesh.png"), 0.25F, 0.25F, 0.75F, 0.75F)
                )
        );
        ContentEntry skeleton = new ContentEntry(
                LootBagMod.id("skeleton"),
                new CommandContent(
                        "/kill @s",
                        new TextureIcon(ResourceLocation.parse("textures/item/bone.png"))
                )
        );
        ContentEntry creeper = new ContentEntry(
                LootBagMod.id("creeper"),
                new CommandContent(
                        "/summon minecraft:creeper ~ ~ ~",
                        new TextureIcon(ResourceLocation.parse("textures/item/gunpowder.png"))
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
