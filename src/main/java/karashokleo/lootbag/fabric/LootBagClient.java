package karashokleo.lootbag.fabric;

import karashokleo.lootbag.api.client.LootBagScreenRegistry;
import karashokleo.lootbag.api.client.screen.OptionalLootBagScreen;
import karashokleo.lootbag.api.client.screen.RandomLootBagScreen;
import karashokleo.lootbag.api.client.screen.SingleLootBagScreen;
import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.LootBagRegistryKeys;
import karashokleo.lootbag.api.common.bag.OptionalBag;
import karashokleo.lootbag.api.common.bag.RandomBag;
import karashokleo.lootbag.api.common.bag.SingleBag;
import karashokleo.lootbag.item.LootBagItemRegistry;
import karashokleo.lootbag.network.ClientNetwork;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.fabricmc.fabric.api.client.rendering.v1.ColorProviderRegistry;

public class LootBagClient implements ClientModInitializer
{
    @Override
    public void onInitializeClient()
    {
        LootBagScreenRegistry.register(SingleBag.TYPE, SingleLootBagScreen::new);
        LootBagScreenRegistry.register(OptionalBag.TYPE, OptionalLootBagScreen::new);
        LootBagScreenRegistry.register(RandomBag.TYPE, RandomLootBagScreen::new);

        ClientNetwork.init();
        ClientPlayConnectionEvents.JOIN.register((handler, sender, client) ->
        {
            LootBagRegistry.CONTENT_REGISTRY = handler.getRegistryManager().get(LootBagRegistryKeys.CONTENT_KEY);
            LootBagRegistry.BAG_REGISTRY = handler.getRegistryManager().get(LootBagRegistryKeys.BAG_KEY);
        });
        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> LootBagItemRegistry.LOOT_BAG.getBagEntry(stack).map(bag -> bag.getColor().byTintIndex(tintIndex)).orElse(tintIndex * 0xffffff), LootBagItemRegistry.LOOT_BAG);
    }
}
