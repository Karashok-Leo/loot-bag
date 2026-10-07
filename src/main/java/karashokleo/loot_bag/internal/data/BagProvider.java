package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.data.AbstractBagProvider;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.minecraft.registry.RegistryWrapper;
import java.util.concurrent.CompletableFuture;
import net.minecraft.util.Identifier;

import java.util.function.BiConsumer;

public class BagProvider extends AbstractBagProvider
{
    public BagProvider(FabricDataOutput dataOutput, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(dataOutput, registriesFuture);
    }

    @Override
    protected void configure(BiConsumer<Identifier, Bag> provider, RegistryWrapper.WrapperLookup lookup)
    {
        LootBagDataGenerator.BAGS.forEach(entry -> provider.accept(entry.id(), entry.bag()));
    }

    @Override
    public String getName()
    {
        return "Loot Bag Example Bags";
    }
}
