package karashokleo.lootbag.content.logic.bag;

import com.mojang.serialization.Codec;
import karashokleo.lootbag.client.screen.LootBagScreen;

public record BagType<B extends Bag>(Codec<B> codec, boolean quick)
{
}
