package karashokleo.lootbag.api.common.bag;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.api.common.util.EnumCodecUtil;
import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.OpenBagContext;
import karashokleo.lootbag.api.common.content.Content;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.text.Text;
import net.minecraft.util.Rarity;
import net.minecraft.util.Util;

import java.util.Optional;

public abstract class Bag
{
    public static final Codec<Bag> CODEC = LootBagRegistry.BAG_TYPE_REGISTRY.getCodec().dispatch(Bag::getType, BagType::codec);
    public static final Codec<RegistryEntry<Bag>> ENTRY_CODEC = RegistryElementCodec.of(LootBagRegistry.BAG_KEY, CODEC);

    public static <T extends Bag> Products.P2<RecordCodecBuilder.Mu<T>, Rarity, Color> bagFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                EnumCodecUtil.getEnumCodec(Rarity.class).optionalFieldOf("rarity", Rarity.COMMON).forGetter(Bag::getRarity),
                Color.CODEC.fieldOf("color").forGetter(Bag::getColor)
        );
    }

    protected final Rarity rarity;

    protected final Color color;

    private String translationKey;

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

    public Text getName()
    {
        return Text.translatable(this.getNameKey());
    }

    public String getNameKey()
    {
        if (this.translationKey == null)
            this.translationKey = Util.createTranslationKey("bag", LootBagRegistry.getBagId(this));
        return this.translationKey;
    }

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
