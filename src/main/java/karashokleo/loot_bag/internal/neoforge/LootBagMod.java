package karashokleo.loot_bag.internal.neoforge;

import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.internal.data.LootBagManagerImpl;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.ServerNetworkHandlers;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import karashokleo.loot_bag.internal.data.LootBagDataGenerator;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LootBagMod.LOADER_ID)
public class LootBagMod
{
    public static final String LOADER_ID = "loot_bag";
    public static final String MOD_ID = "loot-bag";
    public static final Logger LOGGER = LoggerFactory.getLogger("loot-bag");

    public LootBagMod(IEventBus modBus)
    {
        LootBagRegistry.init(modBus);
        LootBagItemRegistry.init(modBus);
        LootBagEntry.init(modBus);
        modBus.addListener(ServerNetworkHandlers::register);
        modBus.addListener(LootBagDataGenerator::gatherData);
        NeoForge.EVENT_BUS.addListener(LootBagManagerImpl::registerLoader);
        NeoForge.EVENT_BUS.addListener(ServerNetworkHandlers::sendSyncData);
    }

    public static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}