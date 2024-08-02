package karashokleo.lootbag.network;

import karashokleo.lootbag.api.common.bag.Bag;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;

public class ServerNetwork
{
    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(OpenBagPacket.TYPE, OpenBagPacket::handle);
    }

    public static void sendScreen(ServerPlayerEntity player, int slot, Bag bag)
    {
        ServerPlayNetworking.send(player, new SetScreenPacket(slot, bag));
    }
}
