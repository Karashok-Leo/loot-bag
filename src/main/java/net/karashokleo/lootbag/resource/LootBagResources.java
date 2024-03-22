package net.karashokleo.lootbag.resource;

import net.karashokleo.lootbag.LootBag;
import net.karashokleo.lootbag.config.LootBagConfig;
import net.karashokleo.lootbag.config.initial.LootBagEntries;
import net.karashokleo.lootbag.content.LootBagTags;
import net.karashokleo.lootbag.content.LootBagType;
import net.minecraft.util.Identifier;
import pers.solid.brrp.v1.api.RuntimeResourcePack;
import pers.solid.brrp.v1.fabric.api.RRPCallback;
import pers.solid.brrp.v1.model.ModelJsonBuilder;

import java.util.Map;

public class LootBagResources
{
    public static final RuntimeResourcePack pack = RuntimeResourcePack.create(LootBag.id("rrp"));

    public static void init()
    {
        addModels();
        addTags();
        RRPCallback.BEFORE_VANILLA.register(resources -> resources.add(pack));
    }

    private static void addModels()
    {
        for (String id : LootBagConfig.getLootBagEntries().keySet())
        {
            try
            {
                pack.addModel(new Identifier(id).withPrefixedPath("item/"), ModelJsonBuilder.create(LootBag.id("item/generic")));
            } catch (Exception e)
            {
                LootBag.LOGGER.error("Error generating model of loot bag entry: " + id);
            }
        }
    }

    private static void addTags()
    {
        for (Map.Entry<String, LootBagEntries.Entry> entry : LootBagConfig.getLootBagEntries().entrySet())
        {
            try
            {
                LootBagTags.tagBuilderMap.get(entry.getValue().type).add(new Identifier(entry.getKey()));
            } catch (Exception e)
            {
                LootBag.LOGGER.error("Error generating tag of loot bag entry: " + entry.getKey());
            }
        }
        for (LootBagType type : LootBagType.values())
            pack.addTag(LootBagTags.tagMap.get(type), LootBagTags.tagBuilderMap.get(type));
    }
}
