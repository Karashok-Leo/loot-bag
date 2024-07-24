package karashokleo.lootbag.network;

import karashokleo.lootbag.client.LootBagScreenRegistry;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import karashokleo.lootbag.LootBag;
import karashokleo.lootbag.client.screen.LootBagScreen;
import karashokleo.lootbag.content.logic.LootBagRegistry;
import karashokleo.lootbag.content.logic.bag.Bag;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;
import net.minecraft.util.Identifier;

public record SetScreenPacket(int slot, Identifier bagId) implements FabricPacket
{
    public static final PacketType<SetScreenPacket> TYPE = PacketType.create(LootBag.id("set_screen"), buf -> new SetScreenPacket(buf.readVarInt(), buf.readIdentifier()));

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

    public static void handle(SetScreenPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        Bag bag = LootBagRegistry.BAG_REGISTRY.get(packet.bagId);
        if (bag == null) return;
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(bag.getType()).createScreen(bag, packet.slot);
        MinecraftClient.getInstance().setScreen(screen);
    }
}
