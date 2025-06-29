package karashokleo.loot_bag.api.common.icon;

import com.mojang.serialization.Codec;

public record IconType<T extends Icon>(Codec<T> codec)
{
}
