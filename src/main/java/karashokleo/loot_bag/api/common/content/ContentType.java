package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.MapCodec;

public record ContentType<T extends Content>(MapCodec<T> codec)
{
}
