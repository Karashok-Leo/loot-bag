package karashokleo.loot_bag.api.data;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricCodecDataProvider;
import net.minecraft.data.DataOutput;

public abstract class AbstractContentProvider extends FabricCodecDataProvider<Content>
{
    public AbstractContentProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput, DataOutput.OutputType.DATA_PACK, ConstantTexts.CONTENT_DIR, Content.CODEC);
    }
}
