package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record BagEntry(Identifier id, Bag bag, String nameKey)
{
    public BagEntry(Identifier id, Bag bag)
    {
        this(id, bag, id.toTranslationKey("bag"));
    }

    public static final Codec<BagEntry> CODEC = Identifier.CODEC.comapFlatMap(
            id ->
            {
                BagEntry entry = LootBagManager.getInstance().getBagEntry(id);
                return entry == null ? DataResult.error(() -> ConstantTexts.unknownBagMessage(id)) : DataResult.success(entry);
            },
            BagEntry::id
    );

    public Text getName()
    {
        return Text.translatable(this.nameKey);
    }
}
