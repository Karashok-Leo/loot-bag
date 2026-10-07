package karashokleo.loot_bag.api.client.screen;

import com.mojang.blaze3d.platform.GlStateManager;
import com.mojang.blaze3d.systems.RenderSystem;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import org.lwjgl.opengl.GL14;

public class OptionalLootBagScreen extends ScrollableLootBagScreen<OptionalBag>
{
    protected static final Text TEXT_OPTIONAL = Text.translatable("text.loot-bag.optional_screen");
    protected static final Identifier ARROW = LootBagMod.id("textures/gui/arrow.png");
    protected static final int ARROW_WIDTH = 14;
    protected static final int ARROW_HEIGHT = 22;
    protected static final int ARROW_X_OFFSET = 100;
    protected ToggleButtonWidget prevArrow;
    protected ToggleButtonWidget nextArrow;

    public OptionalLootBagScreen(OptionalBag bag, int slot)
    {
        super(TEXT_OPTIONAL, bag, slot);
    }

    @Override
    protected void init()
    {
        super.init();
        prevArrow = new ArrowButtonWidget((width - ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, true);
        nextArrow = new ArrowButtonWidget((width + ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, false);
        addDrawableChild(prevArrow);
        addDrawableChild(nextArrow);
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

    private static class ArrowButtonWidget extends ToggleButtonWidget
    {
        private ArrowButtonWidget(int x, int y, int width, int height, boolean toggled)
        {
            super(x, y, width, height, toggled);
        }

        @Override
        public void renderWidget(DrawContext context, int mouseX, int mouseY, float delta)
        {
            // Keep the original texture atlas and UVs after vanilla removed setTextureUV.
            RenderSystem.disableDepthTest();
            int u = 1 + (this.isToggled() ? ARROW_WIDTH + 2 : 0);
            int v = 1 + (this.isSelected() ? ARROW_HEIGHT + 2 : 0);
            // The original position_tex shader enabled alpha blending, including for resource packs.
            RenderSystem.enableBlend();
            RenderSystem.blendEquation(GL14.GL_FUNC_ADD);
            RenderSystem.blendFunc(GlStateManager.SrcFactor.SRC_ALPHA, GlStateManager.DstFactor.ONE_MINUS_SRC_ALPHA);
            context.drawTexture(ARROW, this.getX(), this.getY(), u, v, this.width, this.height);
            RenderSystem.enableDepthTest();
        }
    }
}
