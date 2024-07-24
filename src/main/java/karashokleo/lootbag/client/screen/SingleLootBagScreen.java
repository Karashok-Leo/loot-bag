package karashokleo.lootbag.client.screen;

import karashokleo.lootbag.content.logic.bag.SingleBag;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;

public class SingleLootBagScreen extends LootBagScreen<SingleBag>
{
    private static final String TEXT_SINGLE = "text.loot-bag.single_screen";

    public SingleLootBagScreen(SingleBag bag, int slot)
    {
        super(Text.translatable(TEXT_SINGLE), bag, slot);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawIcon(context, getCurrentContent().getIcon(), 0, 1, 1F);
    }

    @Override
    protected Content getCurrentContent()
    {
        return bag.getContent();
    }

    @Override
    protected void open()
    {
        super.open(0);
    }
}
