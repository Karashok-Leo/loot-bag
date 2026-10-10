package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.resources.ResourceKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;

public class LootBagItemRegistry
{
    public static final ResourceKey<CreativeModeTab> ITEM_GROUP_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, LootBagMod.id("loot_bag"));
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(LootBagMod.MOD_ID);
    public static final DeferredRegister<CreativeModeTab> ITEM_GROUPS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, LootBagMod.MOD_ID);
    public static final DeferredItem<LootBagItem> LOOT_BAG = ITEMS.register("loot_bag", () -> new LootBagItem(new Item.Properties().stacksTo(16)));
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> ITEM_GROUP = ITEM_GROUPS.register("loot_bag", () ->
                CreativeModeTab
                        .builder()
                        .icon(() -> LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG.get()::getStack)
                                .findFirst()
                                .orElse(ItemStack.EMPTY)
                        )
                        .displayItems((displayContext, entries) -> entries.acceptAll(LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG.get()::getStack)
                                .toList()
                        ))
                        .title(Component.translatable("itemGroup.loot-bag.loot_bag"))
                        .build()
        );

    public static void init(IEventBus bus)
    {
        ITEMS.register(bus);
        ITEM_GROUPS.register(bus);
    }
}
