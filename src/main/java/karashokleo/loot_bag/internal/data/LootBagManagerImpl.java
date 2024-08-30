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
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleSynchronousResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
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

    public final Map<Identifier, ContentEntry> CONTENTS = new HashMap<>();
    public final Map<Identifier, BagEntry> BAGS = new HashMap<>();

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
    public ContentEntry getContentEntry(Identifier id)
    {
        return CONTENTS.get(id);
    }

    @Nullable
    @Override
    public BagEntry getBagEntry(Identifier id)
    {
        return BAGS.get(id);
    }

    @Override
    public void putContent(Identifier id, Content content)
    {
        CONTENTS.put(id, new ContentEntry(id, content));
    }

    @Override
    public void putBag(Identifier id, Bag bag)
    {
        BAGS.put(id, new BagEntry(id, bag));
    }

    @Override
    public void clearAllEntries()
    {
        CONTENTS.clear();
        BAGS.clear();
    }

    public static void registerLoader()
    {
        ResourceManagerHelper.get(ResourceType.SERVER_DATA).registerReloadListener(new Loader());
    }

    private static class Loader implements SimpleSynchronousResourceReloadListener
    {
        private final Identifier LOADER_ID = LootBagMod.id("loader");

        @Override
        public Identifier getFabricId()
        {
            return LOADER_ID;
        }

        @Override
        public void reload(ResourceManager manager)
        {
            INSTANCE.clearAllEntries();
            this.tryLoad(manager, ConstantTexts.CONTENT_DIR, Content.CODEC, INSTANCE::putContent);
            this.tryLoad(manager, ConstantTexts.BAG_DIR, Bag.CODEC, INSTANCE::putBag);
        }

        private <T> void tryLoad(ResourceManager manager, String path, Codec<T> codec, BiConsumer<Identifier, T> consumer)
        {
            manager.findResources(path, id -> id.getPath().endsWith(".json")).forEach((id, resourceRef) ->
            {
                try
                {
                    InputStream stream = resourceRef.getInputStream();
                    JsonObject data = JsonParser.parseReader(new InputStreamReader(stream)).getAsJsonObject();
                    consumer.accept(
                            id.withPath(s -> s
                                    .replaceFirst(path + "/", "")
                                    .replaceFirst(".json", "")),
                            codec.decode(JsonOps.INSTANCE, data).result().orElseThrow().getFirst()
                    );
                } catch (Exception e)
                {
                    LootBagMod.LOGGER.error("Error while loading {}", id);
                }
            });
        }
    }
}
