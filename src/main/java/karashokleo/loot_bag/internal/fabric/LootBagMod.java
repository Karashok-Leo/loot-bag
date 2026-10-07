package karashokleo.loot_bag.internal.fabric;

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
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Mod(LootBagMod.FORGE_MOD_ID)
public class LootBagMod
{
    public static final String FORGE_MOD_ID = "loot_bag";
    // Keep the original resource namespace and datapack directories.
    public static final String MOD_ID = "loot-bag";
    public static final Logger LOGGER = LoggerFactory.getLogger("loot-bag");

    public LootBagMod()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();
        bus.addListener(LootBagRegistry::createRegistries);
        bus.addListener(LootBagMod::initStaticRegistries);
        bus.addListener(LootBagItemRegistry::init);
        bus.addListener((RegisterEvent event) -> {
            if (event.getRegistryKey().equals(RegistryKeys.LOOT_POOL_ENTRY_TYPE)) LootBagEntry.init();
        });
        ServerNetworkHandlers.init();
        LootBagManagerImpl.registerLoader();
    }

    private static void initStaticRegistries(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(LootBagRegistry.CONTENT_TYPE_KEY))
        {
            LootBagRegistry.registerContentType(id("item"), ItemContent.TYPE);
            LootBagRegistry.registerContentType(id("loot_table"), LootTableContent.TYPE);
            LootBagRegistry.registerContentType(id("command"), CommandContent.TYPE);
            LootBagRegistry.registerContentType(id("effect"), EffectContent.TYPE);
        }
        if (event.getRegistryKey().equals(LootBagRegistry.BAG_TYPE_KEY))
        {
            LootBagRegistry.registerBagType(id("single"), SingleBag.TYPE);
            LootBagRegistry.registerBagType(id("optional"), OptionalBag.TYPE);
            LootBagRegistry.registerBagType(id("random"), RandomBag.TYPE);
        }
        if (event.getRegistryKey().equals(LootBagRegistry.ICON_TYPE_KEY))
        {
            LootBagRegistry.registerIconType(id("item"), ItemIcon.TYPE);
            LootBagRegistry.registerIconType(id("texture"), TextureIcon.TYPE);
        }
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}
