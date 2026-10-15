package karashokleo.loot_bag.internal.fabric;

import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.internal.data.LootBagManagerImpl;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.ServerNetworkHandlers;
import net.minecraft.util.Identifier;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LootBagMod.LOADER_ID)
public class LootBagMod
{
    // Mod loader restriction: no '-'
    public static final String LOADER_ID = "loot_bag";
    public static final String MOD_ID = "loot-bag";
    public static final Logger LOGGER = LoggerFactory.getLogger("loot-bag");

    public LootBagMod(FMLJavaModLoadingContext context)
    {
        IEventBus bus = context.getModEventBus();
        LootBagRegistry.init(bus);
        LootBagItemRegistry.init(bus);
        LootBagEntry.init(bus);
        ServerNetworkHandlers.init();
        LootBagManagerImpl.registerLoader();
    }

    public static Identifier id(String path)
    {
        return Identifier.of(MOD_ID, path);
    }
}
