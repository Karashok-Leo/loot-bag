package karashokleo.loot_bag.internal.fabric;

import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import karashokleo.loot_bag.api.common.bag.RandomBag;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.content.CommandContent;
import karashokleo.loot_bag.api.common.content.ItemContent;
import karashokleo.loot_bag.api.common.content.LootTableContent;
import karashokleo.loot_bag.internal.data.LootBagData;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import karashokleo.loot_bag.internal.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
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
        LootBagItemRegistry.init();
        ServerNetwork.init();
        LootBagData.registerLoader();
    }

    private static void initStaticRegistries()
    {
        LootBagRegistry.CONTENT_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(LootBagRegistry.CONTENT_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        LootBagRegistry.registerContentType(id("item"), ItemContent.TYPE);
        LootBagRegistry.registerContentType(id("loot_table"), LootTableContent.TYPE);
        LootBagRegistry.registerContentType(id("command"), CommandContent.TYPE);
        LootBagRegistry.BAG_TYPE_REGISTRY = FabricRegistryBuilder
                .createSimple(LootBagRegistry.BAG_TYPE_KEY)
                .attribute(RegistryAttribute.SYNCED)
                .buildAndRegister();
        LootBagRegistry.registerBagType(id("single"), SingleBag.TYPE);
        LootBagRegistry.registerBagType(id("optional"), OptionalBag.TYPE);
        LootBagRegistry.registerBagType(id("random"), RandomBag.TYPE);
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}