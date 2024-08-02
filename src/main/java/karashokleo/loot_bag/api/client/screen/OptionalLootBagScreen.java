package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.api.common.bag.OptionalBag;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

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
        prevArrow = new ToggleButtonWidget((width - ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, true);
        prevArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
        nextArrow = new ToggleButtonWidget((width + ARROW_X_OFFSET - ARROW_WIDTH) / 2, this.getArrowY() - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, false);
        nextArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
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
}
