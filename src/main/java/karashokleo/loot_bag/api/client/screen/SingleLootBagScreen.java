package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.client.render.DrawableIcon;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;

public class SingleLootBagScreen extends LootBagScreen<SingleBag>
{
    protected static final Component TEXT_SINGLE = Component.translatable("text.loot-bag.single_screen");
    protected DrawableIcon icon;

    public SingleLootBagScreen(SingleBag bag, int slot)
    {
        super(TEXT_SINGLE, bag, slot);
    }

    @Override
    protected void init()
    {
        super.init();
        this.icon = new DrawableIcon(this.getCurrentContent().content().getIcon());
        this.addRenderableOnly(icon);
    }

    @Override
    protected void updateDrawableIcon(GuiGraphics context, int mouseX, int mouseY, float delta)
    {
        this.updateDrawableIconInternal(icon, 0, 1, 1F);
    }

    @Override
    protected ContentEntry getCurrentContent()
    {
        return bag.getContent();
    }

    @Override
    protected void open()
    {
        super.open(0);
    }
}
