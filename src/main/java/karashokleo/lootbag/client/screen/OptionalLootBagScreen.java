package karashokleo.lootbag.client.screen;

import karashokleo.lootbag.LootBag;
import karashokleo.lootbag.content.logic.bag.OptionalBag;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.util.List;

public class OptionalLootBagScreen extends ScrollableLootBagScreen<OptionalBag>
{
    protected static final String TEXT_OPTIONAL = "text.loot-bag.optional_screen";
    protected static final Identifier ARROW = LootBag.id("textures/gui/arrow.png");
    protected static final int ARROW_WIDTH = 14;
    protected static final int ARROW_HEIGHT = 22;
    protected static final int ARROW_X = 100;
    protected static final int ARROW_Y = 210;
    protected ToggleButtonWidget prevArrow;
    protected ToggleButtonWidget nextArrow;

    public OptionalLootBagScreen(OptionalBag bag, int slot)
    {
        super(Text.translatable(TEXT_OPTIONAL), bag, slot);
    }

    @Override
    protected void init()
    {
        super.init();
        prevArrow = new ToggleButtonWidget((width - ARROW_X - ARROW_WIDTH) / 2, ARROW_Y - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, true);
        prevArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
        nextArrow = new ToggleButtonWidget((width + ARROW_X - ARROW_WIDTH) / 2, ARROW_Y - ARROW_HEIGHT / 2, ARROW_WIDTH, ARROW_HEIGHT, false);
        nextArrow.setTextureUV(1, 1, ARROW_WIDTH + 2, ARROW_HEIGHT + 2, ARROW);
        addDrawableChild(prevArrow);
        addDrawableChild(nextArrow);
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

    @Override
    protected List<Content> fetchContents()
    {
        return bag.getContents();
    }
}