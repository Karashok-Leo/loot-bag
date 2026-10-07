package karashokleo.loot_bag.internal.data;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.data.DataOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.DataWriter;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

/** Forge replacement for FabricCodecDataProvider; keeps the existing configure API. */
public abstract class CodecDataProvider<T> implements DataProvider
{
    private final DataOutput.PathResolver resolver;
    private final Codec<T> codec;

    protected CodecDataProvider(DataOutput output, String directory, Codec<T> codec)
    {
        this.resolver = output.getResolver(DataOutput.OutputType.DATA_PACK, directory);
        this.codec = codec;
    }

    protected abstract void configure(BiConsumer<Identifier, T> provider);

    @Override
    public CompletableFuture<?> run(DataWriter writer)
    {
        List<CompletableFuture<?>> futures = new ArrayList<>();
        configure((id, value) -> futures.add(DataProvider.writeToPath(writer,
                codec.encodeStart(JsonOps.INSTANCE, value).getOrThrow(false, message -> {
                    throw new IllegalStateException(message);
                }), resolver.resolveJson(id))));
        return CompletableFuture.allOf(futures.toArray(CompletableFuture[]::new));
    }
}
