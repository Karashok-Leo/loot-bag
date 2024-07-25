package karashokleo.lootbag.api.common.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.api.common.OpenBagContext;
import karashokleo.lootbag.api.common.content.Content;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Rarity;

import java.util.Optional;

public class SingleBag extends Bag
{
    public static final Codec<SingleBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Content.ENTRY_CODEC.fieldOf("content").forGetter(SingleBag::getContentEntry)
            ).and(bagFields(ins)).apply(ins, SingleBag::new)
    );

    public static final BagType<SingleBag> TYPE = new BagType<>(CODEC, true);

    protected final RegistryEntry<Content> content;

    public SingleBag(RegistryEntry<Content> content, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.content = content;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public RegistryEntry<Content> getContentEntry()
    {
        return this.content;
    }

    public Content getContent()
    {
        return this.content.value();
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        return Optional.of(this.getContent());
    }
}
