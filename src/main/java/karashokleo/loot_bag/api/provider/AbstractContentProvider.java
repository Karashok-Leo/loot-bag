package karashokleo.loot_bag.api.provider;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.internal.data.LootBagData;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;

public abstract class AbstractContentProvider extends FabricCodecDataProvider<Content>
{
    public AbstractContentProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput, DataOutput.OutputType.DATA_PACK, LootBagData.CONTENT_DIR, Content.CODEC);
    }
}
