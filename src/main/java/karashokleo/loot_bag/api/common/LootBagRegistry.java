package karashokleo.loot_bag.api.common;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagType;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentType;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("all")
public class LootBagRegistry
{
    public static final RegistryKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("content_type"));
    public static final RegistryKey<Registry<BagType<?>>> BAG_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("bag_type"));
    public static final Registry<ContentType<?>> CONTENT_TYPE_REGISTRY = FabricRegistryBuilder
            .createSimple(LootBagRegistry.CONTENT_TYPE_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();
    public static final Registry<BagType<?>> BAG_TYPE_REGISTRY = FabricRegistryBuilder
            .createSimple(LootBagRegistry.BAG_TYPE_KEY)
            .attribute(RegistryAttribute.SYNCED)
            .buildAndRegister();

    public static <T extends Content> ContentType<T> registerContentType(Identifier id, ContentType<T> type)
    {
        return Registry.register(CONTENT_TYPE_REGISTRY, id, type);
    }

    public static <T extends Bag> BagType<T> registerBagType(Identifier id, BagType<T> type)
    {
        return Registry.register(BAG_TYPE_REGISTRY, id, type);
    }
}
