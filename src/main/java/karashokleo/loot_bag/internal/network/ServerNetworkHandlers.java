package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.item.LootBagItem;
import karashokleo.loot_bag.internal.network.packet.OpenBagPacket;
import karashokleo.loot_bag.internal.network.packet.SetScreenPacket;
import karashokleo.loot_bag.internal.network.packet.SyncDataPackets;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.Collection;

public class ServerNetworkHandlers
{
    public static void sendScreen(ServerPlayerEntity player, int slot, Identifier bagId)
    {
        ServerPlayNetworking.send(player, new SetScreenPacket(slot, bagId));
    }

    public static void init()
    {
        ServerPlayNetworking.registerGlobalReceiver(SyncDataPackets.ACK_ID, ServerNetworkHandlers::handleAck);
        ServerPlayNetworking.registerGlobalReceiver(OpenBagPacket.TYPE, ServerNetworkHandlers::handleOpenBag);
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register(ServerNetworkHandlers::sendSyncData);
    }

    private static void sendSyncData(ServerPlayerEntity player, boolean joined)
    {
        sendSyncContent(player);
    }

    private static void sendSyncContent(ServerPlayerEntity player)
    {
        Collection<ContentEntry> contentEntries = LootBagManager.getInstance().getAllContentEntries();
        ServerPlayNetworking.send(player, new SyncDataPackets.SyncContentPacket(contentEntries));
    }

    private static void sendSyncBag(ServerPlayerEntity player)
    {
        Collection<BagEntry> bagEntries = LootBagManager.getInstance().getAllBagEntries();
        ServerPlayNetworking.send(player, new SyncDataPackets.SyncBagPacket(bagEntries));
    }

    private static void handleAck(MinecraftServer server, ServerPlayerEntity player, ServerPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        if (buf.readVarInt() == LootBagManager.getInstance().getAllContentEntries().size())
            sendSyncBag(player);
        else sendSyncContent(player);
    }

    private static void handleOpenBag(OpenBagPacket packet, ServerPlayerEntity player, PacketSender responseSender)
    {
        LootBagItem.open(player, packet.slot(), packet.selectedIndex());
    }
}
