package karashokleo.lootbag.api.client.screen;

import karashokleo.lootbag.api.common.bag.RandomBag;
import net.minecraft.text.Text;

public class RandomLootBagScreen extends ScrollableLootBagScreen<RandomBag>
{
    protected static final Text TEXT_RANDOM = Text.translatable("text.loot-bag.random_screen");
    protected int tick;

    public RandomLootBagScreen(RandomBag bag, int slot)
    {
        super(TEXT_RANDOM, bag, slot);
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
}
