package karashokleo.loot_bag.api.common.icon;

import com.mojang.serialization.MapCodec;

public record IconType<T extends Icon>(MapCodec<T> codec)
{
}
