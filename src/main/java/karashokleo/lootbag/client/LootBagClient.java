package karashokleo.lootbag.client;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;
import karashokleo.lootbag.content.LootBagItems;
import karashokleo.lootbag.network.ClientNetwork;

public class LootBagClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        LootBagScreenRegistry.register();
        ClientNetwork.init();
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> LootBagItems.LOOT_BAG.getBagEntry(stack).map(bag -> bag.getColor().byTintIndex(tintIndex)).orElse(0xffffff), LootBagItems.LOOT_BAG);
    }
}
