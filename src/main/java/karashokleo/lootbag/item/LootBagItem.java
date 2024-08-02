package karashokleo.lootbag.item;

import com.mojang.datafixers.util.Pair;
import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.OpenBagContext;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.network.ServerNetwork;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtOps;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;
import net.minecraft.util.Rarity;
import net.minecraft.util.TypedActionResult;
import net.minecraft.world.World;

import java.util.Optional;

public class LootBagItem extends Item
{
    private static final Text INVALID = Text.translatable("text.loot-bag.invalid");
    private static final String KEY = "BagId";

    public LootBagItem(Settings settings)
    {
        super(settings);
    }

    public Optional<Bag> getBagEntry(ItemStack stack)
    {
        NbtCompound nbt = stack.getNbt();
        if (nbt == null) return Optional.empty();
        NbtElement element = nbt.get(KEY);
        if (element == null) return Optional.empty();
        return LootBagRegistry.BAG_REGISTRY.getCodec()
                .decode(NbtOps.INSTANCE, element)
                .result()
                .map(Pair::getFirst);
    }

    public ItemStack getStack(Bag bag)
    {
        ItemStack stack = this.getDefaultStack();
        stack.setSubNbt(
                KEY,
                LootBagRegistry.BAG_REGISTRY.getCodec()
                        .encodeStart(NbtOps.INSTANCE, bag)
                        .result()
                        .orElseThrow()
        );
        return stack;
    }

    @Override
    public Text getName(ItemStack stack)
    {
        return this.getBagEntry(stack).map(Bag::getName).orElseGet(this::getName);
    }

    @Override
    public Rarity getRarity(ItemStack stack)
    {
        return this.getBagEntry(stack).map(Bag::getRarity).orElse(Rarity.COMMON);
    }

    @Override
    public TypedActionResult<ItemStack> use(World world, PlayerEntity user, Hand hand)
    {
        ItemStack stack = user.getStackInHand(hand);
        if (user instanceof ServerPlayerEntity player)
        {
            int slot = hand == Hand.MAIN_HAND ? player.getInventory().selectedSlot : 40;
            this.getBagEntry(stack).ifPresentOrElse(bag ->
            {
                // Open Without Screen While Sneaking
                if (player.isSneaking() && bag.getType().quick())
                    open(player, stack, bag, 0);
                    // Open Through Screen
                else ServerNetwork.sendScreen(player, slot, bag);
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
            item.getBagEntry(stack).ifPresentOrElse(
                    bag -> open(player, stack, bag, selectedIndex),
                    () -> player.sendMessage(INVALID, true)
            );
    }
}
