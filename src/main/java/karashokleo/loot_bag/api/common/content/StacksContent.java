package karashokleo.loot_bag.api.common.content;

import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.level.ServerLevel;

import java.util.List;

public abstract class StacksContent extends Content
{
    protected StacksContent(Icon icon)
    {
        super(icon);
    }

    protected abstract List<ItemStack> getLootStacks(ServerPlayer player);

    @Override
    public void reward(ServerPlayer player)
    {
        for (ItemStack stack : this.getLootStacks(player))
        {
            ServerLevel world = player.serverLevel();
            ItemEntity itemEntity = new ItemEntity(world, player.getX(), player.getY(), player.getZ(), stack.copy());
            itemEntity.setNoPickUpDelay();
            world.addFreshEntity(itemEntity);
        }
    }
}
