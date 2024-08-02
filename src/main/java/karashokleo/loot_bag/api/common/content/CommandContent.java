package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.server.network.ServerPlayerEntity;

public class CommandContent extends Content
{
    public static final Codec<CommandContent> CODEC = RecordCodecBuilder.create(
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
    public void reward(ServerPlayerEntity player)
    {
        player.server
                .getCommandManager()
                .executeWithPrefix(
                        player.getCommandSource()
                                .withLevel(player.server.getFunctionPermissionLevel())
                                .withSilent(),
                        command
                );
    }
}
