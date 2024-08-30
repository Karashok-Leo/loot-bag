package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;

import java.util.Collections;
import java.util.List;

public class ItemContent extends StacksContent
{
    public static final Codec<ItemContent> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    ItemStack.CODEC.fieldOf("item").forGetter(ItemContent::getStack)
            ).and(contentFields(ins).t1()).apply(ins, ItemContent::new)
    );

    public static final ContentType<ItemContent> TYPE = new ContentType<>(CODEC);

    protected final ItemStack stack;

    public ItemContent(ItemStack stack, Icon icon)
    {
        super(icon);
        this.stack = stack;
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
