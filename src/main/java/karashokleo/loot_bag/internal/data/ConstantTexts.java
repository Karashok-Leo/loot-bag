package karashokleo.loot_bag.internal.data;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.util.Identifier;

public final class ConstantTexts
{
    private static final String PARENT_DIR = LootBagMod.MOD_ID + "/";
    public static final String BAG_DIR = PARENT_DIR + "bag";
    public static final String CONTENT_DIR = PARENT_DIR + "content";

    public static String unknownContentMessage(Identifier id)
    {
        return "Unknown loot content: '%s'".formatted(id);
    }

    public static String unknownBagMessage(Identifier id)
    {
        return "Unknown loot bag: '%s'".formatted(id);
    }
}
