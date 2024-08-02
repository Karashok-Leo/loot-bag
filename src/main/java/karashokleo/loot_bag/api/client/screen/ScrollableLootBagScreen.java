package karashokleo.loot_bag.api.client.screen;

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

    protected ScrollableLootBagScreen(Text title, B bag, int slot)
    {
        super(title, bag, slot);
    }

    @Override
    protected void drawIcon(DrawContext context)
    {
        drawIcon(context, this.getCurrentContent().content().getIcon(), offset * OFFSET_MUL, 1F - getPercent(), 1F - getPercent());
        drawIcon(context, this.getPreviousContent().content().getIcon(), (offset - MAX_OFFSET * getDirection()) * OFFSET_MUL, getPercent(), getPercent());
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
        currentIndex = adapt(currentIndex);
        offset = -MAX_OFFSET;
        this.updateContent();
    }

    protected void next()
    {
        currentIndex++;
        currentIndex = adapt(currentIndex);
        offset = MAX_OFFSET;
        this.updateContent();
    }

    protected int adapt(int index)
    {
        int length = getContents().size();
        if (index > length - 1)
            return 0;
        if (index < 0)
            return length - 1;
        return index;
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
        previousIndex = adapt(previousIndex);
        return getContents().get(previousIndex);
    }

    @Override
    protected void open()
    {
        super.open(currentIndex);
    }
}
