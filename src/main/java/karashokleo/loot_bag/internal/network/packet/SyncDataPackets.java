package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.RegistryByteBuf;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

import java.util.Collection;
import java.util.HashSet;

public class SyncDataPackets
{
    public static final CustomPayload.Id<SyncContentPacket> SYNC_CONTENT_TYPE = new CustomPayload.Id<>(LootBagMod.id("sync_content"));
    public static final PacketCodec<RegistryByteBuf, SyncContentPacket> SYNC_CONTENT_CODEC = PacketCodec.of(
            SyncContentPacket::write,
            buf ->
            {
                HashSet<ContentEntry> entries = buf.readCollection(
                        HashSet::new,
                        packetByteBuf ->
                        {
                            Identifier id = packetByteBuf.readIdentifier();
                            Content content = readJson(buf, Content.CODEC);
                            return new ContentEntry(id, content);
                        }
                );
                return new SyncContentPacket(entries);
            }
    );
    public static final CustomPayload.Id<SyncBagPacket> SYNC_BAG_TYPE = new CustomPayload.Id<>(LootBagMod.id("sync_bag"));
    public static final PacketCodec<RegistryByteBuf, SyncBagPacket> SYNC_BAG_CODEC = PacketCodec.of(
            SyncBagPacket::write,
            buf ->
            {
                HashSet<BagEntry> entries = buf.readCollection(
                        HashSet::new,
                        packetByteBuf ->
                        {
                            Identifier id = packetByteBuf.readIdentifier();
                            Bag bag = readJson(buf, Bag.CODEC);
                            return new BagEntry(id, bag);
                        }
                );
                return new SyncBagPacket(entries);
            }
    );
    public static final Identifier ACK_ID = LootBagMod.id("sync_ack");

    public record SyncContentPacket(Collection<ContentEntry> entries) implements CustomPayload
    {
        public void write(RegistryByteBuf buf)
        {
            buf.writeCollection(
                    entries,
                    (packetByteBuf, entry) ->
                    {
                        packetByteBuf.writeIdentifier(entry.id());
                        writeJson(buf, Content.CODEC, entry.content());
                    }
            );
        }

        @Override
        public Id<? extends CustomPayload> getId()
        {
            return SYNC_CONTENT_TYPE;
        }
    }

    public record SyncBagPacket(Collection<BagEntry> entries) implements CustomPayload
    {
        public void write(RegistryByteBuf buf)
        {
            buf.writeCollection(
                    entries,
                    (packetByteBuf, entry) ->
                    {
                        packetByteBuf.writeIdentifier(entry.id());
                        writeJson(buf, Bag.CODEC, entry.bag());
                    }
            );
        }

        @Override
        public Id<? extends CustomPayload> getId()
        {
            return SYNC_BAG_TYPE;
        }
    }

    public record AckPacket(int count) implements CustomPayload
    {
        public static final Id<AckPacket> TYPE = new Id<>(ACK_ID);
        public static final PacketCodec<RegistryByteBuf, AckPacket> CODEC = PacketCodec.of(
                (packet, buf) -> buf.writeVarInt(packet.count()),
                buf -> new AckPacket(buf.readVarInt())
        );

        @Override
        public Id<? extends CustomPayload> getId()
        {
            return TYPE;
        }
    }

    private static <T> T readJson(RegistryByteBuf buf, Codec<T> codec)
    {
        return codec.parse(buf.getRegistryManager().getOps(JsonOps.INSTANCE), JsonParser.parseString(buf.readString())).getOrThrow();
    }

    private static <T> void writeJson(RegistryByteBuf buf, Codec<T> codec, T value)
    {
        buf.writeString(codec.encodeStart(buf.getRegistryManager().getOps(JsonOps.INSTANCE), value).getOrThrow().toString());
    }
}
