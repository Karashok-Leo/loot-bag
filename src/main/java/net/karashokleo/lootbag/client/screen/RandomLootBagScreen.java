package net.karashokleo.lootbag.client.screen;

import net.karashokleo.lootbag.config.initial.LootTableEntries.Entry;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class RandomLootBagScreen extends ScrollableLootBagScreen
{
    private static final String TEXT_RANDOM = "text.loot-bag.random_screen";
    private int tick;

    public RandomLootBagScreen(Entry[] lootEntries, Hand hand)
    {
        super(Text.translatable(TEXT_RANDOM), lootEntries, hand);
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
