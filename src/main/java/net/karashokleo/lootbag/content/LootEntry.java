package net.karashokleo.lootbag.content;

import net.minecraft.item.ItemStack;
import net.minecraft.loot.LootTable;
import net.minecraft.loot.LootTables;
import net.minecraft.loot.context.LootContextParameterSet;
import net.minecraft.loot.context.LootContextParameters;
import net.minecraft.loot.context.LootContextTypes;
import net.minecraft.registry.Registries;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.util.Identifier;

import java.util.Collections;
import java.util.List;

public class LootEntry
{
    public LootType type;
    public String value;
    public Icon icon;
    public String name;
    public String[] description;

    public LootEntry(LootType type, String value, Icon icon, String name, String[] description)
    {
        this.type=type;
        this.value = value;
        this.icon = icon;
        this.name = name;
        this.description = description;
    }

    public static class Icon
    {
        public String texture;
        public int width;
        public int height;

        public Icon(String texture, int width, int height)
        {
            this.texture = texture;
            this.width = width;
            this.height = height;
        }
    }

    public List<ItemStack> getLootStacks(ServerPlayerEntity player)
    {
        switch (type)
        {
            case ITEM ->
            {
                return List.of(Registries.ITEM.get(new Identifier(value)).getDefaultStack());
            }
            case LOOT_TABLE ->
            {
                Identifier id = new Identifier(value);
                if (id.equals(LootTables.EMPTY)) break;
                ServerWorld world = player.getServerWorld();
                LootContextParameterSet lootContextParameterSet = new LootContextParameterSet.Builder(world)
                        .add(LootContextParameters.THIS_ENTITY, player)
                        .add(LootContextParameters.ORIGIN, player.getPos())
                        .build(LootContextTypes.CHEST);
                LootTable lootTable = world.getServer().getLootManager().getLootTable(id);
                return lootTable.generateLoot(lootContextParameterSet);
            }
            case COMMAND -> player.server
                    .getCommandManager()
                    .executeWithPrefix(
                            player.getCommandSource()
                                    .withLevel(player.server.getFunctionPermissionLevel())
                                    .withSilent()
                            , value
                    );
        }
        return Collections.emptyList();
    }
}
