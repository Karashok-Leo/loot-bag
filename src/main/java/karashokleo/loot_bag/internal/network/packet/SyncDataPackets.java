package karashokleo.loot_bag.internal.network.packet;

import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import karashokleo.loot_bag.api.common.bag.Bag;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.api.common.content.Content;
import karashokleo.loot_bag.api.common.content.ContentEntry;
import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

import java.util.Collection;
import java.util.HashSet;

public class SyncDataPackets
{
    public static final CustomPacketPayload.Type<SyncContentPacket> SYNC_CONTENT_TYPE = new CustomPacketPayload.Type<>(LootBagMod.id("sync_content"));
    public static final CustomPacketPayload.Type<SyncBagPacket> SYNC_BAG_TYPE = new CustomPacketPayload.Type<>(LootBagMod.id("sync_bag"));
    public static final ResourceLocation ACK_ID = LootBagMod.id("sync_ack");

    private static <T> T readJson(RegistryFriendlyByteBuf buf, Codec<T> codec)
    {
        return codec.parse(buf.registryAccess().createSerializationContext(JsonOps.INSTANCE), JsonParser.parseString(buf.readUtf())).getOrThrow();
    }

    private static <T> void writeJson(RegistryFriendlyByteBuf buf, Codec<T> codec, T value)
    {
        buf.writeUtf(codec.encodeStart(buf.registryAccess().createSerializationContext(JsonOps.INSTANCE), value).getOrThrow().toString());
    }

    public record SyncContentPacket(Collection<ContentEntry> entries) implements CustomPacketPayload
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncContentPacket> STREAM_CODEC = StreamCodec.ofMember(SyncContentPacket::write, buf ->
        {
            int size = buf.readVarInt();
            HashSet<ContentEntry> entries = new HashSet<>();
            for (int i = 0; i < size; i++) entries.add(new ContentEntry(buf.readResourceLocation(), readJson(buf, Content.CODEC)));
            return new SyncContentPacket(entries);
        });

        public void write(RegistryFriendlyByteBuf buf)
        {
            buf.writeVarInt(entries.size());
            for (ContentEntry entry : entries)
            {
                buf.writeResourceLocation(entry.id());
                writeJson(buf, Content.CODEC, entry.content());
            }
        }

        @Override
        public Type<SyncContentPacket> type() { return SYNC_CONTENT_TYPE; }
    }

    public record SyncBagPacket(Collection<BagEntry> entries) implements CustomPacketPayload
    {
        public static final StreamCodec<RegistryFriendlyByteBuf, SyncBagPacket> STREAM_CODEC = StreamCodec.ofMember(SyncBagPacket::write, buf ->
        {
            int size = buf.readVarInt();
            HashSet<BagEntry> entries = new HashSet<>();
            for (int i = 0; i < size; i++) entries.add(new BagEntry(buf.readResourceLocation(), readJson(buf, Bag.CODEC)));
            return new SyncBagPacket(entries);
        });

        public void write(RegistryFriendlyByteBuf buf)
        {
            buf.writeVarInt(entries.size());
            for (BagEntry entry : entries)
            {
                buf.writeResourceLocation(entry.id());
                writeJson(buf, Bag.CODEC, entry.bag());
            }
        }

        @Override
        public Type<SyncBagPacket> type() { return SYNC_BAG_TYPE; }
    }

    public record AckPacket(int count) implements CustomPacketPayload
    {
        public static final Type<AckPacket> TYPE = new Type<>(ACK_ID);
        public static final StreamCodec<RegistryFriendlyByteBuf, AckPacket> STREAM_CODEC = StreamCodec.of((buf, packet) -> buf.writeVarInt(packet.count()), buf -> new AckPacket(buf.readVarInt()));
        @Override
        public Type<AckPacket> type() { return TYPE; }
    }
}
