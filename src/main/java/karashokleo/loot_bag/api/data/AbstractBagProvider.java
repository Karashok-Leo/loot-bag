package karashokleo.loot_bag.api.data;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public abstract class AbstractBagProvider implements DataProvider
{
    private final PackOutput.PathProvider paths;
    private final CompletableFuture<HolderLookup.Provider> registries;

    public AbstractBagProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        this.paths = dataOutput.createPathProvider(PackOutput.Target.DATA_PACK, ConstantTexts.BAG_DIR);
        this.registries = registries;
    }

    protected abstract void configure(BiConsumer<ResourceLocation, Bag> provider);

    @Override
    public CompletableFuture<?> run(CachedOutput output)
    {
        return registries.thenCompose(provider ->
        {
            List<CompletableFuture<?>> futures = new ArrayList<>();
            configure((id, value) -> futures.add(DataProvider.saveStable(output, provider, Bag.CODEC, value, paths.json(id))));
            return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
        });
    }
}
