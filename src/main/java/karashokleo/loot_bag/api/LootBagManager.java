package karashokleo.loot_bag.api;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.data.LootBagManagerImpl;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;

public interface LootBagManager
{
    static LootBagManager getInstance()
    {
        return LootBagManagerImpl.INSTANCE;
    }

    Collection<ContentEntry> getAllContentEntries();

    Collection<BagEntry> getAllBagEntries();

    @Nullable
    ContentEntry getContentEntry(ResourceLocation id);

    @Nullable
    BagEntry getBagEntry(ResourceLocation id);

    void putContent(ResourceLocation id, Content content);

    void putBag(ResourceLocation id, Bag bag);

    void clearAllContentEntries();

    void clearAllBagEntries();
}
