package karashokleo.lootbag.api.client.screen;

import karashokleo.lootbag.api.common.bag.ContentView;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.content.Content;
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
    protected List<Content> contents;

    protected ScrollableLootBagScreen(Text title, B bag, int slot)
    {
        super(title, bag, slot);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawIcon(context, getCurrentContent().getIcon(), offset * OFFSET_MUL, 1F - getPercent(), 1F - getPercent());
        drawIcon(context, getPreviousContent().getIcon(), (offset - MAX_OFFSET * getDirection()) * OFFSET_MUL, getPercent(), getPercent());
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
    }

    protected void next()
    {
        currentIndex++;
        currentIndex = adapt(currentIndex);
        offset = MAX_OFFSET;
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

    private int getDirection()
    {
        return MathHelper.sign(offset);
    }

    private float getPercent()
    {
        return 1F * Math.abs(offset) / MAX_OFFSET;
    }

    protected List<Content> fetchContents()
    {
        return bag.getContents();
    }

    protected List<Content> getContents()
    {
        if (this.contents == null)
            this.contents = this.fetchContents();
        return this.contents;
    }

    @Override
    protected Content getCurrentContent()
    {
        return this.getContents().get(this.currentIndex);
    }

    protected Content getPreviousContent()
    {
        int previousIndex = currentIndex - getDirection();
        previousIndex = adapt(previousIndex);
        return getContents().get(previousIndex);
    }

    @Override
    protected void open()
    {
        super.open(currentIndex);
    }
}
