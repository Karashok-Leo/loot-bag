package karashokleo.lootbag.content.logic;

import karashokleo.lootbag.LootBag;
import karashokleo.lootbag.content.logic.bag.*;
import karashokleo.lootbag.content.logic.content.*;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("all")
public class LootBagRegistry
{
    public static final RegistryKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = RegistryKey.ofRegistry(LootBag.id("content_type"));
    public static final RegistryKey<Registry<Content>> CONTENT_KEY = RegistryKey.ofRegistry(LootBag.id("content"));
    public static final RegistryKey<Registry<BagType<?>>> BAG_TYPE_KEY = RegistryKey.ofRegistry(LootBag.id("bag_type"));
    public static final RegistryKey<Registry<Bag>> BAG_KEY = RegistryKey.ofRegistry(LootBag.id("bag"));

    public static Registry<ContentType<?>> CONTENT_TYPE_REGISTRY;
    public static Registry<Content> CONTENT_REGISTRY;
    public static Registry<BagType<?>> BAG_TYPE_REGISTRY;
    public static Registry<Bag> BAG_REGISTRY;

    public static void register()
    {
        CONTENT_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(CONTENT_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        BAG_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(BAG_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();

        DynamicRegistries.registerSynced(CONTENT_KEY, Content.CODEC);
        DynamicRegistries.registerSynced(BAG_KEY, Bag.CODEC);

        registerContentType("item", ItemContent.TYPE);
        registerContentType("loot_table", LootTableContent.TYPE);
        registerContentType("command", CommandContent.TYPE);

        registerBagType("single", SingleBag.TYPE);
        registerBagType("optional", OptionalBag.TYPE);
        registerBagType("random", RandomBag.TYPE);

        ServerLifecycleEvents.SERVER_STARTED.register(server ->
        {
            CONTENT_REGISTRY = server.getRegistryManager().get(LootBagRegistry.CONTENT_KEY);
            BAG_REGISTRY = server.getRegistryManager().get(LootBagRegistry.BAG_KEY);
        });
    }

    public static Content getContent(Identifier contentId)
    {
        return CONTENT_REGISTRY.get(contentId);
    }

    public static Identifier getContentId(Content content)
    {
        return CONTENT_REGISTRY.getId(content);
    }

    public static Bag getBag(Identifier bagId)
    {
        return BAG_REGISTRY.get(bagId);
    }

    public static Identifier getBagId(Bag bag)
    {
        return BAG_REGISTRY.getId(bag);
    }

    public static <T extends Content> ContentType<T> registerContentType(String path, ContentType<T> type)
    {
        return registerContentType(LootBag.id(path), type);
    }

    public static <T extends Content> ContentType<T> registerContentType(Identifier id, ContentType<T> type)
    {
        return Registry.register(CONTENT_TYPE_REGISTRY, id, type);
    }

    public static <T extends Bag> BagType<T> registerBagType(String path, BagType<T> type)
    {
        return registerBagType(LootBag.id(path), type);
    }

    public static <T extends Bag> BagType<T> registerBagType(Identifier id, BagType<T> type)
    {
        return Registry.register(BAG_TYPE_REGISTRY, id, type);
    }
}
