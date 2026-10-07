package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import net.minecraft.network.PacketByteBuf;

import java.util.Collection;
import java.util.HashSet;

public class SyncDataPackets
{
    public record AckPacket(int count)
    {
        public static AckPacket read(PacketByteBuf buf) { return new AckPacket(buf.readVarInt()); }
        public void write(PacketByteBuf buf) { buf.writeVarInt(count); }
    }

    public static SyncContentPacket readContent(PacketByteBuf buf)
    {
        return new SyncContentPacket(buf.readCollection(HashSet::new, buffer ->
                new ContentEntry(buffer.readIdentifier(), buffer.decodeAsJson(Content.CODEC))));
    }

    public static SyncBagPacket readBag(PacketByteBuf buf)
    {
        return new SyncBagPacket(buf.readCollection(HashSet::new, buffer ->
                new BagEntry(buffer.readIdentifier(), buffer.decodeAsJson(Bag.CODEC))));
    }

    public record SyncContentPacket(Collection<ContentEntry> entries)
    {
        public void write(PacketByteBuf buf)
        {
            buf.writeCollection(
                    entries,
                    (packetByteBuf, entry) ->
                    {
                        packetByteBuf.writeIdentifier(entry.id());
                        packetByteBuf.encodeAsJson(Content.CODEC, entry.content());
                    }
            );
        }
    }

    public record SyncBagPacket(Collection<BagEntry> entries)
    {
        public void write(PacketByteBuf buf)
        {
            buf.writeCollection(
                    entries,
                    (packetByteBuf, entry) ->
                    {
                        packetByteBuf.writeIdentifier(entry.id());
                        packetByteBuf.encodeAsJson(Bag.CODEC, entry.bag());
                    }
            );
        }
    }
}
