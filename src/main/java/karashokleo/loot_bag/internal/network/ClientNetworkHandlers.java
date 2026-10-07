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
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

@OnlyIn(Dist.CLIENT)
public class ClientNetworkHandlers
{
    public static void sendOpen(int slot, int selectedIndex)
    {
        PacketDistributor.sendToServer(new OpenBagPacket(slot, selectedIndex));
    }

    public static void handleSyncContent(SyncDataPackets.SyncContentPacket packet, IPayloadContext context)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllContentEntries();
        for (ContentEntry entry : packet.entries())
            manager.putContent(entry.id(), entry.content());
        context.reply(new SyncDataPackets.AckPacket(manager.getAllContentEntries().size()));
    }

    public static void handleSyncBag(SyncDataPackets.SyncBagPacket packet, IPayloadContext context)
    {
        LootBagManager manager = LootBagManager.getInstance();
        manager.clearAllBagEntries();
        for (BagEntry entry : packet.entries())
            manager.putBag(entry.id(), entry.bag());
    }

    public static void handleSetScreen(SetScreenPacket packet, IPayloadContext context)
    {
        BagEntry entry = LootBagManager.getInstance().getBagEntry(packet.bagId());
        if (entry == null) throw new IllegalStateException(ConstantTexts.unknownBagMessage(packet.bagId()));
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(entry.bag().getType()).createScreen(entry.bag(), packet.slot());
        Minecraft.getInstance().setScreen(screen);
    }
}
