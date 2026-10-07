package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.PacketByteBuf;

public record OpenBagPacket(int slot, int selectedIndex) implements CustomPayload
{
    public static final Id<OpenBagPacket> TYPE = new Id<>(LootBagMod.id("open_bag"));
    public static final PacketCodec<RegistryByteBuf, OpenBagPacket> CODEC = PacketCodec.of(OpenBagPacket::write, buf -> new OpenBagPacket(buf.readVarInt(), buf.readInt()));

    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(slot);
        buf.writeInt(selectedIndex);
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return TYPE;
    }
}
