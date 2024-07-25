package karashokleo.lootbag.api.client;

import karashokleo.lootbag.api.client.screen.LootBagScreen;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.bag.BagType;

import java.util.HashMap;
import java.util.Map;

public class LootBagScreenRegistry
{
    private static final Map<BagType<?>, LootBagScreenFactory<?>> MAP = new HashMap<>();

    public static <B extends Bag> void register(BagType<B> type, LootBagScreenFactory<B> factory)
    {
        MAP.put(type, factory);
    }

    @SuppressWarnings("unchecked")
    public static <B extends Bag> LootBagScreenFactory<B> getFactory(BagType<B> type)
    {
        return (LootBagScreenFactory<B>) MAP.get(type);
    }

    public interface LootBagScreenFactory<B extends Bag>
    {
        LootBagScreen<B> create(B bag, int slot);

        @SuppressWarnings("unchecked")
        default LootBagScreen<B> createScreen(Bag bag, int slot)
        {
            return create((B) bag, slot);
        }
    }
}
