package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.MapCodec;

public record BagType<T extends Bag>(MapCodec<T> codec, boolean quick)
{
}
