package karashokleo.loot_bag.internal.mixin;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.internal.item.LootBagItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    @Inject(method = "getRarity", at = @At("RETURN"), cancellable = true)
    private void lootBag$getConfiguredRarity(CallbackInfoReturnable<Rarity> cir)
    {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof LootBagItem item)
        {
            Rarity rarity = item.getBag(stack).map(Bag::getRarity).orElse(Rarity.COMMON);
            // Apply vanilla's enchantment promotion to the current bag definition.
            cir.setReturnValue(stack.hasEnchantments() ? switch (rarity)
            {
                case COMMON, UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                default -> rarity;
            } : rarity);
        }
    }
}
