package karashokleo.lootbag.content.logic.bag;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.content.logic.OpenBagContext;
import karashokleo.lootbag.content.logic.content.Content;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.math.random.Random;
import org.apache.commons.lang3.mutable.MutableInt;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class RandomBag extends Bag
{
    public static final Codec<RandomBag> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Entry.CODEC.listOf().fieldOf("pool").forGetter(RandomBag::getPool)
            ).and(bagFields(ins)).apply(ins, RandomBag::new)
    );

    public static final BagType<RandomBag> TYPE = new BagType<>(CODEC, true);

    protected final List<Entry> pool;

    public RandomBag(List<Entry> pool, Rarity rarity, Color color)
    {
        super(rarity, color);
        this.pool = pool;
    }

    public List<Entry> getPool()
    {
        return pool;
    }

    @Override
    public BagType<?> getType()
    {
        return TYPE;
    }

    @Override
    public Optional<Content> getContent(OpenBagContext context)
    {
        Random random = context.random();

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

    public record Entry(Identifier content, int weight)
    {
        public static final Codec<Entry> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        Identifier.CODEC.fieldOf("content").forGetter(Entry::content),
                        Codec.INT.fieldOf("weight").forGetter(Entry::weight)
                ).apply(ins, Entry::new)
        );

        @Nullable
        public Content getContent()
        {
            return LootBagRegistry.CONTENT_REGISTRY.get(this.content);
        }
    }
}