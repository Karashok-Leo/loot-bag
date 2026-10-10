package karashokleo.loot_bag.api.common;

import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagType;
import karashokleo.loot_bag.api.common.content.ItemContent;
import karashokleo.loot_bag.api.common.content.LootTableContent;
import karashokleo.loot_bag.api.common.content.CommandContent;
import karashokleo.loot_bag.api.common.content.EffectContent;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentType;
import karashokleo.loot_bag.api.common.icon.ItemIcon;
import karashokleo.loot_bag.api.common.icon.TextureIcon;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.api.common.icon.IconType;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.neoforged.neoforge.registries.NewRegistryEvent;
import net.neoforged.neoforge.registries.RegistryBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.function.Supplier;

@SuppressWarnings("all")
public class LootBagRegistry
{
    public static final ResourceKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("content_type"));
    public static final ResourceKey<Registry<BagType<?>>> BAG_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("bag_type"));
    public static final ResourceKey<Registry<IconType<?>>> ICON_TYPE_KEY = ResourceKey.createRegistryKey(LootBagMod.id("icon_type"));

    public static final Registry<ContentType<?>> CONTENT_TYPE_REGISTRY = new RegistryBuilder<>(CONTENT_TYPE_KEY).sync(true).create();
    public static final Registry<BagType<?>> BAG_TYPE_REGISTRY = new RegistryBuilder<>(BAG_TYPE_KEY).sync(true).create();
    public static final Registry<IconType<?>> ICON_TYPE_REGISTRY = new RegistryBuilder<>(ICON_TYPE_KEY).sync(true).create();

    public static final DeferredRegister<ContentType<?>> CONTENT_TYPES = DeferredRegister.create(CONTENT_TYPE_KEY, LootBagMod.MOD_ID);
    public static final DeferredRegister<BagType<?>> BAG_TYPES = DeferredRegister.create(BAG_TYPE_KEY, LootBagMod.MOD_ID);
    public static final DeferredRegister<IconType<?>> ICON_TYPES = DeferredRegister.create(ICON_TYPE_KEY, LootBagMod.MOD_ID);

    // Keep codec-bearing classes uninitialized until the custom registries exist.
    public static final DeferredHolder<ContentType<?>, ContentType<ItemContent>> ITEM_CONTENT = registerContentType("item", () -> new ContentType<>(ItemContent.CODEC));
    public static final DeferredHolder<ContentType<?>, ContentType<LootTableContent>> LOOT_TABLE_CONTENT = registerContentType("loot_table", () -> new ContentType<>(LootTableContent.CODEC));
    public static final DeferredHolder<ContentType<?>, ContentType<CommandContent>> COMMAND_CONTENT = registerContentType("command", () -> new ContentType<>(CommandContent.CODEC));
    public static final DeferredHolder<ContentType<?>, ContentType<EffectContent>> EFFECT_CONTENT = registerContentType("effect", () -> new ContentType<>(EffectContent.CODEC));
    public static final DeferredHolder<BagType<?>, BagType<SingleBag>> SINGLE_BAG = registerBagType("single", () -> new BagType<>(SingleBag.CODEC, true));
    public static final DeferredHolder<BagType<?>, BagType<OptionalBag>> OPTIONAL_BAG = registerBagType("optional", () -> new BagType<>(OptionalBag.CODEC, false));
    public static final DeferredHolder<BagType<?>, BagType<RandomBag>> RANDOM_BAG = registerBagType("random", () -> new BagType<>(RandomBag.CODEC, true));
    public static final DeferredHolder<IconType<?>, IconType<ItemIcon>> ITEM_ICON = registerIconType("item", () -> new IconType<>(ItemIcon.CODEC));
    public static final DeferredHolder<IconType<?>, IconType<TextureIcon>> TEXTURE_ICON = registerIconType("texture", () -> new IconType<>(TextureIcon.CODEC));

    public static void init(IEventBus bus)
    {
        bus.addListener(LootBagRegistry::registerRegistries);
        CONTENT_TYPES.register(bus);
        BAG_TYPES.register(bus);
        ICON_TYPES.register(bus);
    }

    public static void registerRegistries(NewRegistryEvent event)
    {
        event.register(CONTENT_TYPE_REGISTRY);
        event.register(BAG_TYPE_REGISTRY);
        event.register(ICON_TYPE_REGISTRY);
    }

    public static <T extends Content> DeferredHolder<ContentType<?>, ContentType<T>> registerContentType(String name, Supplier<ContentType<T>> type)
    {
        return CONTENT_TYPES.register(name, type);
    }

    public static <T extends Bag> DeferredHolder<BagType<?>, BagType<T>> registerBagType(String name, Supplier<BagType<T>> type)
    {
        return BAG_TYPES.register(name, type);
    }

    public static <T extends Icon> DeferredHolder<IconType<?>, IconType<T>> registerIconType(String name, Supplier<IconType<T>> type)
    {
        return ICON_TYPES.register(name, type);
    }
}
