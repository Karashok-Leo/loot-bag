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
import net.minecraft.util.RandomSource;
import org.apache.commons.lang3.mutable.MutableInt;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

public class RandomBag extends Bag implements ContentView
{
    public static final MapCodec<RandomBag> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    Entry.CODEC.listOf().fieldOf("pool").forGetter(RandomBag::getPool)
            ).and(bagFields(ins)).apply(ins, RandomBag::new)
    );

    public static final DeferredHolder<BagType<?>, BagType<RandomBag>> TYPE = LootBagRegistry.RANDOM_BAG;

    protected final List<Entry> pool;

    public RandomBag(List<Entry> pool, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.pool = pool;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE.get();
    }

    public List<Entry> getPool()
    {
        return pool;
    }

    @Override
    public List<ContentEntry> getContents()
    {
        return this.getPool().stream().map(Entry::content).filter(Objects::nonNull).toList();
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        RandomSource random = context.random();

        List<Entry> entryList = this.getPool();

        if (entryList.isEmpty()) return Optional.empty();


        MutableInt totalWeight = new MutableInt();
        for (Entry entry : entryList)
            totalWeight.add(entry.weight());
        int size = entryList.size();

        Content content = null;
        if (totalWeight.intValue() == 0 || size == 1)
            content = entryList.get(0).getContent();
        else
        {
            int randomInt = random.nextInt(totalWeight.intValue());
            for (Entry entry : entryList)
            {
                randomInt -= entry.weight();
                if (randomInt < 0)
                {
                    content = entry.getContent();
                    break;
                }
            }
        }
        return Optional.ofNullable(content);
    }

    public record Entry(ContentEntry content, int weight)
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        ContentEntry.CODEC.fieldOf("content").forGetter(Entry::content),
                        Codec.INT.fieldOf("weight").forGetter(Entry::weight)
                ).apply(ins, Entry::new)
        );

        public Content getContent()
        {
            return this.content.content();
        }
    }
}
