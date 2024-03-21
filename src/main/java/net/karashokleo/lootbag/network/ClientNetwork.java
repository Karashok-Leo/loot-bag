package net.karashokleo.lootbag.network;

import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.karashokleo.lootbag.client.OptionalLootBagScreen;
import net.karashokleo.lootbag.client.RandomLootBagScreen;
import net.karashokleo.lootbag.client.SingleLootBagScreen;
import net.karashokleo.lootbag.content.LootBagItem;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.network.packet.c2s.play.CustomPayloadC2SPacket;
import net.minecraft.registry.Registries;
import net.minecraft.util.Hand;

public class ClientNetwork
{
    public static void init()
    {
        ClientPlayNetworking.registerGlobalReceiver(LootBagPackets.SCREEN, (client, handler, buf, responseSender) ->
        {
            LootBagItem item = (LootBagItem) buf.readRegistryValue(Registries.ITEM);
            Hand hand = buf.readEnumConstant(Hand.class);
            if (item == null) return;
            Screen screen = switch (item.type)
            {
                case SINGLE -> new SingleLootBagScreen(item.lootEntries, hand);
                case OPTIONAL -> new OptionalLootBagScreen(item.lootEntries, hand);
                case RANDOM -> new RandomLootBagScreen(item.lootEntries, hand);
            };
            client.execute(() ->
            {
                if (client.world == null || client.player == null) return;
                client.setScreen(screen);
            });
        });
    }

    public static void sendOpen(ClientPlayerEntity player, int lootIndex, Hand hand)
    {
        PacketByteBuf buf = PacketByteBufs.create();
        buf.writeInt(lootIndex);
        buf.writeEnumConstant(hand);
        CustomPayloadC2SPacket packet = new CustomPayloadC2SPacket(LootBagPackets.OPEN, buf);
        player.networkHandler.sendPacket(packet);
    }
}
