package karashokleo.lootbag.api.common.content;

import com.mojang.datafixers.Products;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import karashokleo.lootbag.api.common.LootBagRegistry;
import net.minecraft.registry.entry.RegistryElementCodec;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.minecraft.util.Util;

public abstract class Content
{
    public static final Codec<Content> CODEC = LootBagRegistry.CONTENT_TYPE_REGISTRY.getCodec().dispatch(Content::getType, ContentType::codec);
    public static final Codec<RegistryEntry<Content>> ENTRY_CODEC = RegistryElementCodec.of(LootBagRegistry.CONTENT_KEY, CODEC);

    protected static <T extends Content> Products.P2<RecordCodecBuilder.Mu<T>, Icon, Integer> contentFields(RecordCodecBuilder.Instance<T> instance)
    {
        return instance.group(
                Icon.CODEC.fieldOf("icon").forGetter(Content::getIcon),
                Codec.INT.fieldOf("lines").forGetter(Content::getDescriptionLines)
        );
    }

    protected final Icon icon;
    protected final int descriptionLines;

    private String nameKey;
    private String descKey;

    protected Content(Icon icon, int descriptionLines)
    {
        this.icon = icon;
        this.descriptionLines = descriptionLines;
    }

    public Icon getIcon()
    {
        return icon;
    }

    public int getDescriptionLines()
    {
        return descriptionLines;
    }

    protected abstract ContentType<?> getType();

    public abstract void reward(ServerPlayerEntity player);

    public MutableText getName()
    {
        return Text.translatable(this.getNameKey());
    }

    public String getNameKey()
    {
        if (this.nameKey == null)
            this.nameKey = Util.createTranslationKey("content", LootBagRegistry.getContentId(this));
        return this.nameKey;
    }

    public MutableText getDesc(int lines)
    {
        return Text.translatable(this.getDescKey() + lines);
    }

    public String getDescKey()
    {
        if (this.descKey == null)
            this.descKey = getNameKey() + ".desc.";
        return this.descKey;
    }

    public record Icon(Identifier texture, int width, int height)
    {
        public static final Codec<Icon> CODEC = RecordCodecBuilder.create(
                ins -> ins.group(
                        Identifier.CODEC.fieldOf("texture").forGetter(Icon::texture),
                        Codec.INT.optionalFieldOf("width", 16).forGetter(Icon::width),
                        Codec.INT.optionalFieldOf("height", 16).forGetter(Icon::height)
                ).apply(ins, Icon::new)
        );

        public Icon(Identifier texture)
        {
            this(texture, 16, 16);
        }
    }
}
