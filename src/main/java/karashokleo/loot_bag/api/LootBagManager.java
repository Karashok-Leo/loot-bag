package karashokleo.loot_bag.api;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.data.LootBagManagerImpl;
import net.minecraft.util.Identifier;
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
    ContentEntry getContentEntry(Identifier id);

    @Nullable
    BagEntry getBagEntry(Identifier id);

    void putContent(Identifier id, Content content);

    void putBag(Identifier id, Bag bag);

    void clearAllContentEntries();

    void clearAllBagEntries();
}
