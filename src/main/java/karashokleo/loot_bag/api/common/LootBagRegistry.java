package karashokleo.loot_bag.api.common;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagType;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentType;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.api.common.icon.IconType;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.registry.Registries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("all")
public class LootBagRegistry
{
    public static final RegistryKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("content_type"));
    public static final RegistryKey<Registry<BagType<?>>> BAG_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("bag_type"));
    public static final RegistryKey<Registry<IconType<?>>> ICON_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("icon_type"));

    // Forge creates synced registry wrappers during NewRegistryEvent, before registration.
    public static Registry<ContentType<?>> CONTENT_TYPE_REGISTRY;
    public static Registry<BagType<?>> BAG_TYPE_REGISTRY;
    public static Registry<IconType<?>> ICON_TYPE_REGISTRY;

    public static void createRegistries(NewRegistryEvent event)
    {
        event.create(new RegistryBuilder<ContentType<?>>().setName(CONTENT_TYPE_KEY.getValue()).hasTags(),
                registry -> CONTENT_TYPE_REGISTRY = (Registry<ContentType<?>>) Registries.REGISTRIES.get(CONTENT_TYPE_KEY.getValue()));
        event.create(new RegistryBuilder<BagType<?>>().setName(BAG_TYPE_KEY.getValue()).hasTags(),
                registry -> BAG_TYPE_REGISTRY = (Registry<BagType<?>>) Registries.REGISTRIES.get(BAG_TYPE_KEY.getValue()));
        event.create(new RegistryBuilder<IconType<?>>().setName(ICON_TYPE_KEY.getValue()).hasTags(),
                registry -> ICON_TYPE_REGISTRY = (Registry<IconType<?>>) Registries.REGISTRIES.get(ICON_TYPE_KEY.getValue()));
    }

    public static <T extends Content> ContentType<T> registerContentType(Identifier id, ContentType<T> type)
    {
        return Registry.register(CONTENT_TYPE_REGISTRY, id, type);
    }

    public static <T extends Bag> BagType<T> registerBagType(Identifier id, BagType<T> type)
    {
        return Registry.register(BAG_TYPE_REGISTRY, id, type);
    }

    public static <T extends Icon> IconType<T> registerIconType(Identifier id, IconType<T> type)
    {
        return Registry.register(ICON_TYPE_REGISTRY, id, type);
    }
}
