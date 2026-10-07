package karashokleo.loot_bag.api.common.icon;

import com.mojang.datafixers.kinds.App;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.common.LootBagRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.minecraft.client.gui.GuiGraphics;
import com.mojang.blaze3d.vertex.PoseStack;

public abstract class Icon
{
    public static final Codec<Icon> CODEC = LootBagRegistry.ICON_TYPE_REGISTRY.byNameCodec().dispatch(Icon::getType, IconType::codec);

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

    /// override this method must be annotated with <code>@OnlyIn(Dist.CLIENT)</code> !!!
    @OnlyIn(Dist.CLIENT)
    public abstract void render(GuiGraphics context, PoseStack matrices, float alpha, float delta);
}
