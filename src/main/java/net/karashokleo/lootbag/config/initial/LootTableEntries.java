package net.karashokleo.lootbag.config.initial;

import java.util.Map;

public class LootTableEntries
{
    public Map<String, Entry> entries = Map.of(
            "cow",
            new Entry(
                    "entities/cow",
                    new Entry.Icon("textures/item/beef.png", 16, 16), "Beef", "Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"),
            "bell",
            new Entry(
                    "blocks/bell",
                    new Entry.Icon("textures/item/bell.png", 16, 16), "Bell", "Desc"),
            "ice",
            new Entry(
                    "blocks/ice",
                    new Entry.Icon("textures/block/ice.png", 16, 16), "Ice", "Desc"),
            "stone",
            new Entry(
                    "blocks/stone",
                    new Entry.Icon("textures/block/stone.png", 16, 16), "Stone", "Desc"),
            "zombie",
            new LootTableEntries.Entry(
                    "entities/zombie",
                    new LootTableEntries.Entry.Icon("textures/item/rotten_flesh.png", 16, 16), "Zombie", "Desc"),
            "creeper",
            new LootTableEntries.Entry(
                    "entities/creeper",
                    new LootTableEntries.Entry.Icon("textures/item/gunpowder.png", 16, 16), "Creeper", "Desc"),
            "skeleton",
            new LootTableEntries.Entry(
                    "entities/skeleton",
                    new LootTableEntries.Entry.Icon("textures/item/bone.png", 16, 16), "Skeleton", "Desc")
    );

    public static class Entry
    {
        public String loot_table_id;
        public Icon icon;
        public String name;
        public String description;

        public Entry(String loot_table_id, Icon icon, String name, String description)
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
}
