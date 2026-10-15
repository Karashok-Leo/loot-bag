package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.data.AbstractBagProvider;
import net.minecraft.core.HolderLookup;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.NotNull;

import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class BagProvider extends AbstractBagProvider
{
    public BagProvider(PackOutput dataOutput, CompletableFuture<HolderLookup.Provider> registries)
    {
        super(dataOutput, registries);
    }

    @Override
    protected void configure(BiConsumer<ResourceLocation, Bag> provider)
    {
        LootBagDataGenerator.BAGS.forEach(entry -> provider.accept(entry.id(), entry.bag()));
    }

    @NotNull
    @Override
    public String getName()
    {
        return "Loot Bag Example Bags";
    }
}
