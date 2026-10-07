package karashokleo.loot_bag.internal.network.packet;

import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SetScreenPacket(int slot, Identifier bagId)
{
    public static SetScreenPacket read(PacketByteBuf buf)
    {
        return new SetScreenPacket(buf.readVarInt(), buf.readIdentifier());
    }

    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(this.slot);
        buf.writeIdentifier(this.bagId);
    }
}
