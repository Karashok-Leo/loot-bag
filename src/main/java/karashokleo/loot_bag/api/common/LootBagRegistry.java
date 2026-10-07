package karashokleo.loot_bag.api.common;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagType;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentType;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.api.common.icon.IconType;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;

@SuppressWarnings("all")
public class LootBagRegistry
{
    public static final ResourceKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("content_type"));
    public static final ResourceKey<Registry<BagType<?>>> BAG_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("bag_type"));
    public static final ResourceKey<Registry<IconType<?>>> ICON_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("icon_type"));

    public static final Registry<ContentType<?>> CONTENT_TYPE_REGISTRY = new RegistryBuilder<>(CONTENT_TYPE_KEY).sync(true).create();
    public static final Registry<BagType<?>> BAG_TYPE_REGISTRY = new RegistryBuilder<>(BAG_TYPE_KEY).sync(true).create();
    public static final Registry<IconType<?>> ICON_TYPE_REGISTRY = new RegistryBuilder<>(ICON_TYPE_KEY).sync(true).create();

    public static void registerRegistries(NewRegistryEvent event)
    {
        event.register(CONTENT_TYPE_REGISTRY);
        event.register(BAG_TYPE_REGISTRY);
        event.register(ICON_TYPE_REGISTRY);
    }

    public static <T extends Content> ContentType<T> registerContentType(ResourceLocation id, ContentType<T> type)
    {
        return Registry.register(CONTENT_TYPE_REGISTRY, id, type);
    }

    public static <T extends Bag> BagType<T> registerBagType(ResourceLocation id, BagType<T> type)
    {
        return Registry.register(BAG_TYPE_REGISTRY, id, type);
    }

    public static <T extends Icon> IconType<T> registerIconType(ResourceLocation id, IconType<T> type)
    {
        return Registry.register(ICON_TYPE_REGISTRY, id, type);
    }
}
