package net.karashokleo.lootbag.resource;

import net.karashokleo.lootbag.LootBag;
import net.karashokleo.lootbag.config.LootBagConfig;
import net.karashokleo.lootbag.config.initial.LootBagEntries;
import net.karashokleo.lootbag.content.LootBagTags;
import net.minecraft.registry.tag.TagBuilder;
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
            pack.addModel(new Identifier(id).withPrefixedPath("item/"), ModelJsonBuilder.create(LootBag.id("item/generic")));
    }

    private static void addTags()
    {
        for (Map.Entry<String, LootBagEntries.Entry> entry : LootBagConfig.getLootBagEntries().entrySet())
            pack.addTag(LootBagTags.tagMap.get(entry.getValue().type), TagBuilder.create().add(new Identifier(entry.getKey())));
    }
}
