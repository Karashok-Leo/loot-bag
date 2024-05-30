package net.karashokleo.lootbag.client.screen;

import net.karashokleo.lootbag.LootBag;
import net.karashokleo.lootbag.content.LootEntry;
import net.minecraft.client.gui.widget.ToggleButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;

public class OptionalLootBagScreen extends ScrollableLootBagScreen
{
    private static final String TEXT_OPTIONAL = "text.loot-bag.optional_screen";
    private static final Identifier ARROW = LootBag.id("textures/gui/arrow.png");
    private static final int ARROW_WIDTH = 14;
    private static final int ARROW_HEIGHT = 22;
    private static final int ARROW_X = 100;
    private static final int ARROW_Y = 210;
    private ToggleButtonWidget prevArrow;
    private ToggleButtonWidget nextArrow;

    public OptionalLootBagScreen(LootEntry[] lootEntries, Hand hand)
    {
        super(Text.translatable(TEXT_OPTIONAL), lootEntries, hand);
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
}
