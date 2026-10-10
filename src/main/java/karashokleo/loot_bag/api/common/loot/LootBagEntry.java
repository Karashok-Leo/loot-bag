package karashokleo.loot_bag.api.common.loot;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItemRegistry;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.predicates.LootItemCondition;
import net.minecraft.world.level.storage.loot.LootContext;
import net.minecraft.world.level.storage.loot.entries.LootPoolSingletonContainer;
import net.minecraft.world.level.storage.loot.entries.LootPoolEntryType;
import net.minecraft.world.level.storage.loot.functions.LootItemFunction;
import net.minecraft.core.registries.Registries;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.minecraft.resources.ResourceLocation;

import java.util.List;
import java.util.function.Consumer;

public class LootBagEntry extends LootPoolSingletonContainer
{
    public static final MapCodec<LootBagEntry> CODEC = RecordCodecBuilder.mapCodec(ins -> singletonFields(ins)
            .and(ResourceLocation.CODEC.fieldOf("bag").forGetter(entry -> entry.bagId))
            .apply(ins, LootBagEntry::new));
    private static final DeferredRegister<LootPoolEntryType> TYPES = DeferredRegister.create(Registries.LOOT_POOL_ENTRY_TYPE, LootBagMod.MOD_ID);
    public static final DeferredHolder<LootPoolEntryType, LootPoolEntryType> TYPE = TYPES.register("loot_bag", () -> new LootPoolEntryType(CODEC));

    private final ResourceLocation bagId;

    protected LootBagEntry(int weight, int quality, List<LootItemCondition> conditions, List<LootItemFunction> functions, ResourceLocation bagId)
    {
        super(weight, quality, conditions, functions);
        this.bagId = bagId;
    }

    @Override
    protected void createItemStack(Consumer<ItemStack> lootConsumer, LootContext context)
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

    public static LootPoolSingletonContainer.Builder<?> builder(BagEntry bag)
    {
        return builder(bag.id());
    }

    public static LootPoolSingletonContainer.Builder<?> builder(ResourceLocation bagId)
    {
        return simpleBuilder((weight, quality, conditions, functions) -> new LootBagEntry(weight, quality, conditions, functions, bagId));
    }
}
