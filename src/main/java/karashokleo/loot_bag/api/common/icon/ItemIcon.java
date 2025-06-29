package karashokleo.loot_bag.api.common.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.client.render.AlphaVertexConsumerWrapper;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.DiffuseLighting;
import net.minecraft.client.render.OverlayTexture;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.item.ItemRenderer;
import net.minecraft.client.render.model.BakedModel;
import net.minecraft.client.render.model.json.ModelTransformationMode;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.item.ItemConvertible;
import net.minecraft.item.ItemStack;
import org.joml.Matrix4f;

public class ItemIcon extends Icon
{
    public static final Codec<ItemIcon> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    CodecUtil.ITEM_STACK_CODEC.fieldOf("item").forGetter(ItemIcon::getStack)
            ).and(iconFields(ins)).apply(ins, ItemIcon::new)
    );

    public static final IconType<ItemIcon> TYPE = new IconType<>(CODEC);

    protected final ItemStack stack;

    public ItemIcon(ItemStack stack, float scale, boolean translucent)
    {
        super(scale, translucent);
        this.stack = stack;
    }

    public ItemIcon(ItemStack stack)
    {
        super();
        this.stack = stack;
    }

    public ItemIcon(ItemConvertible item)
    {
        this(item.asItem().getDefaultStack());
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

    @Environment(EnvType.CLIENT)
    @Override
    public void render(DrawContext context, MatrixStack matrices, float alpha, float delta)
    {
        if (stack.isEmpty()) return;

        matrices.push();
        matrices.multiplyPositionMatrix(new Matrix4f().scaling(1.0F, -1.0F, 1.0F));
        matrices.scale(scale, scale, 1);
        matrices.scale(SIZE, SIZE, 1);

        // get the item renderer from the client singleton
        ItemRenderer itemRenderer = MinecraftClient.getInstance().getItemRenderer();

        BakedModel bakedModel = itemRenderer.getModel(stack, null, null, 0);

        boolean bl = !bakedModel.isSideLit();
        if (bl)
        {
            DiffuseLighting.disableGuiDepthLighting();
        }

        // use AlphaVertexConsumerWrapper if translucent
        VertexConsumerProvider.Immediate vertexConsumers = context.getVertexConsumers();
        var vertexConsumerProvider = this.isTranslucent() ? new AlphaVertexConsumerWrapper.Provider(vertexConsumers, (int) (alpha * 255)) : vertexConsumers;

        itemRenderer.renderItem(stack, ModelTransformationMode.GUI, false, matrices, vertexConsumerProvider, 15728880, OverlayTexture.DEFAULT_UV, bakedModel);
        context.draw();
        if (bl)
        {
            DiffuseLighting.enableGuiDepthLighting();
        }

        matrices.pop();
    }
}
