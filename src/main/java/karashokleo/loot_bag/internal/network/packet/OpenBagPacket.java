package karashokleo.loot_bag.internal.network.packet;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.network.PacketByteBuf;

public record OpenBagPacket(int slot, int selectedIndex) implements FabricPacket
{
    public static final PacketType<OpenBagPacket> TYPE = PacketType.create(LootBagMod.id("open_bag"), buf -> new OpenBagPacket(buf.readVarInt(), buf.readInt()));

    @Override
    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(slot);
        buf.writeInt(selectedIndex);
    }

    @Override
    public PacketType<?> getType()
    {
        return TYPE;
    }
}
