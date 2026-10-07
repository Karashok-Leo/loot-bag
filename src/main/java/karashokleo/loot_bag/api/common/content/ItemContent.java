package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.api.common.icon.ItemIcon;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.List;

public class ItemContent extends StacksContent
{
    public static final MapCodec<ItemContent> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    CodecUtil.ITEM_STACK_CODEC.fieldOf("item").forGetter(ItemContent::getStack)
            ).and(contentFields(ins).t1()).apply(ins, ItemContent::new)
    );

    public static final ContentType<ItemContent> TYPE = new ContentType<>(CODEC);

    protected final ItemStack stack;

    public ItemContent(ItemStack stack, Icon icon)
    {
        super(icon);
        this.stack = stack;
    }

    public ItemContent(ItemStack stack)
    {
        super(new ItemIcon(stack));
        this.stack = stack;
    }

    public ItemContent(ItemConvertible item)
    {
        super(new ItemIcon(item));
        this.stack = item.asItem().getDefaultStack();
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE;
    }

    @Override
    protected List<ItemStack> getLootStacks(ServerPlayerEntity player)
    {
        return Collections.singletonList(this.getStack().copy());
    }
}
