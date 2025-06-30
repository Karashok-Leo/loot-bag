package karashokleo.loot_bag.api.common.icon;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.util.math.MatrixStack;

public abstract class Icon
{
    public static final Codec<Icon> CODEC = LootBagRegistry.ICON_TYPE_REGISTRY.getCodec().dispatch(Icon::getType, IconType::codec);

    protected static <T extends Icon> Products.P2<RecordCodecBuilder.Mu<T>, Float, Boolean> iconFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Icon::getScale),
                Codec.BOOL.optionalFieldOf("translucent", true).forGetter(Icon::isTranslucent)
        );
    }

    public static final int SIZE = 64;

    protected final float scale;
    protected final boolean translucent;

    protected Icon(float scale, boolean translucent)
    {
        this.scale = scale;
        this.translucent = translucent;
    }

    protected Icon()
    {
        this(1.0F, true);
    }

    public float getScale()
    {
        return scale;
    }

    public boolean isTranslucent()
    {
        return translucent;
    }

    protected abstract IconType<?> getType();

    /// override this method must be annotated with <code>@Environment(EnvType.CLIENT)</code> !!!
    @Environment(EnvType.CLIENT)
    public abstract void render(DrawContext context, MatrixStack matrices, float alpha, float delta);
}
