package karashokleo.lootbag.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;
import net.minecraft.util.Util;

public class LanguageProvider extends FabricLanguageProvider
{
    public LanguageProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder)
    {
        LootBagData.CONTENTS.forEach((key, content) ->
        {
            String nameKey = Util.createTranslationKey("content", key.getValue());
            builder.add(nameKey, "Content Name - " + key.getValue());
            for (int line = 0; line < content.getDescriptionLines(); line++)
                builder.add(nameKey + ".desc." + line, "Content Description - " + key.getValue() + " - Line " + line);
        });
        LootBagData.BAGS.keySet().forEach(key ->
                builder.add(Util.createTranslationKey("bag", key.getValue()), "Bag Name - " + key.getValue()));
    }
}
