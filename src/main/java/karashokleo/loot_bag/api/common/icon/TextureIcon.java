package karashokleo.loot_bag.api.common.icon;

import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.GameRenderer;
import com.mojang.blaze3d.vertex.*;
import com.mojang.blaze3d.vertex.PoseStack;
import net.minecraft.resources.ResourceLocation;
import org.joml.Matrix4f;

public class TextureIcon extends Icon
{
    public static final MapCodec<TextureIcon> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    ResourceLocation.CODEC.fieldOf("texture").forGetter(TextureIcon::getTexture),
                    Codec.FLOAT.optionalFieldOf("u0", 0F).forGetter(TextureIcon::getU0),
                    Codec.FLOAT.optionalFieldOf("v0", 0F).forGetter(TextureIcon::getV0),
                    Codec.FLOAT.optionalFieldOf("u1", 1F).forGetter(TextureIcon::getU1),
                    Codec.FLOAT.optionalFieldOf("v1", 1F).forGetter(TextureIcon::getV1)
            ).and(iconFields()).apply(ins, TextureIcon::new)
    );

    public static final IconType<TextureIcon> TYPE = new IconType<>(CODEC);

    protected final ResourceLocation texture;
    protected final float u0, v0, u1, v1;

    public TextureIcon(ResourceLocation texture, float u0, float v0, float u1, float v1, float scale)
    {
        super(scale);
        this.texture = texture;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public TextureIcon(ResourceLocation texture, float u0, float v0, float u1, float v1)
    {
        super();
        this.texture = texture;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public TextureIcon(ResourceLocation texture)
    {
        this(texture, 0, 0, 1, 1);
    }

    public ResourceLocation getTexture()
    {
        return texture;
    }

    public float getU0()
    {
        return u0;
    }

    public float getV0()
    {
        return v0;
    }

    public float getU1()
    {
        return u1;
    }

    public float getV1()
    {
        return v1;
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
        matrices.pushPose();

        matrices.scale(scale, scale, 1);
        matrices.scale(SIZE, SIZE, 1);

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorShader);
        RenderSystem.enableBlend();
        // Match the blend state formerly supplied by the 1.20 position_color_tex shader.
        RenderSystem.blendEquation(org.lwjgl.opengl.GL14.GL_FUNC_ADD);
        RenderSystem.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        Matrix4f matrix4f = matrices.last().pose();
        BufferBuilder bufferBuilder = Tesselator.getInstance().begin(VertexFormat.Mode.QUADS, DefaultVertexFormat.POSITION_TEX_COLOR);
        bufferBuilder.addVertex(matrix4f, -0.5F, -0.5F, 0)
                .setColor(1F, 1F, 1F, alpha)
                .setUv(u0, v0);
        bufferBuilder.addVertex(matrix4f, -0.5F, 0.5F, 0)
                .setColor(1F, 1F, 1F, alpha)
                .setUv(u0, v1);
        bufferBuilder.addVertex(matrix4f, 0.5F, 0.5F, 0)
                .setColor(1F, 1F, 1F, alpha)
                .setUv(u1, v1);
        bufferBuilder.addVertex(matrix4f, 0.5F, -0.5F, 0)
                .setColor(1F, 1F, 1F, alpha)
                .setUv(u1, v0);
        BufferUploader.drawWithShader(bufferBuilder.buildOrThrow());
        RenderSystem.disableBlend();

        matrices.popPose();
    }
}
