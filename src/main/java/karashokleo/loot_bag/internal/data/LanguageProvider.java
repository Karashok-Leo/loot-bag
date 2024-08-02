package karashokleo.loot_bag.internal.data;

import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput;
import net.fabricmc.fabric.api.datagen.v1.provider.FabricLanguageProvider;

public class LanguageProvider extends FabricLanguageProvider
{
    public LanguageProvider(FabricDataOutput dataOutput)
    {
        super(dataOutput);
    }

    @Override
    public void generateTranslations(TranslationBuilder builder)
    {
        LootBagDataGenerator.CONTENTS.forEach(entry ->
        {
            builder.add(entry.nameKey(), "Content Name - " + entry.id());
            builder.add(entry.descKey(), "Content Description - " + entry.id() + " - " +
                    ".........................................................................." +
                    ".........................................................................." +
                    ".........................................................................." +
                    ".........................................................................." +
                    "loooooooooooooooong enough to display multi-line text effects");
        });
        LootBagDataGenerator.BAGS.forEach(entry ->
                builder.add(entry.nameKey(), "Bag Name - " + entry.id()));
    }
}
