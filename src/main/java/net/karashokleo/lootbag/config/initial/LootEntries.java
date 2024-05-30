package net.karashokleo.lootbag.config.initial;

import net.karashokleo.lootbag.content.LootEntry;
import net.karashokleo.lootbag.content.LootType;

import java.util.Map;

public class LootEntries
{
    public Map<String, LootEntry> entries = Map.of(
            "cow",
            new LootEntry(
                    LootType.ITEM,
                    "minecraft:beef",
                    new LootEntry.Icon("textures/item/beef.png", 16, 16), "Beef", new String[]{"Loooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooooong"}),
            "bell",
            new LootEntry(
                    LootType.ITEM,
                    "minecraft:bell",
                    new LootEntry.Icon("textures/item/bell.png", 16, 16), "Bell", new String[]{"Desc"}),
            "ice",
            new LootEntry(
                    LootType.LOOT_TABLE,
                    "blocks/ice",
                    new LootEntry.Icon("textures/block/ice.png", 16, 16), "Ice", new String[]{"Desc"}),
            "stone",
            new LootEntry(
                    LootType.LOOT_TABLE,
                    "blocks/stone",
                    new LootEntry.Icon("textures/block/stone.png", 16, 16), "Stone", new String[]{"Desc"}),
            "zombie",
            new LootEntry(
                    LootType.LOOT_TABLE,
                    "entities/zombie",
                    new LootEntry.Icon("textures/item/rotten_flesh.png", 16, 16), "Zombie", new String[]{"Desc"}),
            "creeper",
            new LootEntry(
                    LootType.COMMAND,
                    "/summon minecraft:creeper ~ ~ ~",
                    new LootEntry.Icon("textures/item/gunpowder.png", 16, 16), "Creeper", new String[]{"Desc"}),
            "skeleton",
            new LootEntry(
                    LootType.COMMAND,
                    "/kill",
                    new LootEntry.Icon("textures/item/bone.png", 16, 16), "Skeleton", new String[]{"Desc"})
    );

}
