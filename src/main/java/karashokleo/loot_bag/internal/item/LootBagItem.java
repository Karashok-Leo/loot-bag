package karashokleo.loot_bag.internal.item;

import com.mojang.datafixers.util.Pair;
import karashokleo.loot_bag.api.common.OpenBagContext;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.network.ServerNetworkHandlers;
import net.minecraft.client.item.TooltipContext;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.*;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Optional;

public class LootBagItem extends Item
{
    protected static final MutableText INVALID = Text.translatable("text.loot-bag.invalid").formatted(Formatting.RED);
    protected static final MutableText OPEN_PREVIEW_SCREEN = Text.translatable("tooltip.loot-bag.open_screen").formatted(Formatting.GRAY);
    protected static final MutableText QUICK_OPEN = Text.translatable("tooltip.loot-bag.quick_open").formatted(Formatting.GRAY);
    protected static final MutableText QUICK_OPEN_STACK = Text.translatable("tooltip.loot-bag.quick_open_stack").formatted(Formatting.GRAY);
    protected static final String KEY = "BagId";

    public LootBagItem(Settings settings)
    {
        super(settings);
    }

    public Optional<BagEntry> getBagEntry(ItemStack stack)
    {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return Optional.empty();
        NbtElement element = nbt.get(KEY);
        if (element == null) return Optional.empty();
        return BagEntry.CODEC
                .decode(NbtOps.INSTANCE, element)
                .result()
                .map(Pair::getFirst);
    }

    public Optional<Bag> getBag(ItemStack stack)
    {
        return this.getBagEntry(stack).map(BagEntry::bag);
    }

    public ItemStack getStack(Identifier bagId)
    {
        ItemStack stack = this.getDefaultStack();
        stack.setSubNbt(
                KEY,
                NbtString.of(bagId.toString())
        );
        return stack;
    }

    public ItemStack getStack(BagEntry entry)
    {
        ItemStack stack = this.getDefaultStack();
        stack.setSubNbt(
                KEY,
                BagEntry.CODEC
                        .encodeStart(NbtOps.INSTANCE, entry)
                        .result()
                        .orElseThrow()
        );
        return stack;
    }

    @Override
    public Text getName(ItemStack stack)
    {
        return this.getBagEntry(stack)
                .map(BagEntry::getName)
                .orElseGet(this::getName);
    }

    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return this.getBag(stack).map(Bag::getRarity).orElse(Rarity.COMMON);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player)
        {
            int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
            this.getBagEntry(stack).ifPresentOrElse(entry ->
            {
                // Open Without Screen While Sneaking
                Bag bag = entry.bag();
                if (player.isSneaking() && bag.getType().quick())
                {
                    if (hand == Hand.MAIN_HAND)
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
            }, () -> player.sendMessage(INVALID, true));
        }
        return TypedActionResult.success(stack, world.isClient());
    }

    public static void open(ServerPlayerEntity player, ItemStack stack, Bag bag, int selectedIndex)
    {
        bag.getContent(new OpenBagContext(player.getRandom(), selectedIndex)).ifPresent(content ->
        {
            content.reward(player);
            if (!player.isCreative())
                stack.decrement(1);
        });
    }

    public static void open(ServerPlayerEntity player, int slot, int selectedIndex)
    {
        ItemStack stack = player.getInventory().getStack(slot);
        if (stack.getItem() instanceof LootBagItem item)
            item.getBag(stack).ifPresentOrElse(
                    bag -> open(player, stack, bag, selectedIndex),
                    () -> player.sendMessage(INVALID, true)
            );
    }

    @Override
    public void appendTooltip(ItemStack stack, @Nullable World world, List<Text> tooltip, TooltipContext context)
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
