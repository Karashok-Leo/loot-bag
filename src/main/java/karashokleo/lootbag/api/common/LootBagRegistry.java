package karashokleo.lootbag.api.common;

import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.bag.BagType;
import karashokleo.lootbag.api.common.content.Content;
import karashokleo.lootbag.api.common.content.ContentType;
import karashokleo.lootbag.fabric.LootBagMod;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.util.Identifier;

@SuppressWarnings("all")
public class LootBagRegistry
{

    public static Registry<ContentType<?>> CONTENT_TYPE_REGISTRY;
    public static Registry<Content> CONTENT_REGISTRY;
    public static Registry<BagType<?>> BAG_TYPE_REGISTRY;
    public static Registry<Bag> BAG_REGISTRY;

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
        return registerContentType(LootBagMod.id(path), type);
    }

    public static <T extends Content> ContentType<T> registerContentType(Identifier id, ContentType<T> type)
    {
        return Registry.register(CONTENT_TYPE_REGISTRY, id, type);
    }

    public static <T extends Bag> BagType<T> registerBagType(String path, BagType<T> type)
    {
        return registerBagType(LootBagMod.id(path), type);
    }

    public static <T extends Bag> BagType<T> registerBagType(Identifier id, BagType<T> type)
    {
        return Registry.register(BAG_TYPE_REGISTRY, id, type);
    }
}
