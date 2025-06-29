package karashokleo.loot_bag.api.client.render;

import net.minecraft.client.render.VertexConsumer;

public abstract class VertexConsumerWrapper implements VertexConsumer
{
    protected final VertexConsumer parent;

    public VertexConsumerWrapper(VertexConsumer parent)
    {
        this.parent = parent;
    }

    @Override
    public VertexConsumer vertex(double x, double y, double z)
    {
        this.parent.vertex(x, y, z);
        return this;
    }

    @Override
    public VertexConsumer color(int red, int green, int blue, int alpha)
    {
        this.parent.color(red, green, blue, alpha);
        return this;
    }

    @Override
    public VertexConsumer texture(float u, float v)
    {
        this.parent.texture(u, v);
        return this;
    }

    @Override
    public VertexConsumer overlay(int u, int v)
    {
        this.parent.overlay(u, v);
        return this;
    }

    @Override
    public VertexConsumer light(int u, int v)
    {
        this.parent.light(u, v);
        return this;
    }

    @Override
    public VertexConsumer normal(float x, float y, float z)
    {
        this.parent.normal(x, y, z);
        return this;
    }

    @Override
    public void next()
    {
        this.parent.next();
    }

    @Override
    public void fixedColor(int red, int green, int blue, int alpha)
    {
        this.parent.fixedColor(red, green, blue, alpha);
    }

    @Override
    public void unfixColor()
    {
        this.parent.unfixColor();
    }
}
