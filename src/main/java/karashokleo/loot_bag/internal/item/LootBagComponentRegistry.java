package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.component.ComponentType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class LootBagComponentRegistry
{
    public static final ComponentType<Identifier> BAG_ID = ComponentType.<Identifier>builder()
            .codec(Identifier.CODEC)
            .packetCodec(Identifier.PACKET_CODEC)
            .build();

    public static void init()
    {
        Registry.register(Registries.DATA_COMPONENT_TYPE, LootBagMod.id("bag_id"), BAG_ID);
    }
}
