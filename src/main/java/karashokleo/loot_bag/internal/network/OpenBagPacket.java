package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import karashokleo.loot_bag.internal.item.LootBagItem;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.server.network.ServerPlayerEntity;

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

    @SuppressWarnings("unused")
    public static void handle(OpenBagPacket packet, ServerPlayerEntity player, PacketSender responseSender)
    {
        LootBagItem.open(player, packet.slot(), packet.selectedIndex());
    }
}
