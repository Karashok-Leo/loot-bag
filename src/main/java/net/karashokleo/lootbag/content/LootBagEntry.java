package net.karashokleo.lootbag.content;

import net.karashokleo.lootbag.config.initial.LootBagEntries;
import net.minecraft.util.Rarity;

public class LootBagEntry
{
    public LootBagType type;
    public String[] loot_tables;
    public Rarity rarity;
    public int stack;
    public Color color;

    public LootBagEntry(LootBagType type, String[] loot_tables, Rarity rarity, int stack, Color color)
    {
        this.type = type;
        this.loot_tables = loot_tables;
        this.rarity = rarity;
        this.stack = stack;
        this.color = color;
    }

    public static class Color
    {
        public int bag_body;
        public int bag_string;

        public Color(int bag_body, int bag_string)
        {
            this.bag_body = bag_body;
            this.bag_string = bag_string;
        }

        public int byTintIndex(int tintIndex)
        {
            return tintIndex == 0 ? bag_body : bag_string;
        }
    }
}
