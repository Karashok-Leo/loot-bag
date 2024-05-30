package net.karashokleo.lootbag.client.screen;

import net.karashokleo.lootbag.content.LootEntry;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.text.Text;
import net.minecraft.util.Hand;

public class SingleLootBagScreen extends BaseLootBagScreen
{
    private static final String TEXT_SINGLE = "text.loot-bag.single_screen";

    public SingleLootBagScreen(LootEntry[] lootEntries, Hand hand)
    {
        super(Text.translatable(TEXT_SINGLE), lootEntries, hand);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta)
    {
        super.render(context, mouseX, mouseY, delta);
        drawIcon(context, getCurrentEntry().icon, 0, 1, 1F);
    }
}
