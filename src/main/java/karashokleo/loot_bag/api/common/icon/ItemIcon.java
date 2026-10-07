package karashokleo.loot_bag.api.common.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.item.ItemStack;

public class ItemIcon extends Icon
{
    public static final MapCodec<ItemIcon> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    CodecUtil.ITEM_STACK_CODEC.fieldOf("item").forGetter(ItemIcon::getStack)
            ).and(iconFields()).apply(ins, ItemIcon::new)
    );

    public static final IconType<ItemIcon> TYPE = new IconType<>(CODEC);

    protected final ItemStack stack;

    public ItemIcon(ItemStack stack, float scale)
    {
        super(scale);
        this.stack = stack;
    }

    public ItemIcon(ItemStack stack)
    {
        super();
        this.stack = stack;
    }

    public ItemIcon(ItemLike item)
    {
        this(item.asItem().getDefaultInstance());
    }

    public ItemStack getStack()
    {
        return stack;
    }

    @Override
    public IconType<?> getType()
    {
        return TYPE;
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void render(GuiGraphics context, PoseStack matrices, float alpha, float delta)
    {
        if (stack.isEmpty()) return;

        matrices.pushPose();
//        matrices.multiplyPositionMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        matrices.scale(scale, scale, 1);
        matrices.scale(SIZE, SIZE, 1);
        matrices.scale(1 / 16F, 1 / 16F, 1);

        context.renderItem(stack, -8, -8);

        // get the item renderer from the minecraft singleton
//        ItemRenderer itemRenderer = Minecraft.getInstance().getItemRenderer();
//
//        BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);
//
//        boolean bl = !bakedModel.isSideLit();
//        if (bl)
//        {
//            DiffuseLighting.disableGuiDepthLighting();
//        }
//
//        // use AlphaVertexConsumerWrapper if translucent
//        MultiBufferSource.Immediate vertexConsumers = context.getVertexConsumers();
//        var vertexConsumerProvider = this.isTranslucent() ? new AlphaVertexConsumerWrapper.Provider(vertexConsumers, (int) (alpha * 255)) : vertexConsumers;
//
//        itemRenderer.renderItem(stack, ModelTransformationMode.GUI, false, matrices, vertexConsumerProvider, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
//        context.draw();
//        if (bl)
//        {
//            DiffuseLighting.enableGuiDepthLighting();
//        }

        matrices.popPose();
    }
}
