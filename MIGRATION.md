# Forge 1.20.1 migration

Based on Fabric 1.20.1 master `4c968beb39ad9be32305f2662d768f8cef1064fb`.
Mod version: 1.3.1. Requires Minecraft 1.20.1, Forge 47.4.22+, and Java 17.

## Changes from master

- Architectury Loom builds and remaps the Forge jar while retaining the original Yarn source mappings. Forge metadata uses `loot_bag`; the original `loot-bag` resource namespace, item/type/loot-entry IDs, translations, NBT `BagId`, and datapack directories are preserved.
- Fabric lifecycle, registry, resource reload, item-color, creative-tab, networking, client-only annotations, and datagen hooks use Forge equivalents. The internal `internal.fabric` package stays unchanged to avoid unnecessary import churn.
- Public type-registry fields retain `Registry<T>` and the register methods, but are initialized during Forge `NewRegistryEvent` and are no longer final. Add-ons register types during the corresponding Forge `RegisterEvent`.
- Networking uses a Forge `SimpleChannel`; content-before-ACK-before-bags ordering and gameplay packet contents are preserved. Different loaders are not wire-compatible.
- Datagen provider constructors accept vanilla `DataOutput`; the original protected `configure` contract is preserved. Example pack metadata now uses vanilla text-component serialization, with the same description and pack format 15.
- All bag algorithms, reward implementations, GUI classes, item consumption and quick-open logic are unchanged. Icon drawing code is unchanged except for sided annotations. All eight shipped assets and the eleven example content/bag/language JSON files are byte-identical to master. No bags or contents are bundled by default.

## Build and verification

Run `bash gradlew build` with Java 17. `check` includes Forge `runData`, which generates the example pack and runs 15 runtime regression groups in the isolated `src/validation` source set. Test code is excluded from published jars.

On 2026-10-07, the packaged SRG jar passed actual-game verification in an official Forge 47.4.22 client without Loom/project classes:

- Empty defaults and invalid bags; original example and separate deterministic test packs
- Single, optional and random previews; item/enchantment/texture rendering and automatic random cycling
- Optional arrow wrap, chosen item/effect, Escape and inventory-key cancellation
- Item, effect, command and chest-loot-table rewards, plus custom loot-entry generation
- Survival consumption, creative no-consumption, main-hand single and offhand whole-stack quick opening
- Original sword damage 66 (durability 1495/1561), rarity, tint, translations and tooltips
- Live reload of an existing stack, then save/rejoin and another successful opening

The production client exited normally with code 0. Independent multiplayer, audio, and full dedicated-server gameplay were not tested. A production dedicated-server bootstrap reached the EULA boundary; no EULA was accepted. No CI workflow was added.

## Existing behavior left unchanged

Empty optional/random preview lists can fail, negative selection indices throw, and random weights are not validated. Resource loading logs limited error detail. Very long descriptions can exceed the viewport. Original example block/entity loot tables receive the existing CHEST context; the random example intentionally includes lethal commands. These unrelated baseline behaviors were not silently changed.
