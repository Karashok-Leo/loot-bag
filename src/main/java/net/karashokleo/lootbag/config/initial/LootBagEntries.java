package net.karashokleo.lootbag.config.initial;

import net.karashokleo.lootbag.content.LootBagType;
import net.minecraft.util.Rarity;

import java.util.Map;

public class LootBagEntries
{
    public Map<String, Entry> entries = Map.of(
            "loot-bag:example_single",
            new Entry(
                    "Loot Bag",
                    LootBagType.SINGLE,
                    new String[]{"cow"},
                    Rarity.COMMON,
                    1,
                    new Entry.Color(0x000000, 0x8400ff, 0xffffff)
            ),
            "loot-bag:example_optional",
            new Entry(
                    "Optional Loot Bag",
                    LootBagType.OPTIONAL,
                    new String[]{"bell", "ice", "stone"},
                    Rarity.RARE,
                    16,
                    new Entry.Color(0x00fffa, 0x00ff9a, 0xffffff)
            ),
            "loot-bag:example_random",
            new Entry(
                    "Random Loot Bag",
                    LootBagType.RANDOM,
                    new String[]{"zombie", "creeper", "skeleton"},
                    Rarity.EPIC,
                    64,
                    new Entry.Color(0x000000, 0xff0000, 0xffffff)
            )
    );

    public static class Entry
    {
        public String name;
        public LootBagType type;
        public String[] loot_tables;
        public Rarity rarity;
        public int stack;
        public Color color;

        public Entry(String name, LootBagType type, String[] loot_tables, Rarity rarity, int stack, Color color)
        {
            this.name = name;
            this.type = type;
            this.loot_tables = loot_tables;
            this.rarity = rarity;
            this.stack = stack;
            this.color = color;
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
                    case 1 -> bag_overlay;
                    case 2 -> bag_string;
                    default -> bag_body;
                };
            }
        }
    }
}
