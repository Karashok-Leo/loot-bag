package karashokleo.loot_bag.internal.data;

import net.minecraft.data.DataOutput;
import karashokleo.loot_bag.internal.fabric.LootBagMod;

public class LanguageProvider extends net.minecraftforge.common.data.LanguageProvider
{
    public LanguageProvider(DataOutput dataOutput)
    {
        super(dataOutput, LootBagMod.MOD_ID, "en_us");
    }

    @Override
    protected void addTranslations()
    {
        LootBagDataGenerator.CONTENTS.forEach(entry ->
        {
            add(entry.nameKey(), "Content Name - " + entry.id());
            add(entry.descKey(), "Content Description - " + entry.id() + " - " +
                    "\n......" +
                    "\n............" +
                    "\n.................." +
                    "\n........................" +
                    "\n.............................." +
                    "\n...................................." +
                    "\n.........................................." +
                    "\n................................................" +
                    "\n......................................................" +
                    "\n............................................................" +
                    "\n.................................................................." +
                    "\n........................................................................" +
                    "\nloooooooooooooooong enough to display multi-line text effects");
        });
        LootBagDataGenerator.BAGS.forEach(entry ->
                add(entry.nameKey(), "Bag Name - " + entry.id()));
    }
}
