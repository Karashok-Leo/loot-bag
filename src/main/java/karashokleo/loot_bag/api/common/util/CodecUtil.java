package karashokleo.loot_bag.api.common.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;

public class CodecUtil
{
    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.either(
            Registries.ITEM.getCodec(),
            ItemStack.CODEC
    ).xmap(
            either -> either.map(Item::getDefaultStack, stack -> stack),
            stack -> stack.getCount() != 1 || stack.hasNbt() ? Either.right(stack) : Either.left(stack.getItem())
    );

    public static <E extends Enum<E>> Codec<E> getEnumCodec(Class<E> cls)
    {
        return Codec.STRING.comapFlatMap(name -> validate(cls, name), E::name);
    }

    public static <E extends Enum<E>> DataResult<E> validate(Class<E> cls, String name)
    {
        try
        {
            return DataResult.success(E.valueOf(cls, name));
        } catch (IllegalArgumentException e)
        {
            return DataResult.error(e::getMessage);
        }
    }
}
