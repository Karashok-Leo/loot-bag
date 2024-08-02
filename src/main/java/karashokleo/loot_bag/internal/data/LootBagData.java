package karashokleo.loot_bag.internal.data;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
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

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiConsumer;

public class LootBagData
{
    private static final String PARENT_DIR = LootBagMod.MOD_ID + "/";
    public static final String CONTENT_DIR = PARENT_DIR + "content";
    public static final String BAG_DIR = PARENT_DIR + "bag";
    public static final Map<Identifier, ContentEntry> CONTENTS = new HashMap<>();
    public static final Map<Identifier, BagEntry> BAGS = new HashMap<>();

    public static void putContent(Identifier id, Content content)
    {
        CONTENTS.put(id, new ContentEntry(id, content));
    }

    public static void putBag(Identifier id, Bag bag)
    {
        BAGS.put(id, new BagEntry(id, bag));
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
            CONTENTS.clear();
            BAGS.clear();
            this.tryLoad(manager, CONTENT_DIR, Content.CODEC, LootBagData::putContent);
            this.tryLoad(manager, BAG_DIR, Bag.CODEC, LootBagData::putBag);
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

    ;
}
