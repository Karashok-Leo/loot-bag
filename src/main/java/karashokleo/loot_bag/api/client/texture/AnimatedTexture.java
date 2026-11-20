package karashokleo.loot_bag.api.client.texture;

import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.resource.metadata.AnimationResourceMetadata;
import net.minecraft.client.texture.NativeImage;
import net.minecraft.client.texture.NativeImageBackedTexture;
import net.minecraft.client.texture.SpriteDimensions;
import net.minecraft.resource.Resource;
import net.minecraft.util.Identifier;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * A client side animated texture built the same way vanilla animated sprites are built.
 * <p>
 * The frame grid comes from the image size and the frame size, the frame order and the
 * frame times come from the {@code .mcmeta} file next to the texture, and every client
 * tick the current frame is uploaded into the very same texture, exactly like
 * {@code SpriteContents.Ticker} does for a sprite atlas entry. The UV coordinates never
 * change, the pixels under them do.
 */
@Environment(EnvType.CLIENT)
public final class AnimatedTexture implements AutoCloseable
{
    private static final int EMPTY_FRAME_TIME = AnimationResourceMetadata.EMPTY_FRAME_TIME;

    private final Identifier id;
    private final NativeImage sheet;
    private final int frameWidth;
    private final int frameHeight;
    private final int frameColumns;
    private final List<Frame> frames;
    private final boolean interpolate;
    private final NativeImageBackedTexture texture;

    private NativeImage interpolationBuffer;
    private int frameIndex;
    private int subFrame;
    private boolean broken;

    /**
     * Creates a texture that renders nothing, because its resource could not be loaded.
     */
    private AnimatedTexture(Identifier id)
    {
        this.id = id;
        this.sheet = null;
        this.frameWidth = 0;
        this.frameHeight = 0;
        this.frameColumns = 1;
        this.frames = List.of();
        this.interpolate = false;
        this.texture = null;
        this.broken = true;
    }

    private AnimatedTexture(
            Identifier id,
            NativeImage sheet,
            int frameWidth,
            int frameHeight,
            int frameColumns,
            List<Frame> frames,
            boolean interpolate,
            NativeImageBackedTexture texture
    )
    {
        this.id = id;
        this.sheet = sheet;
        this.frameWidth = frameWidth;
        this.frameHeight = frameHeight;
        this.frameColumns = frameColumns;
        this.frames = frames;
        this.interpolate = interpolate;
        this.texture = texture;
        this.broken = false;
    }

    /**
     * Reads the texture, its {@code .mcmeta} animation metadata and its first frame.
     * Must be called on the render thread, because it creates a GL texture.
     */
    public static AnimatedTexture load(Identifier id)
    {
        Optional<Resource> optional = MinecraftClient.getInstance().getResourceManager().getResource(id);
        if (optional.isEmpty())
        {
            LootBagMod.LOGGER.warn("Animated icon texture '{}' does not exist", id);
            return new AnimatedTexture(id);
        }
        Resource resource = optional.get();

        AnimationResourceMetadata animation = AnimationResourceMetadata.EMPTY;
        try
        {
            animation = resource.getMetadata()
                    .decode(AnimationResourceMetadata.READER)
                    .orElse(AnimationResourceMetadata.EMPTY);
        } catch (IOException | RuntimeException e)
        {
            LootBagMod.LOGGER.error("Unable to read the animation metadata of '{}'", id, e);
        }

        NativeImage sheet;
        try (InputStream stream = resource.getInputStream())
        {
            sheet = NativeImage.read(stream);
        } catch (IOException | RuntimeException e)
        {
            LootBagMod.LOGGER.error("Unable to load the animated icon texture '{}'", id, e);
            return new AnimatedTexture(id);
        }

        SpriteDimensions dimensions = animation.getSize(sheet.getWidth(), sheet.getHeight());
        int frameWidth = dimensions.width();
        int frameHeight = dimensions.height();
        if (frameWidth <= 0 || frameHeight <= 0 ||
            frameWidth > sheet.getWidth() || frameHeight > sheet.getHeight())
        {
            LootBagMod.LOGGER.error(
                    "Invalid frame size {}x{} for the animated icon texture '{}' of size {}x{}",
                    frameWidth, frameHeight, id, sheet.getWidth(), sheet.getHeight()
            );
            sheet.close();
            return new AnimatedTexture(id);
        }

        int columns = sheet.getWidth() / frameWidth;
        int rows = sheet.getHeight() / frameHeight;
        int sheetFrames = columns * rows;

        List<Frame> frames = new ArrayList<>();
        animation.forEachFrame((index, time) -> frames.add(new Frame(index, time)));
        if (frames.isEmpty())
        {
            for (int i = 0; i < sheetFrames; i++)
                frames.add(new Frame(i, animation.getDefaultFrameTime()));
        } else
        {
            for (int i = frames.size() - 1; i >= 0; i--)
            {
                Frame frame = frames.get(i);
                if (frame.time > 0 && frame.index >= 0 && frame.index < sheetFrames) continue;
                LootBagMod.LOGGER.warn(
                        "Dropped invalid frame {} ({}) of the animated icon texture '{}'",
                        i, frame, id
                );
                frames.remove(i);
            }
        }
        if (frames.isEmpty()) frames.add(new Frame(0, EMPTY_FRAME_TIME));

        NativeImageBackedTexture texture = new NativeImageBackedTexture(frameWidth, frameHeight, false);
        // Icons are pixel art, so keep the texture sharp instead of blurring it.
        texture.setFilter(false, false);

        AnimatedTexture result = new AnimatedTexture(
                id, sheet, frameWidth, frameHeight, columns,
                List.copyOf(frames), animation.shouldInterpolate(), texture
        );
        result.uploadFrame(result.frames.get(0).index);
        return result;
    }

