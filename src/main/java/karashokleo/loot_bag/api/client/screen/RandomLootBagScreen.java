package karashokleo.loot_bag.api.client.screen;

import karashokleo.loot_bag.api.common.bag.RandomBag;
import net.minecraft.network.chat.Component;

public class RandomLootBagScreen extends ScrollableLootBagScreen<RandomBag>
{
    protected static final Component TEXT_RANDOM = Component.translatable("text.loot-bag.random_screen");
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
