package karashokleo.lootbag.content.logic.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.content.logic.OpenBagContext;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.Rarity;

import java.util.List;
import java.util.Optional;

public class OptionalBag extends Bag implements ContentView
{
    public static final Codec<OptionalBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Content.ENTRY_CODEC.listOf()
                            .fieldOf("options")
                            .forGetter(OptionalBag::getOptions)
            ).and(bagFields(ins)).apply(ins, OptionalBag::new)
    );

    public static final BagType<OptionalBag> TYPE = new BagType<>(CODEC, false);

    protected final List<RegistryEntry<Content>> options;

    public OptionalBag(List<RegistryEntry<Content>> options, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.options = options;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    public List<RegistryEntry<Content>> getOptions()
    {
        return options;
    }

    @Override
    public List<Content> getContents()
    {
        return this.getOptions().stream().map(RegistryEntry::value).toList();
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        List<RegistryEntry<Content>> contents = this.getOptions();
        int selectedIndex = context.selectedIndex();
        return selectedIndex >= contents.size() ?
                Optional.empty() : Optional.of(contents.get(selectedIndex).value());
    }
}
