package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.common.bag.SingleBag;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.text.Text;

public class SingleLootBagScreen extends LootBagScreen<SingleBag>
{
    private static final Text TEXT_SINGLE = Text.translatable("text.loot-bag.single_screen");

    public SingleLootBagScreen(SingleBag bag, int slot)
    {
        super(TEXT_SINGLE, bag, slot);
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
