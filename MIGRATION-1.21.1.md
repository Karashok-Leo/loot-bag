# Fabric 1.21.1 migration

Baseline: `master` commit `4c968beb39ad9be32305f2662d768f8cef1064fb` (Fabric 1.20.1).

## Preserved behavior and data

- Single, optional and weighted random bags; quick opening, off-hand whole-stack opening, creative consumption rules and reward logic are unchanged.
- All four content types (item, loot table, command, effect), both icon types, custom type registries, translation keys and data directories remain present.
- Bag definitions still use `data/<namespace>/loot-bag/bag/`; content definitions still use `data/<namespace>/loot-bag/content/`.
- The mod contains no built-in bag/content definitions. `example/` remains a separate optional pack.
- Existing textures, item models, language files and license are unchanged. Screen geometry, arrow atlas/UVs, 10-tick scrolling and 40-tick random cycling remain unchanged.
- The existing `BagId` field is retained inside the vanilla `minecraft:custom_data` component. For example: `/give @s loot-bag:loot_bag[minecraft:custom_data={BagId:"loot-bag:optional"}] 1`.
- Per the requested scope, complex item definitions use native Minecraft 1.21.1 `ItemStack.CODEC` and `count`/`components` syntax only. No legacy `Count`/`tag` conversion or compatibility codec is included. Count and empty-stack behavior follow vanilla 1.21.1. The original simple item-identifier shorthand remains supported. Checked-in complex examples have been updated to the native format.

## Required implementation/API differences

- Minecraft/Yarn/Fabric dependencies target 1.21.1; Java 21 and updated Loom/Gradle are required.
- Dispatched bag/content/icon codecs and type-record codec fields use `MapCodec`, as required by current DataFixerUpper. The main polymorphic `Bag.CODEC`, `Content.CODEC` and `Icon.CODEC` remain `Codec`s.
- Vanilla removed arbitrary item-stack NBT and `Item.getRarity`. Bag metadata now uses `NbtComponent`; a narrow `ItemStack.getRarity` mixin delegates only the base rarity lookup for loot bags to their current definition. This preserves dynamic rarity after a data reload and vanilla enchanted-item rarity promotion.
- Status effects use registry entries. Dynamic-registry-aware operations are passed to resource decoding, synchronization and data generation, including enchanted icon stacks.
- Custom loot-pool entries use a `MapCodec` instead of the removed Gson serializer. Their JSON type ID and `bag` field are unchanged; vanilla conditions/functions are now lists instead of arrays.
- Fabric networking uses `CustomPayload` and registered `PacketCodec`s instead of removed `FabricPacket` APIs. The original content → acknowledgement → bag synchronization order is preserved.
- Loot-table lookups use reloadable registries and registry keys. Identifier construction, tooltip methods, item settings and datagen provider signatures follow 1.21.1 APIs.
- Item tint callbacks now supply opaque alpha because 1.21.1 interprets them as ARGB; legacy RGB bag colors do not change.
- Vertex rendering uses the current buffer/shader API, explicitly preserving the old shader’s blend equation and alpha factors. The arrow widget draws the original atlas directly because vanilla removed `setTextureUV`, with the same original blend state so partially transparent resource-pack replacements still work. Background rendering avoids the new default blur/double draw to preserve the original screen appearance.
- The example pack metadata recognizes 1.21.1's data-pack format 48 and resource-pack format 34. Datagen emits current vanilla component syntax for complex items.

## Build and checks

With JDK 21: `./gradlew build`. Regression tests live only under `src/test` and are not shipped in the mod JAR. No GitHub Actions workflow has been added.

Optional example generation: first copy `example/pack.mcmeta` to `run/global_packs/required_data/loot_bag/pack.mcmeta`, then run `./gradlew runDatagen`. This external metadata-copy prerequisite already exists in the master build task; it has been retained rather than refactored. Generated and checked-in complex item definitions both use native 1.21.1 component syntax.

Development client: `./gradlew runClient`. Put the separate `example/` pack into the test world's `datapacks` folder to load example bags; install it separately as a resource pack for example translations. Do not copy QA fixtures into `src/main/resources`.

Automated checks and actual-game acceptance are recorded separately; a successful build alone does not constitute actual-game verification.

## Verification results (2026-10-07)

- Native-format revision: `build` passed with 15 regression tests, zero failures/errors/skips; `runDatagen` generated seven contents, three bags and translations in native 1.21.1 format.
- Production JAR SHA-256 `8998ac26ddc5b3e84da9511a1dccc05989710a2ebb394a2d1edf5e9a45789c5c` was tested in an independent official Minecraft 1.21.1/Fabric Loader 0.16.14 client with Fabric API 0.102.1+1.21.1, without project classes.
- Actual native examples passed: optional enchanted sword icon and Damage 66 reward, all three previews, cropped texture cycling, scaled item icon, names and multiline descriptions.
- Actual gameplay passed: preview cancellation; chosen optional rewards and wrapping; item, status-effect, command and loot-table rewards; normal survival consumption; creative non-consumption; main-hand quick opening and off-hand whole-stack quick opening; deterministic random opening; custom loot-entry generation. Inventory counts were checked in-game.
- Native data reload updated an existing stack's rarity. Saved-world rejoin retained five bags, and opening a preserved bag yielded four bags plus one beef. The world saved and the client exited normally (exit 0).
- Bytecode audit confirms the original shorthand plus native `ItemStack.CODEC` only. No legacy `Count`/`tag` conversion, custom count/empty codec or explicit legacy-rejection branch remains. Boundary tests compare the wrapper with vanilla behavior.
- Existing texture/model/language/license files are unchanged; the complex example is deliberately updated to native components. No bag definitions, tests or QA fixtures are included in the distribution JAR. No CI is configured or added.
- Not independently verified: dedicated-server/multiplayer sessions and audio output. Lethal outcomes in the original random example were not deliberately rolled; deterministic content fixtures and weighted-algorithm unit tests were used.
