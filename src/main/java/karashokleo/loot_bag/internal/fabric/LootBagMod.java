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
import net.fabricmc.api.ModInitializer;
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
        LootBagEntry.init();
        ServerNetworkHandlers.init();
        LootBagManagerImpl.registerLoader();
    }

    private static void initStaticRegistries()
    {
        LootBagRegistry.registerContentType(id("item"), ItemContent.TYPE);
        LootBagRegistry.registerContentType(id("loot_table"), LootTableContent.TYPE);
        LootBagRegistry.registerContentType(id("command"), CommandContent.TYPE);
        LootBagRegistry.registerContentType(id("effect"), EffectContent.TYPE);
        LootBagRegistry.registerBagType(id("single"), SingleBag.TYPE);
        LootBagRegistry.registerBagType(id("optional"), OptionalBag.TYPE);
        LootBagRegistry.registerBagType(id("random"), RandomBag.TYPE);
        LootBagRegistry.registerIconType(id("item"), ItemIcon.TYPE);
        LootBagRegistry.registerIconType(id("texture"), TextureIcon.TYPE);
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}