package karashokleo.loot_bag.internal.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSyntaxException;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.data.LootBagData;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.function.Consumer;

public class LootBagEntry extends LeafEntry
{
    public static final Serializer SERIALIZER = new Serializer();
    public static final LootPoolEntryType TYPE = new LootPoolEntryType(SERIALIZER);

    private final BagEntry bag;

    protected LootBagEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions, BagEntry bag)
    {
        super(weight, quality, conditions, functions);
        this.bag = bag;
    }

    @Override
    protected void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context)
    {
        lootConsumer.accept(LootBagItemRegistry.LOOT_BAG.getStack(bag));
    }

    @Override
    public LootPoolEntryType getType()
    {
        return TYPE;
    }

    public static void init()
    {
        Registry.register(Registries.LOOT_POOL_ENTRY_TYPE, LootBagMod.id("loot_bag"), TYPE);
    }

    public static LeafEntry.Builder<?> builder(BagEntry bag)
    {
        return LootBagEntry.builder((int weight, int quality, LootCondition[] conditions, LootFunction[] functions) -> new LootBagEntry(weight, quality, conditions, functions, bag));
    }

    public static class Serializer extends LeafEntry.Serializer<LootBagEntry>
    {
        private static final String KEY = "bag";

        @Override
        public void addEntryFields(JsonObject jsonObject, LootBagEntry leafEntry, JsonSerializationContext jsonSerializationContext)
        {
            super.addEntryFields(jsonObject, leafEntry, jsonSerializationContext);
            jsonObject.addProperty(KEY, leafEntry.bag.id().toString());
        }

        @Override
        protected LootBagEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions)
        {
            Identifier id = new Identifier(JsonHelper.getString(entryJson, KEY));
            BagEntry bagEntry = LootBagData.BAGS.get(id);
            if (bagEntry == null) throw new JsonSyntaxException(LootBagData.unknownBagMessage(id));
            return new LootBagEntry(
                    weight,
                    quality,
                    conditions,
                    functions,
                    bagEntry
            );
        }
    }
}
