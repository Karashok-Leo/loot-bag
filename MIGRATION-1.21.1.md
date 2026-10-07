# Fabric 1.21.1 migration

Baseline: `master` commit `4c968beb39ad9be32305f2662d768f8cef1064fb` (Fabric 1.20.1).

## Preserved behavior and data

- Single, optional and weighted random bags; quick opening, off-hand whole-stack opening, creative consumption rules and reward logic are unchanged.
- All four content types (item, loot table, command, effect), both icon types, custom type registries, translation keys and data directories remain present.
- Bag definitions still use `data/<namespace>/loot-bag/bag/`; content definitions still use `data/<namespace>/loot-bag/content/`.
- The mod contains no built-in bag/content definitions. `example/` remains a separate optional pack.
- Existing textures, item models, language files and license are unchanged. Screen geometry, arrow atlas/UVs, 10-tick scrolling and 40-tick random cycling remain unchanged.
- The existing `BagId` field is retained inside the vanilla `minecraft:custom_data` component. For example: `/give @s loot-bag:loot_bag[minecraft:custom_data={BagId:"loot-bag:optional"}] 1`.
- Legacy item JSON with uppercase `Count` and `tag` is decoded using vanilla's 1.20.1-to-1.21.1 item data fixer with NBT operations, including numeric NBT booleans. This preserves legacy damage, enchantments and custom data in the examples. Simple item identifiers are unchanged. New serialized complex items use vanilla 1.21.1 `count`/`components` syntax. A small equivalent component codec retains the original unrestricted integer counts and empty/air stacks, rather than imposing the new vanilla count limit.

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

Optional example generation: first copy `example/pack.mcmeta` to `run/global_packs/required_data/loot_bag/pack.mcmeta`, then run `./gradlew runDatagen`. This external metadata-copy prerequisite already exists in the master build task; it has been retained rather than refactored. Generated complex item definitions use 1.21.1 component syntax, while the checked-in examples retain the original legacy JSON to demonstrate compatibility.

Development client: `./gradlew runClient`. Put the separate `example/` pack into the test world's `datapacks` folder to load example bags; install it separately as a resource pack for example translations. Do not copy QA fixtures into `src/main/resources`.

Automated checks and actual-game acceptance are recorded separately; a successful build alone does not constitute actual-game verification.

## Verification results (2026-10-07)

- `build`: successful, 16 regression tests, zero failures/errors/skips. Coverage includes all bag choices, weighted/zero-weight behavior, all original example content codecs, legacy enchantments/damage/custom data/numeric booleans, empty/air/large integer counts, component round trips, dynamic rarity with vanilla enchantment promotion, custom loot entry and payload round trips.
- `runDatagen`: successful; seven content definitions, three bag definitions and example translations generated. Generated component-form examples were checked separately; the committed examples retain their original legacy JSON.
- Development client launched and exited normally. An independent production client then loaded the remapped distribution JAR with official Minecraft 1.21.1, Fabric Loader 0.16.14 and Fabric API 0.102.1+1.21.1, without project classes on its classpath.
- Actual gameplay verified: no default bag data; invalid-bag feedback; data reload; single preview, cancel and open; main-hand quick open and off-hand whole-stack opening; optional selection and wrapping; item, effect, command and loot-table rewards; creative non-consumption; custom loot-entry generation; deterministic random opening. Inventory counts were checked in-game.
- After the final compatibility/rendering changes, production JAR SHA-256 `e8de3b1bdade3052ba62352accee9f29b5e93d8ed3cff5a4fd880eec06eace66` was loaded again. Save/rejoin persistence, original enchanted sword icon and Damage 66 reward, tooltips/rarity, cropped texture animation, scaled item icon, multiline descriptions, survival consumption, creative rewards and live rarity changes after `/reload` passed. The client saved and exited normally.
- Existing assets, language files and license were byte-compared against the baseline. No bag definitions, test classes or QA fixtures are included in the distribution JAR. No CI is configured or added.
- Not verified: independent dedicated-server/multiplayer sessions and audio output. Lethal outcomes in the original random example were not deliberately rolled; command/effect/loot-table rewards were exercised with deterministic fixtures, and weighting was unit-tested.
