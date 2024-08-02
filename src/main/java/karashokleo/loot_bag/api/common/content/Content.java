package karashokleo.loot_bag.api.common.content;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

public abstract class Content
{
    public static final Codec<Content> CODEC = LootBagRegistry.CONTENT_TYPE_REGISTRY.getCodec().dispatch(Content::getType, ContentType::codec);

    protected static <T extends Content> Products.P1<RecordCodecBuilder.Mu<T>, Icon> contentFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                Icon.CODEC.fieldOf("icon").forGetter(Content::getIcon)
        );
    }

    protected final Icon icon;

    protected Content(Icon icon)
    {
        this.icon = icon;
    }

    public Icon getIcon()
    {
        return icon;
    }

    protected abstract ContentType<?> getType();

    public abstract void reward(ServerPlayerEntity player);

    public record Icon(Identifier texture, int width, int height)
    {
        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(Icon::texture),
                        Codec.INT.optionalFieldOf("width", 16).forGetter(Icon::width),
                        Codec.INT.optionalFieldOf("height", 16).forGetter(Icon::height)
                ).apply(ins, Icon::new)
        );

        public Icon(Identifier texture)
        {
            this(texture, 16, 16);
        }
    }
}
