package karashokleo.loot_bag.internal.mixin;

import karashokleo.loot_bag.internal.item.LootBagItem;
import net.minecraft.component.ComponentType;
import net.minecraft.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ItemStack.class)
public abstract class ItemStackMixin
{
    // Preserve Item.getRarity(stack), removed by the 1.20.5 component migration.
    // Vanilla still applies its enchanted-item rarity promotion afterwards.
    @Redirect(method = "getRarity", at = @At(value = "INVOKE", target = "Lnet/minecraft/item/ItemStack;getOrDefault(Lnet/minecraft/component/ComponentType;Ljava/lang/Object;)Ljava/lang/Object;"))
    private <T> T loot_bag$getRarity(ItemStack stack, ComponentType<T> type, T fallback)
    {
        if (stack.getItem() instanceof LootBagItem item)
            return (T) item.getRarity(stack);
        return stack.getOrDefault(type, fallback);
    }
}
