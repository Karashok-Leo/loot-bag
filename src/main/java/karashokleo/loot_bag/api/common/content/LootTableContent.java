package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.loot.BuiltInLootTables;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.neoforged.neoforge.registries.DeferredHolder;

import java.util.Collections;
import java.util.List;

public class LootTableContent extends StacksContent
{
    public static final MapCodec<LootTableContent> CODEC = RecordCodecBuilder.mapCodec(
        ins -> ins.group(
            ResourceLocation.CODEC.fieldOf("id").forGetter(LootTableContent::getId)
        ).and(contentFields(ins).t1()).apply(ins, LootTableContent::new)
    );

    public static final DeferredHolder<ContentType<?>, ContentType<LootTableContent>> TYPE = LootBagRegistry.LOOT_TABLE_CONTENT;

    protected final ResourceLocation id;

    public LootTableContent(ResourceLocation id, Icon icon)
    {
        super(icon);
        this.id = id;
    }

    public ResourceLocation getId()
    {
        return id;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE.get();
    }

    @Override
    protected List<ItemStack> getLootStacks(ServerPlayer player)
    {
        if (id.equals(BuiltInLootTables.EMPTY.location()))
        {
            return Collections.emptyList();
        }
        ServerLevel world = player.serverLevel();
        LootParams lootContextParameterSet = new LootParams.Builder(world)
            .withParameter(LootContextParams.THIS_ENTITY, player)
            .withParameter(LootContextParams.ORIGIN, player.position())
            .create(LootContextParamSets.CHEST);
        LootTable lootTable = world.getServer().reloadableRegistries().getLootTable(ResourceKey.create(Registries.LOOT_TABLE, id));
        return lootTable.getRandomItems(lootContextParameterSet);
    }
}
