package karashokleo.lootbag.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;

public class ClientNetwork
{
    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(SetScreenPacket.TYPE, SetScreenPacket::handle);
    }

    public static void sendOpen(int slot, int selectedIndex)
    {
        ClientPlayNetworking.send(new OpenBagPacket(slot, selectedIndex));
    }
}
