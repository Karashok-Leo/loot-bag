package karashokleo.lootbag.api.common;

import net.minecraft.util.math.random.Random;

public record OpenBagContext(Random random, int selectedIndex)
{
    public OpenBagContext
    {
        if (selectedIndex < 0)
            throw new RuntimeException();
    }
}
