package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.neoforge.LootBagMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SetScreenPacket(int slot, ResourceLocation bagId) implements CustomPacketPayload
{
    public static final Type<SetScreenPacket> TYPE = new Type<>(LootBagMod.id("set_screen"));
    public static final StreamCodec<RegistryFriendlyByteBuf, SetScreenPacket> STREAM_CODEC = StreamCodec.ofMember(SetScreenPacket::write, buf -> new SetScreenPacket(buf.readVarInt(), buf.readResourceLocation()));

    public void write(RegistryFriendlyByteBuf buf)
    {
        buf.writeVarInt(slot);
        buf.writeResourceLocation(bagId);
    }

    @Override
    public Type<SetScreenPacket> type()
    {
        return TYPE;
    }
}
