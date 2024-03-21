package net.karashokleo.lootbag;

import net.fabricmc.api.ModInitializer;

import net.karashokleo.lootbag.config.LootBagConfig;
import net.karashokleo.lootbag.content.LootBagItems;
import net.karashokleo.lootbag.resource.LootBagResources;
import net.karashokleo.lootbag.network.ServerNetwork;
import net.minecraft.util.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LootBag implements ModInitializer
{
    public static final String MOD_ID = "loot-bag";
    public static final Logger LOGGER = LoggerFactory.getLogger("loot-bag");

    @Override
    public void onInitialize()
    {
        LootBagConfig.init();
        LootBagItems.init();
        LootBagResources.init();
        ServerNetwork.init();
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}