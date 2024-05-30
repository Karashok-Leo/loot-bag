package net.karashokleo.lootbag.client.screen;

import net.karashokleo.lootbag.content.LootEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.math.MathHelper;

public class ScrollableLootBagScreen extends BaseLootBagScreen
{
    protected static final int MAX_OFFSET = 10;
    protected static final float OFFSET_MUL = 6;
    protected int offset = 0;

    public ScrollableLootBagScreen(Text title, LootEntry[] lootEntries, Hand hand)
    {
        super(title, lootEntries, hand);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawIcon(context, getCurrentEntry().icon, offset * OFFSET_MUL, 1F - getPercent(), 1F - getPercent());
        drawIcon(context, getOldEntry().icon, (offset - MAX_OFFSET * getDirection()) * OFFSET_MUL, getPercent(), getPercent());
    }

    @Override
    public void tick()
    {
        if (offset > 0) offset--;
        else if (offset < 0) offset++;
    }

    @Override
    protected void prev()
    {
        super.prev();
        offset = -MAX_OFFSET;
    }

    @Override
    protected void next()
    {
        super.next();
        offset = MAX_OFFSET;
    }

    private int getDirection()
    {
        return MathHelper.sign(offset);
    }

    private float getPercent()
    {
        return 1F * Math.abs(offset) / MAX_OFFSET;
    }

    private LootEntry getOldEntry()
    {
        int oldIndex = currentEntryIndex - getDirection();
        oldIndex = adapt(oldIndex);
        return lootEntries[oldIndex];
    }
}
