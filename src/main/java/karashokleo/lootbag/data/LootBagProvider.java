package karashokleo.lootbag.data;

import karashokleo.lootbag.fabric.LootBagMod;
import karashokleo.lootbag.api.common.LootBagRegistry;
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
        entries.addAll(registries.getWrapperOrThrow(LootBagRegistry.CONTENT_KEY));
        entries.addAll(registries.getWrapperOrThrow(LootBagRegistry.BAG_KEY));
    }

    @Override
    public String getName()
    {
        return LootBagMod.MOD_ID;
    }
}
