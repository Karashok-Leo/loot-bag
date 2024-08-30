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
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;

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

    private static void handleSyncContent(SyncDataPackets.SyncContentPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllContentEntries();
        for (ContentEntry entry : packet.entries())
            manager.putContent(entry.id(), entry.content());
        responseSender.sendPacket(
                SyncDataPackets.ACK_ID,
                PacketByteBufs
                        .create()
                        .writeVarInt(manager.getAllContentEntries().size())
        );
    }

    private static void handleSyncBag(SyncDataPackets.SyncBagPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllBagEntries();
        for (BagEntry entry : packet.entries())
            manager.putBag(entry.id(), entry.bag());
    }

    private static void handleSetScreen(SetScreenPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        BagEntry entry = LootBagManager.getInstance().getBagEntry(packet.bagId());
        if (entry == null) throw new IllegalStateException(ConstantTexts.unknownBagMessage(packet.bagId()));
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(entry.bag().getType()).createScreen(entry.bag(), packet.slot());
        MinecraftClient.getInstance().setScreen(screen);
    }
}
