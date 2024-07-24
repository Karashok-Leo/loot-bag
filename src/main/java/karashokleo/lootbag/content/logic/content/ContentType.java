package karashokleo.lootbag.content.logic.content;

import com.mojang.serialization.Codec;

public record ContentType<C extends Content>(Codec<C> codec)
{
}
