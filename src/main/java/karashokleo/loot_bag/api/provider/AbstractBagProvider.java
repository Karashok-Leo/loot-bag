package karashokleo.loot_bag.api.provider;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.internal.data.LootBagData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;

public abstract class AbstractBagProvider extends FabricCodecDataProvider<Bag>
{
    public AbstractBagProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput, DataOutput.OutputType.DATA_PACK, LootBagData.BAG_DIR, Bag.CODEC);
    }
}
