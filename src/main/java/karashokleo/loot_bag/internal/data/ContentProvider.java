package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.provider.AbstractContentProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class ContentProvider extends AbstractContentProvider
{
    public ContentProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    protected void configure(BiConsumer<Identifier, Content> provider)
    {
        LootBagDataGenerator.CONTENTS.forEach(entry -> provider.accept(entry.id(), entry.content()));
    }

    @Override
    public String getName()
    {
        return "Loot Bag Example Contents";
    }
}
