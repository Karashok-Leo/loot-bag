package net.karashokleo.lootbag.config;

import net.karashokleo.lootbag.LootBag;
import net.karashokleo.lootbag.config.initial.LootBagEntries;
import net.karashokleo.lootbag.config.initial.LootEntries;
import net.karashokleo.lootbag.content.LootBagEntry;
import net.karashokleo.lootbag.content.LootEntry;
import net.tinyconfig.ConfigManager;

import java.util.Arrays;
import java.util.Map;
import java.util.Objects;

public class LootBagConfig
{
    private static final ConfigManager<LootBagEntries> loot_bags = new ConfigManager<>
            ("loot_bag_entries", new LootBagEntries())
            .builder()
            .setDirectory(LootBag.MOD_ID)
            .build();
    private static final ConfigManager<LootEntries> loot_tables = new ConfigManager<>
            ("loot_table_entries", new LootEntries())
            .builder()
            .setDirectory(LootBag.MOD_ID)
            .build();

    public static Map<String, LootBagEntry> getLootBagEntries()
    {
        return loot_bags.value.entries;
    }

    public static LootEntry[] getLootTableEntries(String[] tables)
    {
        return Arrays
                .stream(tables)
                .map(s -> loot_tables.value.entries.get(s))
                .filter(Objects::nonNull)
                .toList()
                .toArray(new LootEntry[]{});
    }

    public static void init()
    {
        loot_bags.refresh();
        loot_tables.refresh();
    }
}
