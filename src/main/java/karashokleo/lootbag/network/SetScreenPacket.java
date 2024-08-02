package karashokleo.lootbag.network;

import karashokleo.lootbag.api.client.LootBagScreenRegistry;
import karashokleo.lootbag.api.client.screen.LootBagScreen;
import karashokleo.lootbag.api.common.LootBagRegistry;
import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.fabric.LootBagMod;
import net.fabricmc.fabric.api.networking.v1.FabricPacket;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.fabricmc.fabric.api.networking.v1.PacketType;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.network.PacketByteBuf;

public record SetScreenPacket(int slot, Bag bag) implements FabricPacket
{
    public static final PacketType<SetScreenPacket> TYPE = PacketType.create(
            LootBagMod.id("set_screen"),
            buf -> new SetScreenPacket(
                    buf.readVarInt(),
                    buf.readRegistryValue(LootBagRegistry.BAG_REGISTRY)
            )
    );

    @Override
    public void write(PacketByteBuf buf)
    {
        buf.writeVarInt(this.slot);
        buf.writeRegistryValue(LootBagRegistry.BAG_REGISTRY, bag);
    }

    @Override
    public PacketType<?> getType()
    {
        return TYPE;
    }

    @SuppressWarnings("unused")
    public static void handle(SetScreenPacket packet, ClientPlayerEntity player, PacketSender responseSender)
    {
        LootBagScreen<?> screen = LootBagScreenRegistry.getFactory(packet.bag().getType()).createScreen(packet.bag(), packet.slot);
        MinecraftClient.getInstance().setScreen(screen);
    }
}
