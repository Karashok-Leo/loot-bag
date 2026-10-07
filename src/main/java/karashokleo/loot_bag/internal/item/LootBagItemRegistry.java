package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class LootBagItemRegistry
{
    public static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, LootBagMod.id("loot_bag"));
    public static LootBagItem LOOT_BAG = new LootBagItem(new Item.Properties().stacksTo(16));

    public static void init()
    {
        LOOT_BAG = Registry.register(BuiltInRegistries.ITEM, ITEM_GROUP_KEY.location(), LOOT_BAG);

    }

    public static void initGroup()
    {
        Registry.register(
                BuiltInRegistries.CREATIVE_MODE_TAB,
                LootBagMod.id("loot_bag"),
                CreativeModeTab
                        .builder()
                        .icon(() -> LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG::getStack)
                                .findFirst()
                                .orElse(ItemStack.EMPTY)
                        )
                        .displayItems((displayContext, entries) -> entries.acceptAll(LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG::getStack)
                                .toList()
                        ))
                        .title(Component.translatable("itemGroup.loot-bag.loot_bag"))
                        .build()
        );
    }
}
