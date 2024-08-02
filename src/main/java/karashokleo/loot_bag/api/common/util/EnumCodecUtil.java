package karashokleo.loot_bag.api.common.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;

public class EnumCodecUtil
{
    public static <E extends Enum<E>> Codec<E> getEnumCodec(Class<E> cls)
    {
        return Codec.STRING.comapFlatMap(name -> validate(cls, name), E::name);
    }

    public static <E extends Enum<E>> DataResult<E> validate(Class<E> cls, String name)
    {
        try
        {
            return DataResult.success(E.valueOf(cls, name));
        } catch (IllegalArgumentException e)
        {
            return DataResult.error(e::getMessage);
        }
    }
}