    /**
     * Advances the animation by one client tick and uploads the frame when it changed.
     */
    public void tick()
    {
        if (broken || frames.size() <= 1) return;

        Frame current = frames.get(frameIndex);
        subFrame++;
        if (subFrame >= current.time)
        {
            int previous = current.index;
            frameIndex = (frameIndex + 1) % frames.size();
            subFrame = 0;
            int next = frames.get(frameIndex).index;
            if (previous != next) uploadFrame(next);
        } else if (interpolate)
        {
            uploadInterpolatedFrame(current);
        }
    }

    private void uploadFrame(int index)
    {
        if (broken) return;
        int x = index % frameColumns * frameWidth;
        int y = index / frameColumns * frameHeight;
        // The texture has to be bound before the pixels are pushed into it. The last two
        // arguments are "mipmap" and "close the image afterwards", both false: the sheet
        // stays open so the next frame can be uploaded from it again.
        texture.bindTexture();
        sheet.upload(0, 0, 0, x, y, frameWidth, frameHeight, false, false);
    }

    private void uploadInterpolatedFrame(Frame current)
    {
        int next = frames.get((frameIndex + 1) % frames.size()).index;
        if (next == current.index)
        {
            uploadFrame(current.index);
            return;
        }
        if (interpolationBuffer == null)
            interpolationBuffer = new NativeImage(frameWidth, frameHeight, false);

        double ratio = 1.0 - (double) subFrame / (double) current.time;
        int currentX = current.index % frameColumns * frameWidth;
        int currentY = current.index / frameColumns * frameHeight;
        int nextX = next % frameColumns * frameWidth;
        int nextY = next / frameColumns * frameHeight;
        for (int y = 0; y < frameHeight; y++)
        {
            for (int x = 0; x < frameWidth; x++)
            {
                int from = sheet.getColor(currentX + x, currentY + y);
                int to = sheet.getColor(nextX + x, nextY + y);
                interpolationBuffer.setColor(x, y, mix(ratio, from, to));
            }
        }
        texture.bindTexture();
        interpolationBuffer.upload(0, 0, 0, false);
    }

    private static int mix(double ratio, int from, int to)
    {
        int red = mixChannel(ratio, from >> 16 & 0xFF, to >> 16 & 0xFF);
        int green = mixChannel(ratio, from >> 8 & 0xFF, to >> 8 & 0xFF);
        int blue = mixChannel(ratio, from & 0xFF, to & 0xFF);
        return from & 0xFF000000 | red << 16 | green << 8 | blue;
    }

    private static int mixChannel(double ratio, int from, int to)
    {
        return (int) (ratio * (double) from + (1.0 - ratio) * (double) to);
    }

    public Identifier getId()
    {
        return id;
    }

    public boolean isBroken()
    {
        return broken;
    }

    /**
     * The GL id of the texture that holds the current frame, or {@code -1} when the
     * texture could not be loaded.
     */
    public int getGlId()
    {
        return broken ? -1 : texture.getGlId();
    }

    public int getFrameWidth()
    {
        return frameWidth;
    }

    public int getFrameHeight()
    {
        return frameHeight;
    }

    public int getFrameCount()
    {
        return frames.size();
    }

    @Override
    public void close()
    {
        if (interpolationBuffer != null)
        {
            interpolationBuffer.close();
            interpolationBuffer = null;
        }
        // The backing texture closes its own image and releases its GL id.
        if (texture != null) texture.close();
        if (sheet != null) sheet.close();
        broken = true;
    }

    private record Frame(int index, int time)
    {
    }
}
