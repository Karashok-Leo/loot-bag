package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record OpenBagPacket(int slot, int selectedIndex) implements CustomPacketPayload
{
    public static final Type<OpenBagPacket> TYPE = new Type<>(LootBagMod.id("open_bag"));
    public static final StreamCodec<RegistryFriendlyByteBuf, OpenBagPacket> STREAM_CODEC = StreamCodec.ofMember(OpenBagPacket::write, buf -> new OpenBagPacket(buf.readVarInt(), buf.readInt()));

    public void write(RegistryFriendlyByteBuf buf)
    {
        buf.writeVarInt(slot);
        buf.writeInt(selectedIndex);
    }

    @Override
    public Type<OpenBagPacket> type()
    {
        return TYPE;
    }
}
