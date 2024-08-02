package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Optional;

public class OptionalBag extends Bag implements ContentView
{
    public static final Codec<OptionalBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    ContentEntry.CODEC.listOf()
                            .fieldOf("options")
                            .forGetter(OptionalBag::getOptions)
            ).and(bagFields(ins)).apply(ins, OptionalBag::new)
    );

    public static final BagType<OptionalBag> TYPE = new BagType<>(CODEC, false);

    protected final List<ContentEntry> options;

    public OptionalBag(List<ContentEntry> options, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.options = options;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public List<ContentEntry> getOptions()
    {
        return options;
    }

    @Override
    public List<ContentEntry> getContents()
    {
        return this.getOptions();
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        List<ContentEntry> contents = this.getOptions();
        int selectedIndex = context.selectedIndex();
        return selectedIndex >= contents.size() ?
                Optional.empty() : Optional.of(contents.get(selectedIndex).content());
    }
}
