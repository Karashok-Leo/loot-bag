package karashokleo.loot_bag.api.client.render;

import karashokleo.loot_bag.api.common.icon.Icon;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.Drawable;
import net.minecraft.client.util.math.MatrixStack;

public class DrawableIcon implements Drawable
{
    private Icon icon;
    private int x = 0;
    private int y = 0;
    private float scale = 1;
    private float alpha = 1;

    public DrawableIcon(Icon icon)
    {
        this.icon = icon;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public void setIcon(Icon icon)
    {
        this.icon = icon;
    }

    public int getX()
    {
        return x;
    }

    public void setX(int x)
    {
        this.x = x;
    }

    public int getY()
    {
        return y;
    }

    public void setY(int y)
    {
        this.y = y;
    }

    public float getScale()
    {
        return scale;
    }

    public void setScale(float scale)
    {
        this.scale = scale;
    }

    public float getAlpha()
    {
        return alpha;
    }

    public void setAlpha(float alpha)
    {
        this.alpha = alpha;
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        MatrixStack matrixStack = context.getMatrices();
        matrixStack.push();
        matrixStack.translate(x, y, 150);
        matrixStack.scale(scale, scale, 1);
        icon.render(context, matrixStack, alpha, delta);
        matrixStack.pop();
    }
}
