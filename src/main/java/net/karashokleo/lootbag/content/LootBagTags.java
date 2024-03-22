package net.karashokleo.lootbag.content;

import net.karashokleo.lootbag.LootBag;
import net.minecraft.item.Item;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagBuilder;
import net.minecraft.registry.tag.TagKey;

import java.util.Map;

public class LootBagTags
{
    public static final Map<LootBagType, TagKey<Item>> tagMap = Map.of(
            LootBagType.SINGLE, of("single"),
            LootBagType.OPTIONAL, of("optional"),
            LootBagType.RANDOM, of("random")
    );
    public static final Map<LootBagType, TagBuilder> tagBuilderMap = Map.of(
            LootBagType.SINGLE, TagBuilder.create(),
            LootBagType.OPTIONAL, TagBuilder.create(),
            LootBagType.RANDOM, TagBuilder.create()
    );

    private static TagKey<Item> of(String path)
    {
        return TagKey.of(RegistryKeys.ITEM, LootBag.id(path));
    }
}
