package karashokleo.lootbag.api.common;

import karashokleo.lootbag.api.common.bag.Bag;
import karashokleo.lootbag.api.common.bag.BagType;
import karashokleo.lootbag.api.common.content.Content;
import karashokleo.lootbag.api.common.content.ContentType;
import karashokleo.lootbag.fabric.LootBagMod;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;

public class LootBagRegistryKeys
{
    public static final RegistryKey<Registry<ContentType<?>>> CONTENT_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("content_type"));
    public static final RegistryKey<Registry<Content>> CONTENT_KEY = RegistryKey.ofRegistry(LootBagMod.id("content"));
    public static final RegistryKey<Registry<BagType<?>>> BAG_TYPE_KEY = RegistryKey.ofRegistry(LootBagMod.id("bag_type"));
    public static final RegistryKey<Registry<Bag>> BAG_KEY = RegistryKey.ofRegistry(LootBagMod.id("bag"));
}
