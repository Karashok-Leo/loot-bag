package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.api.common.icon.ItemIcon;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;

import java.util.Collections;
import java.util.List;

public class ItemContent extends StacksContent
{
    public static final MapCodec<ItemContent> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    CodecUtil.ITEM_STACK_CODEC.fieldOf("item").forGetter(ItemContent::getStack)
            ).and(contentFields(ins).t1()).apply(ins, ItemContent::new)
    );

    public static final DeferredHolder<ContentType<?>, ContentType<ItemContent>> TYPE = LootBagRegistry.ITEM_CONTENT;

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

    public ItemContent(ItemLike item)
    {
        super(new ItemIcon(item));
        this.stack = item.asItem().getDefaultInstance();
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE.get();
    }

    @Override
    protected List<ItemStack> getLootStacks(ServerPlayer player)
    {
        return Collections.singletonList(this.getStack().copy());
    }
}
