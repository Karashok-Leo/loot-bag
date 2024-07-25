package karashokleo.lootbag.fabric;

import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.bag.OptionalBag;
import karashokleo.lootbag.api.common.bag.RandomBag;
import karashokleo.lootbag.api.common.bag.SingleBag;
import karashokleo.lootbag.api.common.content.CommandContent;
import karashokleo.lootbag.api.common.content.Content;
import karashokleo.lootbag.api.common.content.ItemContent;
import karashokleo.lootbag.api.common.content.LootTableContent;
import karashokleo.lootbag.item.LootBagItemRegistry;
import karashokleo.lootbag.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.event.registry.DynamicRegistries;
import net.fabricmc.fabric.api.event.registry.FabricRegistryBuilder;
import net.fabricmc.fabric.api.event.registry.RegistryAttribute;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootBagMod implements ModInitializer
{
    public static final String MOD_ID = "loot-bag";
    public static final Logger LOGGER = LoggerFactory.getLogger("loot-bag");

    @Override
    public void onInitialize()
    {
        initStaticRegistries();
        initDynamicRegistries();
        registerContentType();
        registerBagType();
        fetchDynamicRegistries();
        LootBagItemRegistry.init();
        ServerNetwork.init();
    }

    private static void initStaticRegistries()
    {
        LootBagRegistry.CONTENT_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(LootBagRegistry.CONTENT_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        LootBagRegistry.BAG_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(LootBagRegistry.BAG_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
    }

    private static void initDynamicRegistries()
    {
        DynamicRegistries.registerSynced(LootBagRegistry.CONTENT_KEY, Content.CODEC);
        DynamicRegistries.registerSynced(LootBagRegistry.BAG_KEY, Bag.CODEC);
    }

    private static void registerContentType()
    {
        LootBagRegistry.registerContentType("item", ItemContent.TYPE);
        LootBagRegistry.registerContentType("loot_table", LootTableContent.TYPE);
        LootBagRegistry.registerContentType("command", CommandContent.TYPE);
    }

    private static void registerBagType()
    {
        LootBagRegistry.registerBagType("single", SingleBag.TYPE);
        LootBagRegistry.registerBagType("optional", OptionalBag.TYPE);
        LootBagRegistry.registerBagType("random", RandomBag.TYPE);
    }

    private static void fetchDynamicRegistries()
    {
        ServerLifecycleEvents.SERVER_STARTED.register(server ->
        {
            LootBagRegistry.CONTENT_REGISTRY = server.getRegistryManager().get(LootBagRegistry.CONTENT_KEY);
            LootBagRegistry.BAG_REGISTRY = server.getRegistryManager().get(LootBagRegistry.BAG_KEY);
        });
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}