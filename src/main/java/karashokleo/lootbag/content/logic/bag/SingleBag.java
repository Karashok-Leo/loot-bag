package karashokleo.lootbag.content.logic.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.content.logic.OpenBagContext;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.Optional;

public class SingleBag extends Bag
{
    public static final Codec<SingleBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Identifier.CODEC.fieldOf("content").forGetter(SingleBag::getContentId)
            ).and(bagFields(ins)).apply(ins, SingleBag::new)
    );

    public static final BagType<SingleBag> TYPE = new BagType<>(CODEC, true);

    protected final Identifier content;

    public SingleBag(Identifier content, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.content = content;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public Identifier getContentId()
    {
        return this.content;
    }

    public Content getContent()
    {
        return LootBagRegistry.CONTENT_REGISTRY.get(this.content);
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        return Optional.of(this.getContent());
    }
}
