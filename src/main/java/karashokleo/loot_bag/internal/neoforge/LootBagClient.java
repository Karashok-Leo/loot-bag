package karashokleo.loot_bag.internal.neoforge;

import karashokleo.loot_bag.api.client.LootBagScreenRegistry;
import karashokleo.loot_bag.api.client.screen.OptionalLootBagScreen;
import karashokleo.loot_bag.api.client.screen.RandomLootBagScreen;
import karashokleo.loot_bag.api.client.screen.SingleLootBagScreen;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = LootBagMod.LOADER_ID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class LootBagClient
{
    @SubscribeEvent
    public static void onInitializeClient(FMLClientSetupEvent event)
    {
        LootBagScreenRegistry.register(SingleBag.TYPE.get(), SingleLootBagScreen::new);
        LootBagScreenRegistry.register(OptionalBag.TYPE.get(), OptionalLootBagScreen::new);
        LootBagScreenRegistry.register(RandomBag.TYPE.get(), RandomLootBagScreen::new);
    }

    @SubscribeEvent
    public static void registerColors(RegisterColorHandlersEvent.Item event)
    {
        event.register((stack, tintIndex) -> LootBagItemRegistry.LOOT_BAG.get().getBag(stack)
                .map(bag -> 0xff000000 | bag.getColor().byTintIndex(tintIndex))
                .orElse(0xff000000 | tintIndex * 0xffffff), LootBagItemRegistry.LOOT_BAG.get());
    }
}
