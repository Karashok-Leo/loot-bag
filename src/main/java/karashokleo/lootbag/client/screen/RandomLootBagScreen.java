package karashokleo.lootbag.client.screen;

import karashokleo.lootbag.content.logic.bag.RandomBag;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.text.Text;

import java.util.List;
import java.util.Objects;

public class RandomLootBagScreen extends ScrollableLootBagScreen<RandomBag>
{
    protected static final String TEXT_RANDOM = "text.loot-bag.random_screen";
    protected int tick;

    public RandomLootBagScreen(RandomBag bag, int slot)
    {
        super(Text.translatable(TEXT_RANDOM), bag, slot);
    }

    @Override
    public void tick()
    {
        super.tick();
        if (++tick == 40)
        {
            tick = 0;
            next();
        }
    }

    @Override
    protected List<Content> fetchContents()
    {
        return bag.getPool().stream().map(RandomBag.Entry::getContent).filter(Objects::nonNull).toList();
    }
}
