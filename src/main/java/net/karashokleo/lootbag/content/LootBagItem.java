package net.karashokleo.lootbag.content;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.karashokleo.lootbag.config.initial.LootTableEntries.Entry;
import net.karashokleo.lootbag.network.ServerNetwork;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Identifier;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Collections;
import java.util.List;

public class LootBagItem extends Item
{
    public String name;
    public LootBagType type;
    public Entry[] lootEntries;

    public LootBagItem(String name, LootBagType type, Entry[] lootEntries, int stack, Rarity rarity)
    {
        super(new FabricItemSettings().maxCount(stack).rarity(rarity));
        this.name = name;
        this.type = type;
        this.lootEntries = lootEntries;
    }

    @Override
    public Text getName(ItemStack stack)
    {
        return this.getName();
    }

    @Override
    public Text getName()
    {
        return Text.translatable(name);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player)
        {
            if (player.isSneaking() && type != LootBagType.OPTIONAL)
            {
                open(player, 0);
                if (!player.isCreative())
                    stack.decrement(1);
            }
            else ServerNetwork.sendScreen(player, this, hand);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    public void open(ServerPlayerEntity player, int index)
    {
        for (ItemStack stack : getLootTableStacks(player, index))
        {
            ServerWorld world = player.getServerWorld();
            ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack.copy());
            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    public Identifier getLootTableId(ServerPlayerEntity player, int index)
    {
        return switch (type)
        {
            case SINGLE -> new Identifier(lootEntries[0].loot_table_id);
            case OPTIONAL -> new Identifier(lootEntries[index].loot_table_id);
            case RANDOM -> new Identifier(lootEntries[player.getRandom().nextInt(lootEntries.length)].loot_table_id);
        };
    }

    public List<ItemStack> getLootTableStacks(ServerPlayerEntity player, int index)
    {
        Identifier identifier = this.getLootTableId(player, index);
        if (identifier == LootTables.EMPTY)
            return Collections.emptyList();
        ServerWorld world = player.getServerWorld();
        LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
                .add(LootContextParameters.THIS_ENTITY, player)
                .add(LootContextParameters.ORIGIN, player.getPos())
                .build(LootContextTypes.CHEST);
        LootTable lootTable = world.getServer().getLootManager().getLootTable(identifier);
        return lootTable.generateLoot(lootContextParameterSet);
    }
}
