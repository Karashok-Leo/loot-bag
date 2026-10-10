package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.neoforged.neoforge.registries.DeferredHolder;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.server.level.ServerPlayer;

public class CommandContent extends Content
{
    public static final MapCodec<CommandContent> CODEC = RecordCodecBuilder.mapCodec(
            ins -> ins.group(
                    Codec.STRING.fieldOf("command").forGetter(CommandContent::getCommand)
            ).and(contentFields(ins).t1()).apply(ins, CommandContent::new)
    );

    public static final DeferredHolder<ContentType<?>, ContentType<CommandContent>> TYPE = LootBagRegistry.COMMAND_CONTENT;

    protected final String command;

    public CommandContent(String command, Icon icon)
    {
        super(icon);
        this.command = command;
    }

    public String getCommand()
    {
        return command;
    }

    @Override
    protected ContentType<?> getType()
    {
        return TYPE.get();
    }

    @Override
    public void reward(ServerPlayer player)
    {
        player.server
                .getCommands()
                .performPrefixedCommand(
                        player.createCommandSourceStack()
                                .withPermission(player.server.getFunctionCompilationLevel())
                                .withSuppressedOutput(),
                        command
                );
    }
}
