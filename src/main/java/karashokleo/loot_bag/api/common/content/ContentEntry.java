package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import karashokleo.loot_bag.api.LootBagManager;
import karashokleo.loot_bag.internal.data.ConstantTexts;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public record ContentEntry(ResourceLocation id, Content content, String nameKey, String descKey)
{
    public ContentEntry(ResourceLocation id, Content content)
    {
        this(
                id,
                content,
                id.toLanguageKey("content"),
                id.toLanguageKey("content") + ".desc"
        );
    }

    public static final Codec<ContentEntry> CODEC = ResourceLocation.CODEC.comapFlatMap(
            id ->
            {
                ContentEntry entry = LootBagManager.getInstance().getContentEntry(id);
                return entry == null ? DataResult.error(() -> ConstantTexts.unknownContentMessage(id)) : DataResult.success(entry);
            },
            ContentEntry::id
    );

    public MutableComponent getName()
    {
        return Component.translatable(this.nameKey);
    }

    public MutableComponent getDesc()
    {
        return Component.translatable(this.descKey);
    }
}
