package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.minecraft.network.packet.CustomPayload;
import net.minecraft.network.codec.PacketCodec;
import net.minecraft.network.RegistryByteBuf;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SetScreenPacket(int slot, Identifier bagId) implements CustomPayload
{
    public static final Id<SetScreenPacket> TYPE = new Id<>(LootBagMod.id("set_screen"));
    public static final PacketCodec<RegistryByteBuf, SetScreenPacket> CODEC = PacketCodec.of(SetScreenPacket::write, buf -> new SetScreenPacket(buf.readVarInt(), buf.readIdentifier()));

    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(this.slot);
        buf.writeIdentifier(this.bagId);
    }

    @Override
    public Id<? extends CustomPayload> getId()
    {
        return TYPE;
    }
}
