package karashokleo.lootbag.client.screen;

import com.mojang.blaze3d.systems.RenderSystem;
import karashokleo.lootbag.content.logic.bag.Bag;
import karashokleo.lootbag.content.logic.content.Content;
import karashokleo.lootbag.network.ClientNetwork;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;
import org.joml.Matrix4f;

public abstract class LootBagScreen<B extends Bag> extends Screen
{
    private static final Text TEXT_OPEN = Text.translatable("text.loot-bag.open");
    private static final int TITLE_COLOR = 0xffffff;
    private static final int NAME_COLOR = 0xffffff;
    private static final int DESC_COLOR = 0xffffff;
    private static final float ICON_SIZE = 64;
    private static final int OPEN_WIDTH = 72;
    private static final int OPEN_HEIGHT = 24;
    protected final B bag;
    protected final int slot;
    protected ButtonWidget openButton;

    protected LootBagScreen(Text title, B bag, int slot)
    {
        super(title);
        this.bag = bag;
        this.slot = slot;
    }

    @Override
    protected void init()
    {
        openButton = ButtonWidget
                .builder(TEXT_OPEN, button -> open())
                .dimensions((width - OPEN_WIDTH) / 2, this.getOpenY() - OPEN_HEIGHT / 2, OPEN_WIDTH, OPEN_HEIGHT)
                .build();
        addDrawableChild(openButton);
    }

    protected int getTitleY()
    {
        return (int) (0.08F * height);
    }

    protected float getIconY()
    {
        return 0.16F * height;
    }

    protected int getNameY()
    {
        return (int) (0.24F * height + ICON_SIZE);
    }

    protected int getDescY()
    {
        return this.getNameY() + 20;
    }

    protected int getOpenY()
    {
        return (int) (0.84F * height);
    }

    @Override
    public boolean shouldPause()
    {
        return false;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        renderBackground(context);
        this.setFocused(null);
        drawTitle(context);
        drawName(context);
        drawDescription(context);
        super.render(context, mouseX, mouseY, delta);
    }

    protected void drawTitle(DrawContext context)
    {
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, this.getTitleY(), TITLE_COLOR);
    }

    protected void drawName(DrawContext context)
    {
        context.drawCenteredTextWithShadow(textRenderer, getCurrentContent().getName().formatted(Formatting.BOLD), width / 2, this.getNameY(), NAME_COLOR);
    }

    protected void drawDescription(DrawContext context)
    {
        for (int i = 0; i < getCurrentContent().getDescriptionLines(); i++)
            context.drawCenteredTextWithShadow(textRenderer, getCurrentContent().getDesc(i), width / 2, this.getDescY() + i * 15, DESC_COLOR);
    }

    protected void drawIcon(DrawContext context, Content.Icon icon, float offsetX, float scale, float alpha)
    {
        float scaleW = ICON_SIZE / icon.width();

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();

        matrixStack.translate((width - ICON_SIZE) / 2F, this.getIconY(), 0);
        matrixStack.translate(offsetX > 0 ? offsetX * 2 : offsetX, (1F - scale) / 2F * ICON_SIZE, 0);

        matrixStack.scale(scaleW, scaleW, scaleW);
        matrixStack.scale(scale, scale, scale);

        RenderSystem.setShaderTexture(0, icon.texture());
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, 0, 0, 0).color(1F, 1F, 1F, alpha).texture(0, 0).next();
        bufferBuilder.vertex(matrix4f, 0, icon.height(), 0).color(1F, 1F, 1F, alpha).texture(0, 1).next();
        bufferBuilder.vertex(matrix4f, icon.width(), icon.height(), 0).color(1F, 1F, 1F, alpha).texture(1, 1).next();
        bufferBuilder.vertex(matrix4f, icon.width(), 0, 0).color(1F, 1F, 1F, alpha).texture(1, 0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        matrixStack.pop();
    }

    protected abstract void open();

    protected void open(int selectedIndex)
    {
        if (client != null && client.player != null)
            ClientNetwork.sendOpen(slot, selectedIndex);
        close();
    }

    protected abstract Content getCurrentContent();
}
