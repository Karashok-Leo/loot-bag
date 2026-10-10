package karashokleo.loot_bag.api.common.bag;

import com.mojang.serialization.Codec;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.world.item.Rarity;

import java.util.List;
import java.util.Optional;

public class OptionalBag extends Bag implements ContentView
{
    public static final MapCodec<OptionalBag> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    ContentEntry.CODEC.listOf()
                            .fieldOf("options")
                            .forGetter(OptionalBag::getOptions)
            ).and(bagFields(ins)).apply(ins, OptionalBag::new)
    );

    public static final DeferredHolder<BagType<?>, BagType<OptionalBag>> TYPE = LootBagRegistry.OPTIONAL_BAG;

    protected final List<ContentEntry> options;

    public OptionalBag(List<ContentEntry> options, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.options = options;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE.get();
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
