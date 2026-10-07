package karashokleo.loot_bag.internal.network.packet;

import net.minecraft.network.PacketByteBuf;

public record OpenBagPacket(int slot, int selectedIndex)
{
    public static OpenBagPacket read(PacketByteBuf buf)
    {
        return new OpenBagPacket(buf.readVarInt(), buf.readInt());
    }

    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(slot);
        buf.writeInt(selectedIndex);
    }
}
