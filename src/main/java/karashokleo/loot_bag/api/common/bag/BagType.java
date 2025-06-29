package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;

public record BagType<T extends Bag>(Codec<T> codec, boolean quick)
{
}
