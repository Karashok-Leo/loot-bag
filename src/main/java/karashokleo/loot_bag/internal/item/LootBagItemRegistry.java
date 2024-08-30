package karashokleo.loot_bag.internal.item;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
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
    public static LootBagItem LOOT_BAG;

    public static void init()
    {
        LOOT_BAG = Registry.register(Registries.ITEM, ITEM_GROUP_KEY.getValue(), new LootBagItem(new FabricItemSettings().maxCount(16)));

        Registry.register(
                Registries.ITEM_GROUP,
                LootBagMod.id("loot_bag"),
                FabricItemGroup
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
