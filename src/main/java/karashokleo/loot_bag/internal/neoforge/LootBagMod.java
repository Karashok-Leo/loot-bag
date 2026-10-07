package karashokleo.loot_bag.internal.neoforge;

import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.content.CommandContent;
import karashokleo.loot_bag.api.common.content.EffectContent;
import karashokleo.loot_bag.api.common.content.ItemContent;
import karashokleo.loot_bag.api.common.content.LootTableContent;
import karashokleo.loot_bag.api.common.icon.ItemIcon;
import karashokleo.loot_bag.api.common.icon.TextureIcon;
import karashokleo.loot_bag.api.common.loot.LootBagEntry;
import karashokleo.loot_bag.internal.data.LootBagManagerImpl;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.ServerNetworkHandlers;
import net.neoforged.fml.common.Mod;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.registries.RegisterEvent;
import net.minecraft.core.registries.Registries;
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
        modBus.addListener(LootBagRegistry::registerRegistries);
        modBus.addListener(LootBagMod::register);
        modBus.addListener(ServerNetworkHandlers::register);
        modBus.addListener(LootBagDataGenerator::gatherData);
        NeoForge.EVENT_BUS.addListener(LootBagManagerImpl::registerLoader);
        NeoForge.EVENT_BUS.addListener(ServerNetworkHandlers::sendSyncData);
    }

    private static void register(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(LootBagRegistry.CONTENT_TYPE_KEY))
        {
            LootBagRegistry.registerContentType(id("item"), ItemContent.TYPE);
            LootBagRegistry.registerContentType(id("loot_table"), LootTableContent.TYPE);
            LootBagRegistry.registerContentType(id("command"), CommandContent.TYPE);
            LootBagRegistry.registerContentType(id("effect"), EffectContent.TYPE);
        }
        else if (event.getRegistryKey().equals(LootBagRegistry.BAG_TYPE_KEY))
        {
            LootBagRegistry.registerBagType(id("single"), SingleBag.TYPE);
            LootBagRegistry.registerBagType(id("optional"), OptionalBag.TYPE);
            LootBagRegistry.registerBagType(id("random"), RandomBag.TYPE);
        }
        else if (event.getRegistryKey().equals(LootBagRegistry.ICON_TYPE_KEY))
        {
            LootBagRegistry.registerIconType(id("item"), ItemIcon.TYPE);
            LootBagRegistry.registerIconType(id("texture"), TextureIcon.TYPE);
        }
        else if (event.getRegistryKey().equals(Registries.ITEM)) LootBagItemRegistry.init();
        else if (event.getRegistryKey().equals(Registries.CREATIVE_MODE_TAB)) LootBagItemRegistry.initGroup();
        else if (event.getRegistryKey().equals(Registries.LOOT_POOL_ENTRY_TYPE)) LootBagEntry.init();
    }

    public static ResourceLocation id(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(MOD_ID, path);
    }
}