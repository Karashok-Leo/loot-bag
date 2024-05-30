package net.karashokleo.lootbag.content;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.karashokleo.lootbag.LootBag;
import net.karashokleo.lootbag.config.LootBagConfig;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.*;

public class LootBagItems
{
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, LootBag.id("loot_bag"));
    public static List<LootBagItem> itemList = new ArrayList<>();

    public static void init()
    {
        registerLootBags();
        registerItemGroups();
    }

    public static void registerLootBags()
    {
        for (Map.Entry<String, LootBagEntry> entry : LootBagConfig.getLootBagEntries().entrySet())
        {
            LootBagItem item = new LootBagItem(
                    entry.getValue().type,
                    entry.getValue().color,
                    LootBagConfig.getLootTableEntries(entry.getValue().loot_tables),
                    entry.getValue().stack,
                    entry.getValue().rarity
            );
            Registry.register(Registries.ITEM, new Identifier(entry.getKey()), item);
            itemList.add(item);
        }
    }

    public static void registerItemGroups()
    {
        ItemStack iconStack = itemList.isEmpty() ? ItemStack.EMPTY : itemList.get(0).getDefaultStack();
        Registry.register(Registries.ITEM_GROUP, LootBag.id("loot_bag"), FabricItemGroup.builder().icon(() -> iconStack).displayName(Text.translatable("itemGroup.loot-bag.loot_bag")).build());
        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(entries -> itemList.forEach(entries::add));
    }
}
