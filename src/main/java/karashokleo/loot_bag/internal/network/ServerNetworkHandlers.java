package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.item.LootBagItem;
import karashokleo.loot_bag.internal.network.packet.OpenBagPacket;
import karashokleo.loot_bag.internal.network.packet.SetScreenPacket;
import karashokleo.loot_bag.internal.network.packet.SyncDataPackets;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.event.OnDatapackSyncEvent;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;

public class ServerNetworkHandlers
{
    public static void sendScreen(ServerPlayer player, int slot, ResourceLocation bagId)
    {
        PacketDistributor.sendToPlayer(player, new SetScreenPacket(slot, bagId));
    }

    public static void register(RegisterPayloadHandlersEvent event)
    {
        var registrar = event.registrar("1");
        registrar.playToServer(SyncDataPackets.ACK_TYPE, SyncDataPackets.AckPacket.STREAM_CODEC, ServerNetworkHandlers::handleAck);
        registrar.playToServer(OpenBagPacket.TYPE, OpenBagPacket.STREAM_CODEC, ServerNetworkHandlers::handleOpenBag);
        registrar.playToClient(SyncDataPackets.SYNC_CONTENT_TYPE, SyncDataPackets.SyncContentPacket.STREAM_CODEC, ClientNetworkHandlers::handleSyncContent);
        registrar.playToClient(SyncDataPackets.SYNC_BAG_TYPE, SyncDataPackets.SyncBagPacket.STREAM_CODEC, ClientNetworkHandlers::handleSyncBag);
        registrar.playToClient(SetScreenPacket.TYPE, SetScreenPacket.STREAM_CODEC, ClientNetworkHandlers::handleSetScreen);
    }

    public static void sendSyncData(OnDatapackSyncEvent event)
    {
        if (event.getPlayer() != null)
        {
            sendSyncContent(event.getPlayer());
        } else
        {
            event.getPlayerList().getPlayers().forEach(ServerNetworkHandlers::sendSyncContent);
        }
    }

    private static void sendSyncContent(ServerPlayer player)
    {
        Collection<ContentEntry> contentEntries = LootBagManager.getInstance().getAllContentEntries();
        PacketDistributor.sendToPlayer(player, new SyncDataPackets.SyncContentPacket(contentEntries));
    }

    private static void sendSyncBag(ServerPlayer player)
    {
        Collection<BagEntry> bagEntries = LootBagManager.getInstance().getAllBagEntries();
        PacketDistributor.sendToPlayer(player, new SyncDataPackets.SyncBagPacket(bagEntries));
    }

    private static void handleAck(SyncDataPackets.AckPacket packet, IPayloadContext context)
    {
        ServerPlayer player = (ServerPlayer) context.player();
        if (packet.count() == LootBagManager.getInstance().getAllContentEntries().size())
        {
            sendSyncBag(player);
        } else
        {
            sendSyncContent(player);
        }
    }

    private static void handleOpenBag(OpenBagPacket packet, IPayloadContext context)
    {
        LootBagItem.open((ServerPlayer) context.player(), packet.slot(), packet.selectedIndex());
    }
}
