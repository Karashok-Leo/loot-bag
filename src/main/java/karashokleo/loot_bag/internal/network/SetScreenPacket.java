package karashokleo.loot_bag.internal.network;

import karashokleo.loot_bag.api.client.LootBagScreenRegistry;
import karashokleo.loot_bag.api.client.screen.LootBagScreen;
import karashokleo.loot_bag.api.common.bag.BagEntry;
import karashokleo.loot_bag.internal.data.LootBagData;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
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

    @SuppressWarnings("unused")
    public static void handle(SetScreenPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        BagEntry entry = LootBagData.BAGS.get(packet.bagId());
        if (entry == null) return;
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(entry.bag().getType()).createScreen(entry.bag(), packet.slot);
        MinecraftClient.getInstance().setScreen(screen);
    }
}
