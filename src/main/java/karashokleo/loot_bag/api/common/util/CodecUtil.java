package karashokleo.loot_bag.api.common.util;

import com.mojang.datafixers.util.Either;
import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.Dynamic;
import com.mojang.serialization.DynamicOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.component.ComponentChanges;
import com.mojang.datafixers.util.Pair;
import net.minecraft.datafixer.TypeReferences;
import net.minecraft.SharedConstants;
import net.minecraft.datafixer.Schemas;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtOps;
import net.minecraft.registry.Registries;

public class CodecUtil
{
    // Keep the original codec's unrestricted integer count and support for empty/air stacks.
    private static final Codec<ItemStack> COMPONENT_STACK_CODEC = RecordCodecBuilder.create(
            instance -> instance.group(
                    Registries.ITEM.getEntryCodec().fieldOf("id").forGetter(ItemStack::getRegistryEntry),
                    Codec.INT.fieldOf("count").orElse(1).forGetter(ItemStack::getCount),
                    ComponentChanges.CODEC.optionalFieldOf("components", ComponentChanges.EMPTY).forGetter(ItemStack::getComponentChanges)
            ).apply(instance, ItemStack::new)
    );

    // ItemStack NBT became components in 1.20.5. Keep reading 1.20.1 datapacks.
    private static final Codec<ItemStack> STACK_CODEC = new Codec<>()
    {
        @Override
        public <T> DataResult<Pair<ItemStack, T>> decode(DynamicOps<T> ops, T input)
        {
            Dynamic<T> data = new Dynamic<>(ops, input);
            if (data.get("Count").result().isPresent() || data.get("tag").result().isPresent())
            {
                int count = data.get("Count").asInt(1);
                data = data.set("Count", data.createByte((byte) 1));
                data = Schemas.getFixer().update(TypeReferences.ITEM_STACK, data.convert(NbtOps.INSTANCE), 3465, SharedConstants.getGameVersion().getSaveVersion().getId()).convert(ops);
                input = data.set("count", data.createInt(count)).getValue();
            }
            return COMPONENT_STACK_CODEC.decode(ops, input);
        }

        @Override
        public <T> DataResult<T> encode(ItemStack input, DynamicOps<T> ops, T prefix)
        {
            return COMPONENT_STACK_CODEC.encode(input, ops, prefix);
        }
    };

    public static final Codec<ItemStack> ITEM_STACK_CODEC = Codec.either(
            Registries.ITEM.getCodec(),
            STACK_CODEC
    ).xmap(
            either -> either.map(Item::getDefaultStack, stack -> stack),
            stack -> stack.getCount() != 1 || !stack.getComponentChanges().isEmpty() ? Either.right(stack) : Either.left(stack.getItem())
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
