package karashokleo.loot_bag.api.common.content;

import com.mojang.serialization.Codec;
import com.mojang.serialization.DataResult;
import karashokleo.loot_bag.internal.data.LootBagData;
import net.minecraft.text.MutableText;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public record ContentEntry(Identifier id, Content content, String nameKey, String descKey)
{
    public ContentEntry(Identifier id, Content content)
    {
        this(
                id,
                content,
                id.toTranslationKey("content"),
                id.toTranslationKey("content") + ".desc"
        );
    }

    public static final Codec<ContentEntry> CODEC = Identifier.CODEC.comapFlatMap(
            id ->
            {
                ContentEntry entry = LootBagData.CONTENTS.get(id);
                return entry == null ? DataResult.error(() -> LootBagData.unknownContentMessage(id)) : DataResult.success(entry);
            },
            ContentEntry::id
    );

    public MutableText getName()
    {
        return Text.translatable(this.nameKey);
    }

    public MutableText getDesc()
    {
        return Text.translatable(this.descKey);
    }
}
