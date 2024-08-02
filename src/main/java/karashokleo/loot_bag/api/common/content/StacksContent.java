package karashokleo.loot_bag.api.common.content;

import net.minecraft.entity.ItemEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;

import java.util.List;

public abstract class StacksContent extends Content
{
    protected StacksContent(Icon icon)
    {
        super(icon);
    }

    protected abstract List<ItemStack> getLootStacks(ServerPlayerEntity player);

    @Override
    public void reward(ServerPlayerEntity player)
    {
        for (ItemStack stack : this.getLootStacks(player))
        {
            ServerWorld world = player.getServerWorld();
            ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack.copy());
            itemEntity.resetPickupDelay();
            world.spawnEntity(itemEntity);
        }
    }
}
