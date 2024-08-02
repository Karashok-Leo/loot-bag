package karashokleo.loot_bag.internal.fabric;

import karashokleo.loot_bag.api.client.LootBagScreenRegistry;
import karashokleo.loot_bag.api.client.screen.OptionalLootBagScreen;
import karashokleo.loot_bag.api.client.screen.RandomLootBagScreen;
import karashokleo.loot_bag.api.client.screen.SingleLootBagScreen;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.ClientNetwork;
import net.fabricmc.api.ClientModInitializer;
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

        ColorProviderRegistry.ITEM.register((stack, tintIndex) -> LootBagItemRegistry.LOOT_BAG.getBag(stack).map(bag -> bag.getColor().byTintIndex(tintIndex)).orElse(tintIndex * 0xffffff), LootBagItemRegistry.LOOT_BAG);
    }
}
