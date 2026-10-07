package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.client.render.DrawableIcon;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.api.common.icon.Icon;
import karashokleo.loot_bag.internal.network.ClientNetworkHandlers;
import net.minecraft.client.gui.components.MultiLineLabel;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.components.Button;
import net.minecraft.network.chat.Component;
import net.minecraft.ChatFormatting;

public abstract class LootBagScreen<B extends Bag> extends Screen
{
    private static final Component TEXT_OPEN = Component.translatable("text.loot-bag.open");
    // colors of the texts
    private static final int TITLE_COLOR = 0xffffff;
    private static final int NAME_COLOR = 0xffffff;
    private static final int DESC_COLOR = 0xffffff;
    // size of the open button
    private static final int OPEN_WIDTH = 72;
    private static final int OPEN_HEIGHT = 24;

    protected final B bag;
    protected final int slot;
    protected Button openButton;
    protected Component contentName = Component.empty();
    protected MultiLineLabel contentDesc = MultiLineLabel.EMPTY;

    protected LootBagScreen(Component title, B bag, int slot)
    {
        super(title);
        this.bag = bag;
        this.slot = slot;
    }

    @Override
    protected void init()
    {
        this.openButton = Button
                .builder(TEXT_OPEN, button -> open())
                .bounds((width - OPEN_WIDTH) / 2, this.getOpenY() - OPEN_HEIGHT / 2, OPEN_WIDTH, OPEN_HEIGHT)
                .build();
        addRenderableWidget(openButton);
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
    public boolean isPauseScreen()
    {
        return false;
    }

    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        this.renderTransparentBackground(context);
        this.setFocused(null);
        this.drawTitle(context);
        this.drawName(context);
        this.drawDescription(context);
        this.updateDrawableIcon(context, mouseX, mouseY, delta);
        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public void renderBackground(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        // The original gradient is drawn before the labels; 1.21 Screen.render calls this again.
    }

    protected void updateContentText()
    {
        ContentEntry entry = this.getCurrentContent();
        this.contentName = entry.getName().withStyle(ChatFormatting.BOLD);
        this.contentDesc = MultiLineLabel.create(this.font, entry.getDesc(), this.width / 2);
    }

    protected void drawTitle(GuiGraphics context)
    {
        context.drawCenteredString(font, title, width / 2, this.getTitleY(), TITLE_COLOR);
    }

    protected void drawName(GuiGraphics context)
    {
        context.drawCenteredString(font, this.contentName, width / 2, this.getNameY(), NAME_COLOR);
    }

    protected void drawDescription(GuiGraphics context)
    {
        // Font Height 15?
        this.contentDesc.renderCentered(context, width / 2, this.getDescY(), this.font.lineHeight, DESC_COLOR);
    }

    protected abstract void updateDrawableIcon(GuiGraphics context, int mouseX, int mouseY, float delta);

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
        if (minecraft != null && minecraft.player != null)
            ClientNetworkHandlers.sendOpen(slot, selectedIndex);
        onClose();
    }

    protected abstract ContentEntry getCurrentContent();

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers)
    {

        if (super.keyPressed(keyCode, scanCode, modifiers))
        {
            return true;
        } else if (this.minecraft != null &&
                   this.minecraft.options.keyInventory.matches(keyCode, scanCode))
        {
            this.onClose();
            return true;
        } else
        {
            return false;
        }
    }
}
