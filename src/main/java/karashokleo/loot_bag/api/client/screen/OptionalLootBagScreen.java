package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import net.minecraft.client.gui.components.StateSwitchingButton;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class OptionalLootBagScreen extends ScrollableLootBagScreen<OptionalBag>
{
    protected static final Component TEXT_OPTIONAL = Component.translatable("text.loot-bag.optional_screen");
    protected static final ResourceLocation ARROW = LootBagMod.id("textures/gui/arrow.png");
    protected static final int ARROW_WIDTH = 14;
    protected static final int ARROW_HEIGHT = 22;
    protected static final int ARROW_X_OFFSET = 100;
    protected ArrowButton prevArrow;
    protected ArrowButton nextArrow;

    public OptionalLootBagScreen(OptionalBag bag, int slot)
    {
        super(TEXT_OPTIONAL, bag, slot);
    }

    @Override
    protected void init()
    {
        super.init();
        prevArrow = new ArrowButton((width - ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, true);
        prevArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
        nextArrow = new ArrowButton((width + ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, false);
        nextArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
        addRenderableWidget(prevArrow);
        addRenderableWidget(nextArrow);
    }

    protected int getArrowY()
    {
        return this.getOpenY();
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button)
    {
        if (prevArrow.mouseClicked(mouseX, mouseY, button))
        {
            prev();
            return true;
        }
        if (nextArrow.mouseClicked(mouseX, mouseY, button))
        {
            next();
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    // 1.21 uses sprites for StateSwitchingButton; keep the existing arrow atlas and UVs.
    protected static class ArrowButton extends StateSwitchingButton
    {
        private int u, v, triggeredOffset, hoveredOffset;
        private ResourceLocation texture;

        public ArrowButton(int x, int y, int width, int height, boolean triggered)
        {
            super(x, y, width, height, triggered);
        }

        public void setTextureUV(int u, int v, int triggeredOffset, int hoveredOffset, ResourceLocation texture)
        {
            this.u = u;
            this.v = v;
            this.triggeredOffset = triggeredOffset;
            this.hoveredOffset = hoveredOffset;
            this.texture = texture;
        }

        @Override
        public void renderWidget(GuiGraphics context, int mouseX, int mouseY, float delta)
        {
            RenderSystem.disableDepthTest();
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(org.lwjgl.opengl.GL14.GL_FUNC_ADD);
            RenderSystem.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
            context.blit(texture, getX(), getY(), u + (isStateTriggered ? triggeredOffset : 0),
                    v + (isHoveredOrFocused() ? hoveredOffset : 0), width, height);
            RenderSystem.enableDepthTest();
        }
    }
}
