package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
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

    public static final ContentType<CommandContent> TYPE = new ContentType<>(CODEC);

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
        return TYPE;
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
