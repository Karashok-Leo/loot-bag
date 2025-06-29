package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.client.render.DrawableIcon;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.ContentView;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.math.MathHelper;

import java.util.List;

public abstract class ScrollableLootBagScreen<B extends Bag & ContentView> extends LootBagScreen<B>
{
    protected static final int MAX_OFFSET = 10;
    protected static final float OFFSET_MUL = 6;
    protected int currentIndex = 0;
    protected int offset = 0;
    protected List<ContentEntry> contents;
    protected DrawableIcon previousIcon;
    protected DrawableIcon currentIcon;

    protected ScrollableLootBagScreen(Text title, B bag, int slot)
    {
        super(title, bag, slot);
    }

    @Override
    protected void init()
    {
        super.init();
        this.previousIcon = new DrawableIcon(getPreviousContent().content().getIcon());
        this.currentIcon = new DrawableIcon(getCurrentContent().content().getIcon());
        this.addDrawable(this.previousIcon);
        this.addDrawable(this.currentIcon);
    }

    @Override
    protected void updateDrawableIcon(DrawContext context, int mouseX, int mouseY, float delta)
    {
        updateDrawableIconInternal(
                this.previousIcon,
                (offset - MAX_OFFSET * getDirection()) * OFFSET_MUL,
                getPercent(),
                getPercent()
        );
        updateDrawableIconInternal(
                this.currentIcon,
                offset * OFFSET_MUL,
                1F - getPercent(),
                1F - getPercent()
        );
    }

    @Override
    public void tick()
    {
        if (offset > 0) offset--;
        else if (offset < 0) offset++;
    }

    protected void prev()
    {
        currentIndex--;
        currentIndex = adaptIndex(currentIndex);
        offset = -MAX_OFFSET;
        this.updateIcon();
        this.updateContentText();
    }

    protected void next()
    {
        currentIndex++;
        currentIndex = adaptIndex(currentIndex);
        offset = MAX_OFFSET;
        this.updateIcon();
        this.updateContentText();
    }

    protected int adaptIndex(int index)
    {
        int length = getContents().size();
        if (index > length - 1)
            return 0;
        if (index < 0)
            return length - 1;
        return index;
    }

    protected void updateIcon()
    {
        this.previousIcon.setIcon(getPreviousContent().content().getIcon());
        this.currentIcon.setIcon(getCurrentContent().content().getIcon());
    }

    protected int getDirection()
    {
        return MathHelper.sign(offset);
    }

    protected float getPercent()
    {
        return 1F * Math.abs(offset) / MAX_OFFSET;
    }

    protected List<ContentEntry> fetchContents()
    {
        return bag.getContents();
    }

    protected List<ContentEntry> getContents()
    {
        if (this.contents == null)
            this.contents = this.fetchContents();
        return this.contents;
    }

    @Override
    protected ContentEntry getCurrentContent()
    {
        return this.getContents().get(this.currentIndex);
    }

    protected ContentEntry getPreviousContent()
    {
        int previousIndex = currentIndex - this.getDirection();
        previousIndex = adaptIndex(previousIndex);
        return getContents().get(previousIndex);
    }

    @Override
    protected void open()
    {
        super.open(currentIndex);
    }
}
