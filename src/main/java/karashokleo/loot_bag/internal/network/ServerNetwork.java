package karashokleo.loot_bag.internal.network;

import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerNetwork
{
    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(OpenBagPacket.TYPE, OpenBagPacket::handle);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> syncData(player));
    }

    public static void syncData(ServerPlayerEntity player)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        SyncDataPacket.write(buf);
        ServerPlayNetworking.send(player, SyncDataPacket.ID, buf);
    }

    public static void sendScreen(ServerPlayerEntity player, int slot, Identifier bagId)
    {
        ServerPlayNetworking.send(player, new SetScreenPacket(slot, bagId));
    }
}
