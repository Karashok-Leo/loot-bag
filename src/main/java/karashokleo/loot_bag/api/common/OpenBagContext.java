package karashokleo.loot_bag.api.common;

import net.minecraft.util.RandomSource;

public record OpenBagContext(RandomSource random, int selectedIndex)
{
    public OpenBagContext
    {
        if (selectedIndex < 0)
            throw new IllegalArgumentException("selectedIndex must be positive");
    }
}
