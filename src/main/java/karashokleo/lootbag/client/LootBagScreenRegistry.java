package karashokleo.lootbag.client;

import karashokleo.lootbag.client.screen.LootBagScreen;
import karashokleo.lootbag.client.screen.OptionalLootBagScreen;
import karashokleo.lootbag.client.screen.RandomLootBagScreen;
import karashokleo.lootbag.client.screen.SingleLootBagScreen;
import karashokleo.lootbag.content.logic.bag.*;

import java.util.HashMap;
import java.util.Map;

public class LootBagScreenRegistry
{
    private static final Map<BagType<?>, LootBagScreenFactory<?>> MAP = new HashMap<>();

    public static void register()
    {
        register(SingleBag.TYPE, SingleLootBagScreen::new);
        register(OptionalBag.TYPE, OptionalLootBagScreen::new);
        register(RandomBag.TYPE, RandomLootBagScreen::new);
    }

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
