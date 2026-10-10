package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.item.Item;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class LootBagItemRegistry
{
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, LootBagMod.id("loot_bag"));
    public static final DeferredRegister<Item> ITEMS = DeferredRegister.create(RegistryKeys.ITEM, LootBagMod.MOD_ID);
    public static final DeferredRegister<ItemGroup> ITEM_GROUPS = DeferredRegister.create(RegistryKeys.ITEM_GROUP, LootBagMod.MOD_ID);
    public static final RegistryObject<LootBagItem> LOOT_BAG = ITEMS.register("loot_bag", () -> new LootBagItem(new Item.Settings().maxCount(16)));
    public static final RegistryObject<ItemGroup> ITEM_GROUP = ITEM_GROUPS.register("loot_bag", () ->
                ItemGroup
                        .builder()
                        .icon(() -> LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG.get()::getStack)
                                .findFirst()
                                .orElse(ItemStack.EMPTY)
                        )
                        .entries((displayContext, entries) -> entries.addAll(LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG.get()::getStack)
                                .toList()
                        ))
                        .displayName(Text.translatable("itemGroup.loot-bag.loot_bag"))
                        .build()
        );

    public static void init(IEventBus bus)
    {
        ITEMS.register(bus);
        ITEM_GROUPS.register(bus);
    }
}
