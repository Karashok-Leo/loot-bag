package net.karashokleo.lootbag.config;

import net.minecraft.util.Identifier;
import net.tinyconfig.ConfigManager;

import java.util.Map;

public class LootBagConfig
{
    private static final ConfigManager<DefaultConfig> bags = new ConfigManager<>
            ("loot_bag", new DefaultConfig())
            .builder()
            .setDirectory("./")
            .enableLogging(true)
            .sanitize(true)
            .build();

    public static Map<String, DefaultConfig.Entry> getBagEntries()
    {
        return bags.value.entries;
    }

    public static void init()
    {
        bags.refresh();
    }
}
