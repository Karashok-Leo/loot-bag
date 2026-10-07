package karashokleo.loot_bag.api.client.render;

import com.mojang.blaze3d.vertex.VertexConsumer;

public abstract class VertexConsumerWrapper implements VertexConsumer
{
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexConsumer addVertex(float x, float y, float z)
    {
        this.parent.addVertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer setColor(int red, int green, int blue, int alpha)
    {
        this.parent.setColor(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer setUv(float u, float v)
    {
        this.parent.setUv(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv1(int u, int v)
    {
        this.parent.setUv1(u, v);
        return this;
    }

    @Override
    public VertexConsumer setUv2(int u, int v)
    {
        this.parent.setUv2(u, v);
        return this;
    }

    @Override
    public VertexConsumer setNormal(float x, float y, float z)
    {
        this.parent.setNormal(x, y, z);
        return this;
    }

}
