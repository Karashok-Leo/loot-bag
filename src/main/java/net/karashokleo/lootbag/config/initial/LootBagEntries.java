package net.karashokleo.lootbag.config.initial;

import net.karashokleo.lootbag.content.LootBagEntry;
import net.karashokleo.lootbag.content.LootBagType;
import net.minecraft.util.Rarity;

import java.util.Map;

public class LootBagEntries
{
    public Map<String, LootBagEntry> entries = Map.of(
            "loot-bag:example_single",
            new LootBagEntry(
                    LootBagType.SINGLE,
                    new String[]{"cow"},
                    Rarity.COMMON,
                    1,
                    new LootBagEntry.Color(0x000000, 0xffffff)
            ),
            "loot-bag:example_optional",
            new LootBagEntry(
                    LootBagType.OPTIONAL,
                    new String[]{"bell", "ice", "stone"},
                    Rarity.RARE,
                    16,
                    new LootBagEntry.Color(0x00fffa, 0xffffff)
            ),
            "loot-bag:example_random",
            new LootBagEntry(
                    LootBagType.RANDOM,
                    new String[]{"zombie", "creeper", "skeleton"},
                    Rarity.EPIC,
                    64,
                    new LootBagEntry.Color(0x000000, 0xffffff)
            )
    );

}
