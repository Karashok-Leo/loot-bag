package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.data.LootBagData;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayNetworkHandler;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public class SyncDataPacket
{
    public static final Identifier ID = LootBagMod.id("sync_data");

    public static void write(PacketByteBuf buf)
    {
        buf.writeInt(LootBagData.CONTENTS.size());
        for (ContentEntry entry : LootBagData.CONTENTS.values())
        {
            buf.writeIdentifier(entry.id());
            buf.encodeAsJson(Content.CODEC, entry.content());
        }
        buf.writeInt(LootBagData.BAGS.size());
        for (BagEntry entry : LootBagData.BAGS.values())
        {
            buf.writeIdentifier(entry.id());
            buf.encodeAsJson(Bag.CODEC, entry.bag());
        }
    }

    @SuppressWarnings("unused")
    public static void handle(MinecraftClient client, ClientPlayNetworkHandler handler, PacketByteBuf buf, PacketSender responseSender)
    {
        int contentSize = buf.readInt();
        for (int i = 0; i < contentSize; i++)
            LootBagData.putContent(buf.readIdentifier(), buf.decodeAsJson(Content.CODEC));
        int bagSize = buf.readInt();
        for (int i = 0; i < bagSize; i++)
            LootBagData.putBag(buf.readIdentifier(), buf.decodeAsJson(Bag.CODEC));
    }
}
