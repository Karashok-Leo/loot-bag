package net.karashokleo.lootbag.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import net.karashokleo.lootbag.content.LootBagItems;
import net.karashokleo.lootbag.network.ClientNetwork;

public class LootBagClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        ClientNetwork.init();
        LootBagItems.entryMap.forEach((item, entry) -> ColorProviderRegistry.ITEM.register((stack, tintIndex) -> entry.color.byTintIndex(tintIndex),item));
    }
}
