package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.data.AbstractContentProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.core.HolderLookup;
import java.util.concurrent.CompletableFuture;
import net.minecraft.resources.ResourceLocation;

import java.util.function.BiConsumer;

public class ContentProvider extends AbstractContentProvider
{
    public ContentProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(dataOutput, registries);
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, Content> provider)
    {
        LootBagDataGenerator.CONTENTS.forEach(entry -> provider.accept(entry.id(), entry.content()));
    }

    @Override
    public String getName()
    {
        return "Loot Bag Example Contents";
    }
}
