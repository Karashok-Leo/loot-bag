package karashokleo.lootbag.data;

import karashokleo.lootbag.LootBag;
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricDynamicRegistryProvider;
import net.minecraft.registry.RegistryWrapper;

import java.util.concurrent.CompletableFuture;

public class LootBagProvider extends FabricDynamicRegistryProvider
{
    public LootBagProvider(FabricDataOutput output, CompletableFuture<RegistryWrapper.WrapperLookup> registriesFuture)
    {
        super(output, registriesFuture);
    }

    @Override
    protected void configure(RegistryWrapper.WrapperLookup registries, Entries entries)
    {
        LootBagData.CONTENTS.forEach(entries::add);
        LootBagData.BAGS.forEach(entries::add);
    }

    @Override
    public String getName()
    {
        return LootBag.MOD_ID;
    }
}
