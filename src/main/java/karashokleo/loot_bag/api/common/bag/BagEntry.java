package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record BagEntry(ResourceLocation id, Bag bag, String nameKey)
{
    public BagEntry(ResourceLocation id, Bag bag)
    {
        this(id, bag, id.toLanguageKey("bag"));
    }

    public static final Codec<BagEntry> CODEC = ResourceLocation.CODEC.comapFlatMap(
            id ->
            {
                BagEntry entry = LootBagManager.getInstance().getBagEntry(id);
                return entry == null ? DataResult.error(() -> ConstantTexts.unknownBagMessage(id)) : DataResult.success(entry);
            },
            BagEntry::id
    );

    public Component getName()
    {
        return Component.translatable(this.nameKey);
    }
}
