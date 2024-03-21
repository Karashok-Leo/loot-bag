package net.karashokleo.lootbag.config;

import net.karashokleo.lootbag.content.LootBagType;
import net.minecraft.util.Rarity;

import java.util.Map;

public class DefaultConfig
{
    public Map<String, Entry> entries = Map.of(
            "loot-bag:example_single",
            new Entry(
                    "Loot Bag",
                    LootBagType.SINGLE,
                    new Entry.LootEntry[]{
                            new Entry.LootEntry(
                                    "entities/cow",
                                    new Entry.LootEntry.Icon("textures/item/beef.png", 16, 16), "Beef", "Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong")
                    },
                    Rarity.COMMON,
                    1,
                    new Entry.Color(0x000000, 0x8400ff, 0xffffff)
            ),
            "loot-bag:example_optional",
            new Entry(
                    "Optional Loot Bag",
                    LootBagType.OPTIONAL,
                    new Entry.LootEntry[]{
                            new Entry.LootEntry(
                                    "blocks/bell",
                                    new Entry.LootEntry.Icon("textures/item/bell.png", 16, 16), "Bell", "Desc"),
                            new Entry.LootEntry(
                                    "blocks/ice",
                                    new Entry.LootEntry.Icon("textures/block/ice.png", 16, 16), "Ice", "Desc"),
                            new Entry.LootEntry(
                                    "blocks/stone",
                                    new Entry.LootEntry.Icon("textures/block/stone.png", 16, 16), "Stone", "Desc"),
                    },
                    Rarity.RARE,
                    16,
                    new Entry.Color(0x00fffa, 0x00ff9a, 0xffffff)
            ),
            "loot-bag:example_random",
            new Entry(
                    "Random Loot Bag",
                    LootBagType.RANDOM,
                    new Entry.LootEntry[]{
                            new Entry.LootEntry(
                                    "entities/zombie",
                                    new Entry.LootEntry.Icon("textures/item/rotten_flesh.png", 16, 16), "Zombie", "Desc"),
                            new Entry.LootEntry(
                                    "entities/creeper",
                                    new Entry.LootEntry.Icon("textures/item/gunpowder.png", 16, 16), "Creeper", "Desc"),
                            new Entry.LootEntry(
                                    "entities/skeleton",
                                    new Entry.LootEntry.Icon("textures/item/bone.png", 16, 16), "Skeleton", "Desc")
                    },
                    Rarity.EPIC,
                    64,
                    new Entry.Color(0x000000, 0xff0000, 0xffffff)
            )
    );

    public static class Entry
    {
        public String name;
        public LootBagType type;
        public LootEntry[] loot_tables;
        public Rarity rarity;
        public int stack;
        public Color color;

        public Entry(String name, LootBagType type, LootEntry[] loot_tables, Rarity rarity, int stack, Color color)
        {
            this.name = name;
            this.type = type;
            this.loot_tables = loot_tables;
            this.rarity = rarity;
            this.stack = stack;
            this.color = color;
        }

        public static class LootEntry
        {
            public String loot_table_id;
            public Icon icon;
            public String name;
            public String description;

            public LootEntry(String loot_table_id, Icon icon, String name, String description)
            {
                this.loot_table_id = loot_table_id;
                this.icon = icon;
                this.name = name;
                this.description = description;
            }

            public static class Icon
            {
                public String texture;
                public int width;
                public int height;

                public Icon(String texture, int width, int height)
                {
                    this.texture = texture;
                    this.width = width;
                    this.height = height;
                }
            }
        }

        public static class Color
        {
            int bag_body;
            int bag_overlay;
            int bag_string;

            public Color(int bag_body, int bag_overlay, int bag_string)
            {
                this.bag_body = bag_body;
                this.bag_overlay = bag_overlay;
                this.bag_string = bag_string;
            }

            public int byTintIndex(int tintIndex)
            {
                return switch (tintIndex)
                {
                    case 0 -> bag_body;
                    case 1 -> bag_overlay;
                    case 2 -> bag_string;
                    default -> bag_body;
                };
            }
        }
    }
}
