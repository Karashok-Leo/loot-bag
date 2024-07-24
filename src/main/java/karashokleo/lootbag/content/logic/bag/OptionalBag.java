package karashokleo.lootbag.content.logic.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.content.logic.OpenBagContext;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Optional;

public class OptionalBag extends Bag
{
    public static final Codec<OptionalBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Identifier.CODEC.listOf()
                            .fieldOf("options")
                            .forGetter(OptionalBag::getOptions)
            ).and(bagFields(ins)).apply(ins, OptionalBag::new)
    );

    public static final BagType<OptionalBag> TYPE = new BagType<>(CODEC, false);

    protected final List<Identifier> options;

    public OptionalBag(List<Identifier> options, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.options = options;
    }

    public List<Identifier> getOptions()
    {
        return options;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public List<Content> getContents()
    {
        return this.getOptions().stream().map(LootBagRegistry.CONTENT_REGISTRY::get).toList();
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        List<Content> contents = getContents();
        int selectedIndex = context.selectedIndex();
        return selectedIndex >= contents.size() ?
                Optional.empty() : Optional.of(contents.get(selectedIndex));
    }
}
