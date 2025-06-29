package karashokleo.loot_bag.api.common.bag;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.util.CodecUtil;
import net.minecraft.util.Rarity;

import java.util.Optional;

public abstract class Bag
{
    public static final Codec<Bag> CODEC = LootBagRegistry.BAG_TYPE_REGISTRY.getCodec().dispatch(Bag::getType, BagType::codec);

    public static <T extends Bag> Products.P2<RecordCodecBuilder.Mu<T>, Rarity, Color> bagFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                CodecUtil.getEnumCodec(Rarity.class).optionalFieldOf("rarity", Rarity.COMMON).forGetter(Bag::getRarity),
                Color.CODEC.fieldOf("color").forGetter(Bag::getColor)
        );
    }

    protected final Rarity rarity;

    protected final Color color;

    protected Bag(Rarity rarity, Color color)
    {
        this.rarity = rarity;
        this.color = color;
    }

    public Rarity getRarity()
    {
        return rarity;
    }

    public Color getColor()
    {
        return color;
    }

    public abstract BagType<?> getType();

    public abstract Optional<Content> getContent(OpenBagContext context);

    public record Color(int bagBody, int bagString)
    {
        public static final Codec<Color> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        Codec.INT.fieldOf("bag_body").forGetter(Color::bagBody),
                        Codec.INT.fieldOf("bag_string").forGetter(Color::bagString)
                ).apply(ins, Color::new)
        );

        public int byTintIndex(int tintIndex)
        {
            return tintIndex == 0 ? bagBody : bagString;
        }
    }
}
