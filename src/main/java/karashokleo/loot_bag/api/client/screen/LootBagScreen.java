package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.client.render.DrawableIcon;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.internal.network.ClientNetworkHandlers;
import net.minecraft.client.font.MultilineText;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

public abstract class LootBagScreen<B extends Bag> extends Screen
{
    private static final Text TEXT_OPEN = Text.translatable("text.loot-bag.open");
    // colors of the texts
    private static final int TITLE_COLOR = 0xffffff;
    private static final int NAME_COLOR = 0xffffff;
    private static final int DESC_COLOR = 0xffffff;
    // size of the open button
    private static final int OPEN_WIDTH = 72;
    private static final int OPEN_HEIGHT = 24;

    protected final B bag;
    protected final int slot;
    protected ButtonWidget openButton;
    protected Text contentName = Text.empty();
    protected MultilineText contentDesc = MultilineText.EMPTY;

    protected LootBagScreen(Text title, B bag, int slot)
    {
        super(title);
        this.bag = bag;
        this.slot = slot;
    }

    @Override
    protected void init()
    {
        this.openButton = ButtonWidget
                .builder(TEXT_OPEN, button -> open())
                .dimensions((width - OPEN_WIDTH) / 2, this.getOpenY() - OPEN_HEIGHT / 2, OPEN_WIDTH, OPEN_HEIGHT)
                .build();
        addDrawableChild(openButton);
        updateContentText();
    }

    protected int getTitleY()
    {
        return (int) (0.08F * height);
    }

    protected int getNameY()
    {
        return (int) (0.24F * height + Icon.SIZE);
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
        this.renderBackground(context);
        this.setFocused(null);
        this.drawTitle(context);
        this.drawName(context);
        this.drawDescription(context);
        this.updateDrawableIcon(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    protected void updateContentText()
    {
        ContentEntry entry = this.getCurrentContent();
        this.contentName = entry.getName().formatted(Formatting.BOLD);
        this.contentDesc = MultilineText.create(this.textRenderer, entry.getDesc(), this.width / 2);
    }

    protected void drawTitle(DrawContext context)
    {
        context.drawCenteredTextWithShadow(textRenderer, title, width / 2, this.getTitleY(), TITLE_COLOR);
    }

    protected void drawName(DrawContext context)
    {
        context.drawCenteredTextWithShadow(textRenderer, this.contentName, width / 2, this.getNameY(), NAME_COLOR);
    }

    protected void drawDescription(DrawContext context)
    {
        // Font Height 15?
        this.contentDesc.drawCenterWithShadow(context, width / 2, this.getDescY(), this.textRenderer.fontHeight, DESC_COLOR);
    }

    protected abstract void updateDrawableIcon(DrawContext context, int mouseX, int mouseY, float delta);

    protected void updateDrawableIconInternal(DrawableIcon drawableIcon, float offsetX, float scale, float alpha)
    {
//        float x = (width - ICON_SIZE) / 2F;
//        float y = this.getIconY();
//        x += offsetX > 0 ? offsetX * 2 : offsetX;
//        y += (1F - scale) / 2F * ICON_SIZE;
        float x = 0.5F * width + offsetX;
        float y = 0.25F * height;

        drawableIcon.setX((int) x);
        drawableIcon.setY((int) y);
        drawableIcon.setScale(scale);
        drawableIcon.setAlpha(alpha);
    }

    protected abstract void open();

    protected void open(int selectedIndex)
    {
        if (client != null && client.player != null)
            ClientNetworkHandlers.sendOpen(slot, selectedIndex);
        close();
    }

    protected abstract ContentEntry getCurrentContent();

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {

        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        } else if (this.client != null &&
                   this.client.options.inventoryKey.matchesKey(keyCode, scanCode))
        {
            this.close();
            return true;
        } else
        {
            return false;
        }
    }
}
