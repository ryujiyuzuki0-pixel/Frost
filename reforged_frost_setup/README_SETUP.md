# Reforged Frost - setup for Nexo, ItemsAdder and Oraxen

Everything here sits on top of **MythicArmors** (it renders the 3D armor). The item
configs only point at the armor it generates. Asset ids used everywhere:

    mythicarmor:reforged_frost_armor_head_piece   (helmet, via item_model)
    mythicarmor:reforged_frost_armor_chest_piece  (chestplate, via equippable)
    mythicarmor:reforged_frost_armor_legs_piece   (leggings, via equippable)
    mythicarmor:reforged_frost_armor_feet_piece   (boots, via equippable)

## 0. Load the model (all platforms)
1. Put `MythicArmors/models/reforged_frost_armor.bbmodel` in `plugins/MythicArmors/models/`.
2. Run `/ma reload`. MythicArmors writes the resource pack to `plugins/MythicArmors/pack.zip`.
3. Test without any item plugin: `/ma give <player> reforged_frost_armor chestplate`
4. If you use ModelEngine, set `create-shader: false` in `plugins/ModelEngine/config.yml`.

## Leather vs Netherite
This is only the vanilla item the armor sits on. Leather = vanilla leather armor values
(1/3/2/1 points). Netherite = vanilla netherite values (3/8/6/3 points, toughness,
knockback resistance, fire-proof). The look is identical.

## 1. Nexo  (`Nexo/items/`)
- `reforged_frost_armor_leather.yml` or `reforged_frost_armor_netherite.yml` -> `plugins/Nexo/items/`
- `/nexo reload`
- Your template README suggests `integrations: nexo: enabled: true` and
  `auto-generated-files: false` in the MythicArmors config. The `mythicarmor.zip` that
  shipped in the template only contains the original seven sets, so for the Frost set to
  appear set `auto-generated-files: true` once (or merge `pack.zip` into
  `plugins/Nexo/pack/external_packs/`), then `/nexo reload`.

## 2. ItemsAdder  (`ItemsAdder/contents/reforged_frost/configs/`)
- Copy the leather and/or netherite file into `plugins/ItemsAdder/contents/reforged_frost/configs/`.
- MythicArmors does not hook into ItemsAdder's pack by itself: unzip
  `plugins/MythicArmors/pack.zip` and copy its `assets/` folder into
  `plugins/ItemsAdder/contents/reforged_frost/resourcepack/assets/`, then `/iazip`.
  If a file in `assets/minecraft/shaders/` already exists, merge the two by hand.
- The helmet uses ItemsAdder's native `item_model`. ItemsAdder has no native option for an
  external equipment asset, so chest/legs/boots use the **ItemsAdderAdditions** addon
  (`components:` section). Without it those three pieces will not show the 3D armor.

## 3. Oraxen, 1.21.2+  (`Oraxen/`)
- Merge `settings_snippet.yml` into `plugins/Oraxen/settings.yml` (`CustomArmor: type: COMPONENT`).
- `items/reforged_frost_armor.yml` -> `plugins/Oraxen/items/`
- Put `plugins/MythicArmors/pack.zip` in `plugins/Oraxen/pack/external_packs/` (see Oraxen's
  "Pack Merging" page), then `/oraxen reload all`.
- Base item is LEATHER_*; change to NETHERITE_* in the file for the heavier version.

## Animation / glow
MythicArmors has no keyframe playback for worn armor (its docs list tints, emissive and a
macOS fallback). The two "animations" inside the original models are single-frame poses.
What the plugin does support, and what this set uses, is an **emissive texture**: the model
contains `reforged_frost_armor_e` (every pink highlight: spike tips, the diamond outline,
the face bars, horn edges). Those pixels stay fully bright in the dark. It is not applied to
any face; the plugin finds it by the `_e` suffix. `frost_turntable.gif` is only a preview.

## Not tested in-game
I could not run a server. The Nexo format is copied from the MythicArmors wiki and your
template. Oraxen and ItemsAdder follow their current docs, but ItemsAdder (chest/legs/boots)
is the least certain. If something does not show, the MythicCraft Discord can confirm.
