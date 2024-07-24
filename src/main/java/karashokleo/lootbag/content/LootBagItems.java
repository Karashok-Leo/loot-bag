package karashokleo.lootbag.content;

import karashokleo.lootbag.LootBag;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;

public class LootBagItems
{
    public static final RegistryKey<ItemGroup> ITEM_GROUP_KEY = RegistryKey.of(RegistryKeys.ITEM_GROUP, LootBag.id("loot_bag"));
    public static LootBagItem LOOT_BAG;

    public static void init()
    {
        LOOT_BAG = Registry.register(Registries.ITEM, LootBag.id("loot_bag"), new LootBagItem(new FabricItemSettings().maxCount(16)));

        Registry.register(Registries.ITEM_GROUP, LootBag.id("loot_bag"), FabricItemGroup.builder().icon(() -> ItemStack.EMPTY).displayName(Text.translatable("itemGroup.loot-bag.loot_bag")).build());

        ItemGroupEvents.modifyEntriesEvent(ITEM_GROUP_KEY).register(entries ->
                LootBagRegistry.BAG_REGISTRY.getIds().stream().map(LOOT_BAG::getStack).toList().forEach(entries::add));
    }
}
