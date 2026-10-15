package karashokleo.loot_bag.api.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.Sheets;
import org.jetbrains.annotations.NotNull;

@Deprecated
public class AlphaVertexConsumerWrapper extends VertexConsumerWrapper
{
    private final int alpha;

    public AlphaVertexConsumerWrapper(VertexConsumer parent, int alpha)
    {
        super(parent);
        this.alpha = alpha;
    }

    @NotNull
    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha)
    {
        return super.setColor(red, green, blue, this.alpha);
    }

    public record Provider(MultiBufferSource vertexConsumerProvider, int alpha) implements MultiBufferSource
    {
        @NotNull
        @Override
        public VertexConsumer getBuffer(@NotNull RenderType layer)
        {
            RenderType renderLayer = Sheets.translucentItemSheet();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            return new AlphaVertexConsumerWrapper(vertexConsumer, alpha);
        }
    }
}
