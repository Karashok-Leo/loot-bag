package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.client.LootBagScreenRegistry;
import karashokleo.loot_bag.api.client.screen.LootBagScreen;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import karashokleo.loot_bag.internal.network.packet.OpenBagPacket;
import karashokleo.loot_bag.internal.network.packet.SetScreenPacket;
import karashokleo.loot_bag.internal.network.packet.SyncDataPackets;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.client.MinecraftClient;

@Environment(EnvType.CLIENT)
public class ClientNetworkHandlers
{
    public static void sendOpen(int slot, int selectedIndex)
    {
        ClientPlayNetworking.send(new OpenBagPacket(slot, selectedIndex));
    }

    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(SyncDataPackets.SYNC_CONTENT_TYPE, ClientNetworkHandlers::handleSyncContent);
        ClientPlayNetworking.registerGlobalReceiver(SyncDataPackets.SYNC_BAG_TYPE, ClientNetworkHandlers::handleSyncBag);
        ClientPlayNetworking.registerGlobalReceiver(SetScreenPacket.TYPE, ClientNetworkHandlers::handleSetScreen);
    }

    private static void handleSyncContent(SyncDataPackets.SyncContentPacket packet, ClientPlayNetworking.Context context)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllContentEntries();
        for (ContentEntry entry : packet.entries())
            manager.putContent(entry.id(), entry.content());
        ClientPlayNetworking.send(new SyncDataPackets.AckPacket(manager.getAllContentEntries().size()));
    }

    private static void handleSyncBag(SyncDataPackets.SyncBagPacket packet, ClientPlayNetworking.Context context)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllBagEntries();
        for (BagEntry entry : packet.entries())
            manager.putBag(entry.id(), entry.bag());
    }

    private static void handleSetScreen(SetScreenPacket packet, ClientPlayNetworking.Context context)
    {
        BagEntry entry = LootBagManager.getInstance().getBagEntry(packet.bagId());
        if (entry == null) throw new IllegalStateException(ConstantTexts.unknownBagMessage(packet.bagId()));
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(entry.bag().getType()).createScreen(entry.bag(), packet.slot());
        MinecraftClient.getInstance().setScreen(screen);
    }
}
