package net.karashokleo.lootbag.network;

import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.karashokleo.lootbag.content.LootBagItem;
import net.minecraft.item.ItemStack;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.s2c.play.CustomPayloadS2CPacket;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Hand;

public class ServerNetwork
{
    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(LootBagPackets.OPEN, (server, player, handler, buf, responseSender) ->
        {
            int index = buf.readInt();
            Hand hand = buf.readEnumConstant(Hand.class);
            server.execute(() ->
            {
                ItemStack stack = player.getStackInHand(hand);
                if (stack.getItem() instanceof LootBagItem item)
                {
                    item.open(player, index);
                    if (!player.isCreative())
                        stack.decrement(1);
                }
            });
        });
    }

    public static void sendScreen(ServerPlayerEntity player, LootBagItem item, Hand hand)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeRegistryValue(Registries.ITEM, item);
        buf.writeEnumConstant(hand);
        player.networkHandler.sendPacket(new CustomPayloadS2CPacket(LootBagPackets.SCREEN, buf));
    }
}
