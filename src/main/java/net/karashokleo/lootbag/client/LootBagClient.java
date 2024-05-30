package net.karashokleo.lootbag.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.karashokleo.lootbag.content.LootBagItem;
import net.karashokleo.lootbag.content.LootBagItems;
import net.karashokleo.lootbag.network.ClientNetwork;

public class LootBagClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientNetwork.init();
        for (LootBagItem item : LootBagItems.itemList)
            ColorProviderRegistry.ITEM.register((stack, tintIndex) -> item.color.byTintIndex(tintIndex), item);
    }
}
