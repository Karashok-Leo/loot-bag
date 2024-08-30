package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashSet;

public class SyncDataPackets
{
    public static final PacketType<SyncContentPacket> SYNC_CONTENT_TYPE = PacketType.create(
            LootBagMod.id("sync_content"),
            buf ->
            {
                HashSet<ContentEntry> entries = buf.readCollection(
                        HashSet::new,
                        packetByteBuf ->
                        {
                            Identifier id = packetByteBuf.readIdentifier();
                            Content content = packetByteBuf.decodeAsJson(Content.CODEC);
                            return new ContentEntry(id, content);
                        }
                );
                return new SyncContentPacket(entries);
            }
    );
    public static final PacketType<SyncBagPacket> SYNC_BAG_TYPE = PacketType.create(
            LootBagMod.id("sync_bag"),
            buf ->
            {
                HashSet<BagEntry> entries = buf.readCollection(
                        HashSet::new,
                        packetByteBuf ->
                        {
                            Identifier id = packetByteBuf.readIdentifier();
                            Bag bag = packetByteBuf.decodeAsJson(Bag.CODEC);
                            return new BagEntry(id, bag);
                        }
                );
                return new SyncBagPacket(entries);
            }
    );
    public static final Identifier ACK_ID = LootBagMod.id("sync_ack");

    public record SyncContentPacket(Collection<ContentEntry> entries) implements FabricPacket
    {
        @Override
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

        @Override
        public PacketType<?> getType()
        {
            return SYNC_CONTENT_TYPE;
        }
    }

    public record SyncBagPacket(Collection<BagEntry> entries) implements FabricPacket
    {
        @Override
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

        @Override
        public PacketType<?> getType()
        {
            return SYNC_BAG_TYPE;
        }
    }
}
