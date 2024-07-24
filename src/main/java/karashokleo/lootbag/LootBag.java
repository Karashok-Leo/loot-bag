package karashokleo.lootbag;

import karashokleo.lootbag.content.LootBagItems;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.network.ServerNetwork;
import net.fabricmc.api.ModInitializer;
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
        LootBagRegistry.register();
        LootBagItems.init();
        ServerNetwork.init();
    }

    public static Identifier id(String path)
    {
        return new Identifier(MOD_ID, path);
    }
}