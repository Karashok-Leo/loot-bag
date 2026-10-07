package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.item.Item;
import net.minecraftforge.registries.RegisterEvent;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class LootBagItemRegistry
{
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, LootBagMod.id("loot_bag"));
    public static LootBagItem LOOT_BAG = new LootBagItem(new Item.Settings().maxCount(16));

    public static void init(RegisterEvent event)
    {
        if (event.getRegistryKey().equals(RegistryKeys.ITEM))
            LOOT_BAG = Registry.register(Registries.ITEM, ITEM_GROUP_KEY.getValue(), LOOT_BAG);
        if (!event.getRegistryKey().equals(RegistryKeys.ITEM_GROUP)) return;

        Registry.register(
                Registries.ITEM_GROUP,
                LootBagMod.id("loot_bag"),
                ItemGroup
                        .builder()
                        .icon(() -> LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG::getStack)
                                .findFirst()
                                .orElse(ItemStack.EMPTY)
                        )
                        .entries((displayContext, entries) -> entries.addAll(LootBagManager
                                .getInstance()
                                .getAllBagEntries()
                                .stream()
                                .map(LOOT_BAG::getStack)
                                .toList()
                        ))
                        .displayName(Text.translatable("itemGroup.loot-bag.loot_bag"))
                        .build()
        );
    }
}
