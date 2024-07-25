package karashokleo.lootbag.api.common.bag;

import com.mojang.serialization.Codec;

public record BagType<B extends Bag>(Codec<B> codec, boolean quick)
{
}
