package karashokleo.loot_bag.internal.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.neoforged.neoforge.event.AddReloadListenerEvent;
import net.minecraft.resources.RegistryOps;
import net.minecraft.core.HolderLookup;
import net.minecraft.server.packs.resources.ResourceManagerReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.PackType;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public final class LootBagManagerImpl implements LootBagManager
{
    public static final LootBagManager INSTANCE = new LootBagManagerImpl();

    public final Map<ResourceLocation, ContentEntry> CONTENTS = new HashMap<>();
    public final Map<ResourceLocation, BagEntry> BAGS = new HashMap<>();

    private LootBagManagerImpl()
    {
    }

    @Override
    public Collection<ContentEntry> getAllContentEntries()
    {
        return CONTENTS.values();
    }

    @Override
    public Collection<BagEntry> getAllBagEntries()
    {
        return BAGS.values();
    }

    @Nullable
    @Override
    public ContentEntry getContentEntry(ResourceLocation id)
    {
        return CONTENTS.get(id);
    }

    @Nullable
    @Override
    public BagEntry getBagEntry(ResourceLocation id)
    {
        return BAGS.get(id);
    }

    @Override
    public void putContent(ResourceLocation id, Content content)
    {
        CONTENTS.put(id, new ContentEntry(id, content));
    }

    @Override
    public void putBag(ResourceLocation id, Bag bag)
    {
        BAGS.put(id, new BagEntry(id, bag));
    }

    @Override
    public void clearAllContentEntries()
    {
        CONTENTS.clear();
    }

    @Override
    public void clearAllBagEntries()
    {
        BAGS.clear();
    }

    public static void registerLoader(AddReloadListenerEvent event)
    {
        event.addListener(new Loader(event.getRegistryAccess()));
    }

    private static class Loader implements ResourceManagerReloadListener
    {
        private final HolderLookup.Provider registries;

        public Loader(HolderLookup.Provider registries)
        {
            this.registries = registries;
        }

        @Override
        public void onResourceManagerReload(ResourceManager manager)
        {
            INSTANCE.clearAllContentEntries();
            this.tryLoad(manager, ConstantTexts.CONTENT_DIR, Content.CODEC, INSTANCE::putContent);
            INSTANCE.clearAllBagEntries();
            this.tryLoad(manager, ConstantTexts.BAG_DIR, Bag.CODEC, INSTANCE::putBag);
        }

        private <T> void tryLoad(ResourceManager manager, String path, Codec<T> codec, BiConsumer<ResourceLocation, T> consumer)
        {
            manager.listResources(path, id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) ->
            {
                try
                {
                    InputStream stream = resourceRef.open();
                    JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                    consumer.accept(
                            id.withPath(s -> s
                                    .replaceFirst(path + "/", "")
                                    .replaceFirst(".json", "")),
                            codec.decode(registries.createSerializationContext(JsonOps.INSTANCE), data).result().orElseThrow().getFirst()
                    );
                } catch (Exception e)
                {
                    LootBagMod.LOGGER.error("Error while loading {}", id);
                }
            });
        }
    }
}
