package karashokleo.loot_bag.api.client.texture;

import com.mojang.blaze3d.systems.RenderSystem;
import karashokleo.loot_bag.internal.fabric.LootBagMod;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.fabricmc.fabric.api.resource.SimpleResourceReloadListener;
import net.minecraft.resource.ResourceManager;
import net.minecraft.resource.ResourceType;
import net.minecraft.util.Identifier;
import net.minecraft.util.profiler.Profiler;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

/**
 * Caches the {@link AnimatedTexture} instances of the icons that are currently on screen
 * and ticks them once per client tick, like the vanilla texture atlas ticks its sprites.
 */
@Environment(EnvType.CLIENT)
public final class AnimatedTextureManager
{
    private static final Map<Identifier, AnimatedTexture> TEXTURES = new HashMap<>();
    private static boolean initialized;

    private AnimatedTextureManager()
    {
    }

    /**
     * Hooks the client tick and the resource reload. Called once from the client initializer.
     */
    public static void init()
    {
        if (initialized) return;
        initialized = true;

        ClientTickEvents.END_CLIENT_TICK.register(client -> tickAll());

        // A resource reload replaces the images under our feet, so drop everything and
        // let the icons load their frames again from the new packs.
        ResourceManagerHelper.get(ResourceType.CLIENT_RESOURCES)
                .registerReloadListener(new SimpleResourceReloadListener<Void>()
                {
                    @Override
                    public CompletableFuture<Void> load(ResourceManager manager, Profiler profiler, Executor executor)
                    {
                        return CompletableFuture.completedFuture(null);
                    }

                    @Override
                    public CompletableFuture<Void> apply(Void data, ResourceManager manager, Profiler profiler, Executor executor)
                    {
                        clear();
                        return CompletableFuture.completedFuture(null);
                    }

                    @Override
                    public Identifier getFabricId()
                    {
                        return LootBagMod.id("animated_textures");
                    }
                });
    }

    /**
     * Returns the animated texture of the given resource, loading it on first use.
     *
     * @return the texture, or {@code null} when the resource does not exist or is invalid
     */
    public static AnimatedTexture get(Identifier textureId)
    {
        AnimatedTexture texture = TEXTURES.get(textureId);
        if (texture == null)
        {
            texture = AnimatedTexture.load(textureId);
            TEXTURES.put(textureId, texture);
        }
        return texture.isBroken() ? null : texture;
    }

    private static void tickAll()
    {
        if (TEXTURES.isEmpty() || !RenderSystem.isOnRenderThread()) return;
        for (AnimatedTexture texture : TEXTURES.values()) texture.tick();
    }

    public static void clear()
    {
        if (!RenderSystem.isOnRenderThread())
        {
            RenderSystem.recordRenderCall(AnimatedTextureManager::clear);
            return;
        }
        TEXTURES.values().forEach(AnimatedTexture::close);
        TEXTURES.clear();
    }
}
