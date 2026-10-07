package karashokleo.loot_bag.api.data;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import karashokleo.loot_bag.internal.data.CodecDataProvider;
import net.minecraft.data.DataOutput;

public abstract class AbstractBagProvider extends CodecDataProvider<Bag>
{
    public AbstractBagProvider(DataOutput dataOutput)
    {
        super(dataOutput, ConstantTexts.BAG_DIR, Bag.CODEC);
    }
}
