package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SetScreenPacket(int slot, Identifier bagId) implements FabricPacket
{
    public static final PacketType<SetScreenPacket> TYPE = PacketType.create(
            LootBagMod.id("set_screen"),
            buf -> new SetScreenPacket(
                    buf.readVarInt(),
                    buf.readIdentifier()
            )
    );

    @Override
    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(this.slot);
        buf.writeIdentifier(this.bagId);
    }

    @Override
    public PacketType<?> getType()
    {
        return TYPE;
    }
}
