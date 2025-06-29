package karashokleo.loot_bag.api.client.render;

import net.minecraft.client.render.RenderLayer;
import net.minecraft.client.render.TexturedRenderLayers;
import net.minecraft.client.render.VertexConsumer;
import net.minecraft.client.render.VertexConsumerProvider;

public class AlphaVertexConsumerWrapper extends VertexConsumerWrapper
{
    private final int alpha;

    public AlphaVertexConsumerWrapper(VertexConsumer parent, int alpha)
    {
        super(parent);
        this.alpha = alpha;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        return super.color(red, green, blue, this.alpha);
    }

    public record Provider(VertexConsumerProvider vertexConsumerProvider, int alpha) implements VertexConsumerProvider
    {
        @Override
        public VertexConsumer getBuffer(RenderLayer layer)
        {
            RenderLayer renderLayer = TexturedRenderLayers.getEntityTranslucentCull();
            VertexConsumer vertexConsumer = vertexConsumerProvider.getBuffer(renderLayer);
            return new AlphaVertexConsumerWrapper(vertexConsumer, alpha);
        }
    }
}
