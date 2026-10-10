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
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.registry.Registries;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;

import java.util.function.Supplier;

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

    public static final DeferredRegister<ContentType<?>> CONTENT_TYPES = DeferredRegister.create(CONTENT_TYPE_KEY, LootBagMod.MOD_ID);
    public static final DeferredRegister<BagType<?>> BAG_TYPES = DeferredRegister.create(BAG_TYPE_KEY, LootBagMod.MOD_ID);
    public static final DeferredRegister<IconType<?>> ICON_TYPES = DeferredRegister.create(ICON_TYPE_KEY, LootBagMod.MOD_ID);

    // Keep codec-bearing classes uninitialized until the custom registries exist.
    public static final RegistryObject<ContentType<ItemContent>> ITEM_CONTENT = registerContentType("item", () -> new ContentType<>(ItemContent.CODEC));
    public static final RegistryObject<ContentType<LootTableContent>> LOOT_TABLE_CONTENT = registerContentType("loot_table", () -> new ContentType<>(LootTableContent.CODEC));
    public static final RegistryObject<ContentType<CommandContent>> COMMAND_CONTENT = registerContentType("command", () -> new ContentType<>(CommandContent.CODEC));
    public static final RegistryObject<ContentType<EffectContent>> EFFECT_CONTENT = registerContentType("effect", () -> new ContentType<>(EffectContent.CODEC));
    public static final RegistryObject<BagType<SingleBag>> SINGLE_BAG = registerBagType("single", () -> new BagType<>(SingleBag.CODEC, true));
    public static final RegistryObject<BagType<OptionalBag>> OPTIONAL_BAG = registerBagType("optional", () -> new BagType<>(OptionalBag.CODEC, false));
    public static final RegistryObject<BagType<RandomBag>> RANDOM_BAG = registerBagType("random", () -> new BagType<>(RandomBag.CODEC, true));
    public static final RegistryObject<IconType<ItemIcon>> ITEM_ICON = registerIconType("item", () -> new IconType<>(ItemIcon.CODEC));
    public static final RegistryObject<IconType<TextureIcon>> TEXTURE_ICON = registerIconType("texture", () -> new IconType<>(TextureIcon.CODEC));

    public static void init(IEventBus bus)
    {
        bus.addListener(LootBagRegistry::createRegistries);
        CONTENT_TYPES.register(bus);
        BAG_TYPES.register(bus);
        ICON_TYPES.register(bus);
    }

    public static void createRegistries(NewRegistryEvent event)
    {
        event.create(new RegistryBuilder<ContentType<?>>().setName(CONTENT_TYPE_KEY.getValue()).hasTags(),
                registry -> CONTENT_TYPE_REGISTRY = (Registry<ContentType<?>>) Registries.REGISTRIES.get(CONTENT_TYPE_KEY.getValue()));
        event.create(new RegistryBuilder<BagType<?>>().setName(BAG_TYPE_KEY.getValue()).hasTags(),
                registry -> BAG_TYPE_REGISTRY = (Registry<BagType<?>>) Registries.REGISTRIES.get(BAG_TYPE_KEY.getValue()));
        event.create(new RegistryBuilder<IconType<?>>().setName(ICON_TYPE_KEY.getValue()).hasTags(),
                registry -> ICON_TYPE_REGISTRY = (Registry<IconType<?>>) Registries.REGISTRIES.get(ICON_TYPE_KEY.getValue()));
    }

    public static <T extends Content> RegistryObject<ContentType<T>> registerContentType(String name, Supplier<ContentType<T>> type)
    {
        return CONTENT_TYPES.register(name, type);
    }

    public static <T extends Bag> RegistryObject<BagType<T>> registerBagType(String name, Supplier<BagType<T>> type)
    {
        return BAG_TYPES.register(name, type);
    }

    public static <T extends Icon> RegistryObject<IconType<T>> registerIconType(String name, Supplier<IconType<T>> type)
    {
        return ICON_TYPES.register(name, type);
    }
}
