package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.client.render.DrawableIcon;
import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SingleLootBagScreen extends LootBagScreen<SingleBag>
{
    protected static final Text TEXT_SINGLE = Text.translatable("text.loot-bag.single_screen");
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
        this.addDrawable(icon);
    }

    @Override
    protected void updateDrawableIcon(DrawContext context, int mouseX, int mouseY, float delta)
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
