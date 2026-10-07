package karashokleo.loot_bag.internal.mixin;

import karashokleo.loot_bag.internal.item.LootBagItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Rarity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    @Inject(method = "getRarity", at = @At("HEAD"), cancellable = true)
    private void lootBag$getConfiguredRarity(CallbackInfoReturnable<Rarity> cir)
    {
        ItemStack stack = (ItemStack) (Object) this;
        if (stack.getItem() instanceof LootBagItem item)
        {
            Rarity rarity = item.getRarity(stack);
            cir.setReturnValue(stack.isEnchanted() ? switch (rarity)
            {
                case COMMON, UNCOMMON -> Rarity.RARE;
                case RARE -> Rarity.EPIC;
                default -> rarity;
            } : rarity);
        }
    }
}
