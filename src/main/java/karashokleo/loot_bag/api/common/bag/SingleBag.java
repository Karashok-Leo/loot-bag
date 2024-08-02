package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.util.Rarity;

import java.util.Optional;

public class SingleBag extends Bag
{
    public static final Codec<SingleBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    ContentEntry.CODEC.fieldOf("content").forGetter(SingleBag::getContent)
            ).and(bagFields(ins)).apply(ins, SingleBag::new)
    );

    public static final BagType<SingleBag> TYPE = new BagType<>(CODEC, true);

    protected final ContentEntry content;

    public SingleBag(ContentEntry content, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.content = content;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public ContentEntry getContent()
    {
        return this.content;
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        return Optional.of(this.getContent().content());
    }
}
