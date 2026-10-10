package karashokleo.loot_bag.api.common.loot;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.condition.LootCondition;
import net.minecraft.loot.context.LootContext;
import net.minecraft.loot.entry.LeafEntry;
import net.minecraft.loot.entry.LootPoolEntryType;
import net.minecraft.loot.function.LootFunction;
import net.minecraft.registry.RegistryKeys;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.util.Identifier;
import net.minecraft.util.JsonHelper;

import java.util.function.Consumer;

public class LootBagEntry extends LeafEntry
{
    public static final Serializer SERIALIZER = new Serializer();
    private static final DeferredRegister<LootPoolEntryType> TYPES = DeferredRegister.create(RegistryKeys.LOOT_POOL_ENTRY_TYPE, LootBagMod.MOD_ID);
    public static final RegistryObject<LootPoolEntryType> TYPE = TYPES.register("loot_bag", () -> new LootPoolEntryType(SERIALIZER));

    private final Identifier bagId;

    protected LootBagEntry(int weight, int quality, LootCondition[] conditions, LootFunction[] functions, Identifier bagId)
    {
        super(weight, quality, conditions, functions);
        this.bagId = bagId;
    }

    @Override
    protected void generateLoot(Consumer<ItemStack> lootConsumer, LootContext context)
    {
        lootConsumer.accept(LootBagItemRegistry.LOOT_BAG.get().getStack(bagId));
    }

    @Override
    public LootPoolEntryType getType()
    {
        return TYPE.get();
    }

    public static void init(IEventBus bus)
    {
        TYPES.register(bus);
    }

    public static LeafEntry.Builder<?> builder(BagEntry bag)
    {
        return builder(bag.id());
    }

    public static LeafEntry.Builder<?> builder(Identifier bagId)
    {
        return LootBagEntry.builder((int weight, int quality, LootCondition[] conditions, LootFunction[] functions) -> new LootBagEntry(weight, quality, conditions, functions, bagId));
    }

    public static class Serializer extends LeafEntry.Serializer<LootBagEntry>
    {
        private static final String KEY = "bag";

        @Override
        public void addEntryFields(JsonObject jsonObject, LootBagEntry leafEntry, JsonSerializationContext jsonSerializationContext)
        {
            super.addEntryFields(jsonObject, leafEntry, jsonSerializationContext);
            jsonObject.addProperty(KEY, leafEntry.bagId.toString());
        }

        @Override
        protected LootBagEntry fromJson(JsonObject entryJson, JsonDeserializationContext context, int weight, int quality, LootCondition[] conditions, LootFunction[] functions)
        {
            return new LootBagEntry(
                    weight,
                    quality,
                    conditions,
                    functions,
                    new Identifier(JsonHelper.getString(entryJson, KEY))
            );
        }
    }
}
