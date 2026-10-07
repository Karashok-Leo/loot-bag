package karashokleo.loot_bag.internal.item;

import com.mojang.datafixers.util.Pair;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.network.ServerNetworkHandlers;
import net.minecraft.world.item.Item.TooltipContext;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.StringTag;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.ChatFormatting;
import net.minecraft.world.item.Rarity;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.component.CustomData;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LootBagItem extends Item
{
    protected static final MutableComponent INVALID = Component.translatable("text.loot-bag.invalid").withStyle(ChatFormatting.RED);
    protected static final MutableComponent OPEN_PREVIEW_SCREEN = Component.translatable("tooltip.loot-bag.open_screen").withStyle(ChatFormatting.GRAY);
    protected static final MutableComponent QUICK_OPEN = Component.translatable("tooltip.loot-bag.quick_open").withStyle(ChatFormatting.GRAY);
    protected static final MutableComponent QUICK_OPEN_STACK = Component.translatable("tooltip.loot-bag.quick_open_stack").withStyle(ChatFormatting.GRAY);
    protected static final String KEY = "BagId";

    public LootBagItem(Properties settings)
    {
        super(settings);
    }

    public Optional<BagEntry> getBagEntry(ItemStack stack)
    {
        CompoundTag nbt = stack.getOrDefault(DataComponents.CUSTOM_DATA, CustomData.EMPTY).copyTag();
        if (nbt == null) return Optional.empty();
        Tag element = nbt.get(KEY);
        if (element == null) return Optional.empty();
        return BagEntry.CODEC
                .decode(NbtOps.INSTANCE, element)
                .result()
                .map(Pair::getFirst);
    }

    private static void setBagData(ItemStack stack, String key, Tag value)
    {
        CustomData.update(DataComponents.CUSTOM_DATA, stack, tag -> tag.put(key, value));
    }

    public Optional<Bag> getBag(ItemStack stack)
    {
        return this.getBagEntry(stack).map(BagEntry::bag);
    }

    public ItemStack getStack(ResourceLocation bagId)
    {
        ItemStack stack = this.getDefaultInstance();
        setBagData(stack,
                KEY,
                StringTag.valueOf(bagId.toString())
        );
        return stack;
    }

    public ItemStack getStack(BagEntry entry)
    {
        ItemStack stack = this.getDefaultInstance();
        setBagData(stack,
                KEY,
                BagEntry.CODEC
                        .encodeStart(NbtOps.INSTANCE, entry)
                        .result()
                        .orElseThrow()
        );
        return stack;
    }

    @Override
    public Component getName(ItemStack stack)
    {
        return this.getBagEntry(stack)
                .map(BagEntry::getName)
                .orElseGet(() -> super.getName(stack));
    }

    public Rarity getRarity(ItemStack stack)
    {
        return this.getBag(stack).map(Bag::getRarity).orElse(Rarity.COMMON);
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level world, Player user, InteractionHand hand)
    {
        ItemStack stack = user.getItemInHand(hand);
        if (user instanceof ServerPlayer player)
        {
            int slot = hand == InteractionHand.MAIN_HAND ? player.getInventory().selected : 40;
            this.getBagEntry(stack).ifPresentOrElse(entry ->
            {
                // Open Without Screen While Sneaking
                Bag bag = entry.bag();
                if (player.isShiftKeyDown() && bag.getType().quick())
                {
                    if (hand == InteractionHand.MAIN_HAND)
                    {
                        open(player, stack, bag, 0);
                    } else
                    {
                        int count = stack.getCount();
                        for (int i = 0; i < count; i++)
                        {
                            open(player, stack, bag, 0);
                        }
                    }
                }
                // Open Through Screen
                else ServerNetworkHandlers.sendScreen(player, slot, entry.id());
            }, () -> player.displayClientMessage(INVALID, true));
        }
        return InteractionResultHolder.sidedSuccess(stack, world.isClientSide());
    }

    public static void open(ServerPlayer player, ItemStack stack, Bag bag, int selectedIndex)
    {
        bag.getContent(new OpenBagContext(player.getRandom(), selectedIndex)).ifPresent(content ->
        {
            content.reward(player);
            if (!player.isCreative())
                stack.shrink(1);
        });
    }

    public static void open(ServerPlayer player, int slot, int selectedIndex)
    {
        ItemStack stack = player.getInventory().getItem(slot);
        if (stack.getItem() instanceof LootBagItem item)
            item.getBag(stack).ifPresentOrElse(
                    bag -> open(player, stack, bag, selectedIndex),
                    () -> player.displayClientMessage(INVALID, true)
            );
    }

    @Override
    public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltip, TooltipFlag flag)
    {
        Optional<Bag> optional = this.getBag(stack);
        if (optional.isEmpty())
        {
            tooltip.add(INVALID);
            return;
        }
        tooltip.add(OPEN_PREVIEW_SCREEN);
        if (!optional.get().getType().quick()) return;
        tooltip.add(QUICK_OPEN);
        tooltip.add(QUICK_OPEN_STACK);
    }
}
