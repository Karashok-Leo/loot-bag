package karashokleo.loot_bag.internal.fabric;

import karashokleo.loot_bag.api.client.LootBagScreenRegistry;
import karashokleo.loot_bag.api.client.screen.OptionalLootBagScreen;
import karashokleo.loot_bag.api.client.screen.RandomLootBagScreen;
import karashokleo.loot_bag.api.client.screen.SingleLootBagScreen;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterColorHandlersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(modid = LootBagMod.FORGE_MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
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
        event.register((stack, tintIndex) -> LootBagItemRegistry.LOOT_BAG.get().getBag(stack).map(bag -> bag.getColor().byTintIndex(tintIndex)).orElse(tintIndex * 0xffffff), LootBagItemRegistry.LOOT_BAG.get());
    }
}
