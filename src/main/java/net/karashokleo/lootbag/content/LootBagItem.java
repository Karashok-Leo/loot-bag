package net.karashokleo.lootbag.content;

import net.fabricmc.fabric.api.item.v1.FabricItemSettings;
import net.karashokleo.lootbag.content.LootBagEntry.Color;
import net.karashokleo.lootbag.network.ServerNetwork;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

public class LootBagItem extends Item
{
    public LootBagType type;
    public Color color;
    public LootEntry[] lootEntries;

    public LootBagItem(LootBagType type, Color color, LootEntry[] lootEntries, int stack, Rarity rarity)
    {
        super(new FabricItemSettings().maxCount(stack).rarity(rarity));
        this.type = type;
        this.color = color;
        this.lootEntries = lootEntries;
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
            } else ServerNetwork.sendScreen(player, this, hand);
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    public void open(ServerPlayerEntity player, int index)
    {
        for (ItemStack stack : getLootEntry(player, index).getLootStacks(player))
        {
            ServerWorld world = player.getServerWorld();
            ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack.copy());
            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }

    public LootEntry getLootEntry(ServerPlayerEntity player, int index)
    {
        return switch (type)
        {
            case SINGLE -> lootEntries[0];
            case OPTIONAL -> lootEntries[index];
            case RANDOM -> lootEntries[player.getRandom().nextInt(lootEntries.length)];
        };
    }
}
