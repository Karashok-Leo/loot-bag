package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class LootBagComponentRegistry
{
    private static final DeferredRegister.DataComponents COMPONENTS = DeferredRegister.createDataComponents(Registries.DATA_COMPONENT_TYPE, LootBagMod.MOD_ID);
    public static final DeferredHolder<DataComponentType<?>, DataComponentType<ResourceLocation>> BAG_ID = COMPONENTS.registerComponentType(
            "bag_id", builder -> builder
                    .persistent(ResourceLocation.CODEC)
                    .networkSynchronized(ResourceLocation.STREAM_CODEC)
    );

    public static void init(IEventBus bus)
    {
        COMPONENTS.register(bus);
    }
}
