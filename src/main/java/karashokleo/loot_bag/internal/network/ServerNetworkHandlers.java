package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import karashokleo.loot_bag.internal.item.LootBagItem;
import karashokleo.loot_bag.internal.network.packet.OpenBagPacket;
import karashokleo.loot_bag.internal.network.packet.SetScreenPacket;
import karashokleo.loot_bag.internal.network.packet.SyncDataPackets;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.Collection;

public class ServerNetworkHandlers
{
    private static final String PROTOCOL = "1";
    public static final SimpleChannel CHANNEL = NetworkRegistry.newSimpleChannel(
            LootBagMod.id("main"), () -> PROTOCOL, PROTOCOL::equals, PROTOCOL::equals);

    public static void sendScreen(ServerPlayerEntity player, int slot, Identifier bagId)
    {
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SetScreenPacket(slot, bagId));
    }

    public static void init()
    {
        CHANNEL.messageBuilder(OpenBagPacket.class, 0, NetworkDirection.PLAY_TO_SERVER)
                .encoder(OpenBagPacket::write).decoder(OpenBagPacket::read)
                .consumerMainThread((packet, context) -> {
                    ServerPlayerEntity player = context.get().getSender();
                    if (player != null) LootBagItem.open(player, packet.slot(), packet.selectedIndex());
                }).add();
        CHANNEL.messageBuilder(SyncDataPackets.AckPacket.class, 1, NetworkDirection.PLAY_TO_SERVER)
                .encoder(SyncDataPackets.AckPacket::write).decoder(SyncDataPackets.AckPacket::read)
                .consumerMainThread((packet, context) -> {
                    ServerPlayerEntity player = context.get().getSender();
                    if (player == null) return;
                    if (packet.count() == LootBagManager.getInstance().getAllContentEntries().size()) sendSyncBag(player);
                    else sendSyncContent(player);
                }).add();
        CHANNEL.messageBuilder(SyncDataPackets.SyncContentPacket.class, 2, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncDataPackets.SyncContentPacket::write).decoder(SyncDataPackets::readContent)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientNetworkHandlers.handleSyncContent(packet))).add();
        CHANNEL.messageBuilder(SyncDataPackets.SyncBagPacket.class, 3, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SyncDataPackets.SyncBagPacket::write).decoder(SyncDataPackets::readBag)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientNetworkHandlers.handleSyncBag(packet))).add();
        CHANNEL.messageBuilder(SetScreenPacket.class, 4, NetworkDirection.PLAY_TO_CLIENT)
                .encoder(SetScreenPacket::write).decoder(SetScreenPacket::read)
                .consumerMainThread((packet, context) -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT,
                        () -> () -> ClientNetworkHandlers.handleSetScreen(packet))).add();
        MinecraftForge.EVENT_BUS.addListener(ServerNetworkHandlers::sendSyncData);
    }

    private static void sendSyncData(OnDatapackSyncEvent event)
    {
        if (event.getPlayer() != null) sendSyncContent(event.getPlayer());
        else event.getPlayerList().getPlayerList().forEach(ServerNetworkHandlers::sendSyncContent);
    }

    private static void sendSyncContent(ServerPlayerEntity player)
    {
        Collection<ContentEntry> contentEntries = LootBagManager.getInstance().getAllContentEntries();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncDataPackets.SyncContentPacket(contentEntries));
    }

    private static void sendSyncBag(ServerPlayerEntity player)
    {
        Collection<BagEntry> bagEntries = LootBagManager.getInstance().getAllBagEntries();
        CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), new SyncDataPackets.SyncBagPacket(bagEntries));
    }
}
