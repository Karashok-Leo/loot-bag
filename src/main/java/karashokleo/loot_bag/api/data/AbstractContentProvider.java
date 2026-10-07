package karashokleo.loot_bag.api.data;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import karashokleo.loot_bag.internal.data.CodecDataProvider;
import net.minecraft.data.DataOutput;

public abstract class AbstractContentProvider extends CodecDataProvider<Content>
{
    public AbstractContentProvider(DataOutput dataOutput)
    {
        super(dataOutput, ConstantTexts.CONTENT_DIR, Content.CODEC);
    }
}
