package karashokleo.loot_bag.api.common.icon;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;
import org.lwjgl.opengl.GL14;

public class TextureIcon extends Icon
{
    public static final MapCodec<TextureIcon> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    Identifier.CODEC.fieldOf("texture").forGetter(TextureIcon::getTexture),
                    Codec.FLOAT.optionalFieldOf("u0", 0F).forGetter(TextureIcon::getU0),
                    Codec.FLOAT.optionalFieldOf("v0", 0F).forGetter(TextureIcon::getV0),
                    Codec.FLOAT.optionalFieldOf("u1", 1F).forGetter(TextureIcon::getU1),
                    Codec.FLOAT.optionalFieldOf("v1", 1F).forGetter(TextureIcon::getV1)
            ).and(iconFields()).apply(ins, TextureIcon::new)
    );

    public static final IconType<TextureIcon> TYPE = new IconType<>(CODEC);

    protected final Identifier texture;
    protected final float u0, v0, u1, v1;

    public TextureIcon(Identifier texture, float u0, float v0, float u1, float v1, float scale)
    {
        super(scale);
        this.texture = texture;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public TextureIcon(Identifier texture, float u0, float v0, float u1, float v1)
    {
        super();
        this.texture = texture;
        this.u0 = u0;
        this.v0 = v0;
        this.u1 = u1;
        this.v1 = v1;
    }

    public TextureIcon(Identifier texture)
    {
        this(texture, 0, 0, 1, 1);
    }

    public Identifier getTexture()
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

    @Environment(EnvType.CLIENT)
    @Override
    public void render(DrawContext context, MatrixStack matrices, float alpha, float delta)
    {
        matrices.push();

        matrices.scale(scale, scale, 1);
        matrices.scale(SIZE, SIZE, 1);

        RenderSystem.setShaderTexture(0, texture);
        RenderSystem.setShader(GameRenderer::getPositionTexColorProgram);
        RenderSystem.enableBlend();
        // Blend state was removed from shader JSON in 1.21; keep the original factors.
        RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
        RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
        Matrix4f matrix4f = matrices.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_TEXTURE_COLOR);
        bufferBuilder.vertex(matrix4f, -0.5F, -0.5F, 0)
                .color(1F, 1F, 1F, alpha)
                .texture(u0, v0);
        bufferBuilder.vertex(matrix4f, -0.5F, 0.5F, 0)
                .color(1F, 1F, 1F, alpha)
                .texture(u0, v1);
        bufferBuilder.vertex(matrix4f, 0.5F, 0.5F, 0)
                .color(1F, 1F, 1F, alpha)
                .texture(u1, v1);
        bufferBuilder.vertex(matrix4f, 0.5F, -0.5F, 0)
                .color(1F, 1F, 1F, alpha)
                .texture(u1, v0);
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        matrices.pop();
    }
}
