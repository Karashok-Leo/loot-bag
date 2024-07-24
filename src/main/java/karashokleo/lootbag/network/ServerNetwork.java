package karashokleo.lootbag.network;

import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public class ServerNetwork
{
    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(OpenBagPacket.TYPE, OpenBagPacket::handle);
    }

    public static void sendScreen(ServerPlayerEntity player, int slot, Identifier bagId)
    {
        ServerPlayNetworking.send(player, new SetScreenPacket(slot, bagId));
    }
}
