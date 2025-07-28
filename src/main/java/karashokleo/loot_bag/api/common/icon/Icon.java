package karashokleo.loot_bag.api.common.icon;

import com.mojang.datafixers.kinds.App;
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

    protected static <T extends Icon> App<RecordCodecBuilder.Mu<T>, Float> iconFields()
    {
        return Codec.FLOAT.optionalFieldOf("scale", 1.0F).forGetter(Icon::getScale);
    }

    public static final int SIZE = 64;

    protected final float scale;

    protected Icon(float scale)
    {
        this.scale = scale;
    }

    protected Icon()
    {
        this(1.0F);
    }

    public float getScale()
    {
        return scale;
    }

    protected abstract IconType<?> getType();

    /// override this method must be annotated with <code>@Environment(EnvType.CLIENT)</code> !!!
    @Environment(EnvType.CLIENT)
    public abstract void render(DrawContext context, MatrixStack matrices, float alpha, float delta);
}
