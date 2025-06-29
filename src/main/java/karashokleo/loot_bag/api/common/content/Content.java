package karashokleo.loot_bag.api.common.content;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.server.network.ServerPlayerEntity;

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
}
