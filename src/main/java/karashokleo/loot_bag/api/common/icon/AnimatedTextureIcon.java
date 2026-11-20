package karashokleo.loot_bag.api.common.icon;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.loot_bag.api.client.texture.AnimatedTexture;
import karashokleo.loot_bag.api.client.texture.AnimatedTextureManager;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.util.Identifier;

/**
 * An icon that renders an animated texture, using the vanilla animation format: the frames
 * are declared by the {@code .mcmeta} file of the texture, the frame grid is calculated
 * from the image size, and the frames are cycled the way vanilla animated sprites are.
 * <pre>{@code
 * {
 *   "type": "loot-bag:animated_texture",
 *   "texture": "minecraft:textures/block/fire_0.png",
 *   "scale": 1.0
 * }
 * }</pre>
 * The frames are taken from the {@code .mcmeta} file that ships next to the texture, so any
 * animated vanilla texture works as it is:
 * <pre>{@code
 * { "animation": { "frametime": 5, "frames": [0, 1, 2, 3] } }
 * }</pre>
 * Without such a file the texture is simply drawn as a single static frame.
 */
public class AnimatedTextureIcon extends TextureIcon
{
    public static final Codec<AnimatedTextureIcon> CODEC = RecordCodecBuilder.create(
            ins -> ins.group(
                    Identifier.CODEC.fieldOf("texture").forGetter(AnimatedTextureIcon::getTexture)
            ).and(iconFields()).apply(ins, AnimatedTextureIcon::new)
    );

    public static final IconType<AnimatedTextureIcon> TYPE = new IconType<>(CODEC);

    public AnimatedTextureIcon(Identifier texture, float scale)
    {
        // The whole frame of the animated texture is drawn, so the UV bounds stay at 0..1.
        super(texture, 0F, 0F, 1F, 1F, scale);
    }

    public AnimatedTextureIcon(Identifier texture)
    {
        this(texture, 1.0F);
    }

    @Override
    public IconType<?> getType()
    {
        return TYPE;
    }

    @Environment(EnvType.CLIENT)
    @Override
    protected boolean bindTexture()
    {
        AnimatedTexture animated = AnimatedTextureManager.get(this.texture);
        if (animated == null) return false;
        // Binding the texture of the current frame keeps the UVs constant while the
        // pixels inside that texture change, just like a vanilla animated sprite.
        com.mojang.blaze3d.systems.RenderSystem.setShaderTexture(0, animated.getGlId());
        return true;
    }
}
