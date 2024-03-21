package net.karashokleo.lootbag.client;

import com.mojang.blaze3d.systems.RenderSystem;
import net.karashokleo.lootbag.config.DefaultConfig.Entry.LootEntry;
import net.karashokleo.lootbag.network.ClientNetwork;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.client.render.*;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.text.StringVisitable;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import org.joml.Matrix4f;

public class BaseLootBagScreen extends Screen
{
    private static final String TEXT_OPEN = "text.loot-bag.open";
    private static final int OPEN_WIDTH = 72;
    private static final int OPEN_HEIGHT = 24;
    private static final int OPEN_Y = 210;
    private static final int TITLE_Y = 20;
    private static final int TITLE_COLOR = 0xffffff;
    private static final int NAME_Y = 120;
    private static final int NAME_COLOR = 0xffffff;
    private static final int DESC_Y = 140;
    private static final int DESC_COLOR = 0xffffff;
    private static final float ICON_SIZE = 64;
    private static final float ICON_Y = 40;
    protected int currentEntryIndex = 0;
    protected LootEntry[] lootEntries;
    protected Hand hand;
    protected ButtonWidget openButton;
    protected MultilineText description;

    protected BaseLootBagScreen(Text title, LootEntry[] lootEntries, Hand hand)
    {
        super(title);
        this.lootEntries = lootEntries;
        this.hand = hand;
    }

    @Override
    protected void init()
    {
        openButton = ButtonWidget
                .builder(Text.translatable(TEXT_OPEN), button -> open())
                .dimensions((width - OPEN_WIDTH) / 2, OPEN_Y - OPEN_HEIGHT / 2, OPEN_WIDTH, OPEN_HEIGHT)
                .build();
        addDrawableChild(openButton);
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
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, TITLE_Y, TITLE_COLOR);
    }

    protected void drawName(DrawContext context)
    {
        context.drawCenteredTextWithShadow(textRenderer, Text.translatable(getCurrentEntry().name), width / 2, NAME_Y, NAME_COLOR);
    }

    protected void drawDescription(DrawContext context)
    {
        if (description == null)
            description = MultilineText.create(textRenderer, StringVisitable.plain(getCurrentEntry().description), 100);
        description.drawCenterWithShadow(context, width / 2, DESC_Y, 10, DESC_COLOR);
//        context.drawCenteredTextWithShadow(textRenderer, Text.translatable(getCurrentEntry().description), width / 2, DESC_Y, DESC_COLOR);
    }

    protected void drawIcon(DrawContext context, LootEntry.Icon icon, float offsetX, float scale, float alpha)
    {
        float scaleW = ICON_SIZE / icon.width;

        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();

        matrixStack.translate((width - ICON_SIZE) / 2F, ICON_Y, 0);
        matrixStack.translate(offsetX > 0 ? offsetX * 2 : offsetX, (1F - scale) / 2F * ICON_SIZE, 0);

        matrixStack.scale(scaleW, scaleW, scaleW);
        matrixStack.scale(scale, scale, scale);

        RenderSystem.setShaderTexture(0, new Identifier(icon.texture));
        RenderSystem.setShader(GameRenderer::getPositionColorTexProgram);
        RenderSystem.enableBlend();
        Matrix4f matrix4f = matrixStack.peek().getPositionMatrix();
        BufferBuilder bufferBuilder = Tessellator.getInstance().getBuffer();
        bufferBuilder.begin(VertexFormat.DrawMode.QUADS, VertexFormats.POSITION_COLOR_TEXTURE);
        bufferBuilder.vertex(matrix4f, 0, 0, 0).color(1F, 1F, 1F, alpha).texture(0, 0).next();
        bufferBuilder.vertex(matrix4f, 0, icon.height, 0).color(1F, 1F, 1F, alpha).texture(0, 1).next();
        bufferBuilder.vertex(matrix4f, icon.width, icon.height, 0).color(1F, 1F, 1F, alpha).texture(1, 1).next();
        bufferBuilder.vertex(matrix4f, icon.width, 0, 0).color(1F, 1F, 1F, alpha).texture(1, 0).next();
        BufferRenderer.drawWithGlobalProgram(bufferBuilder.end());
        RenderSystem.disableBlend();

        matrixStack.pop();
    }

    protected void open()
    {
        if (client != null && client.player != null)
            ClientNetwork.sendOpen(client.player, currentEntryIndex, hand);
        close();
    }

    protected void prev()
    {
        description = null;
        currentEntryIndex--;
        currentEntryIndex = adapt(currentEntryIndex);
    }

    protected void next()
    {
        description = null;
        currentEntryIndex++;
        currentEntryIndex = adapt(currentEntryIndex);
    }

    protected int adapt(int index)
    {
        if (index > lootEntries.length - 1)
            return 0;
        if (index < 0)
            return lootEntries.length - 1;
        return index;
    }

    protected LootEntry getCurrentEntry()
    {
        return lootEntries[currentEntryIndex];
    }
}
