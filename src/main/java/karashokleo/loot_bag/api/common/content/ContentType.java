package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;

public record ContentType<T extends Content>(Codec<T> codec)
{
}
