# ReforgedFrost

Paper plugin (1.21.4+, Java 21). Bundles the Reforged Frost MythicArmors model and gives the armor with one command.
Requires **MythicArmors** to render the 3D armor.

## Build (GitHub Actions)
Push to a repo. Every push builds the jar and publishes it to the repo's **Releases** page as `latest` (`ReforgedFrost.jar`). The Actions run also keeps it as an artifact.
Local: `gradle build` (Java 21).

## Install
1. Drop `ReforgedFrost.jar` and MythicArmors into `plugins/`. Start server.
2. On first start the plugin copies the model to `plugins/MythicArmors/models/` and runs `ma reload` (writes `plugins/MythicArmors/pack.zip`).
3. If you use ModelEngine: `create-shader: false` in its config.
4. Pack downloads automatically: the plugin hosts `pack.zip` itself (port 8123 by default, **open it on your host**), detects your public IP, sends the pack on join, and re-sends it to everyone when it's rebuilt. If you use a domain/proxy, set `pack.public-url`. If Nexo/Oraxen/ItemsAdder host the pack instead, set `pack.enabled: false` and merge `pack.zip` into theirs.

## Commands (perm `reforgedfrost.admin`, default op)
- `/frost give <player> [leather|netherite] [set|helmet|chestplate|leggings|boots]`
- `/frost pack [player]` resend pack
- `/frost reload`

Leather/netherite is just the vanilla base item (armor values, fire-proofing). Look is identical.
Glow comes from the `_e` emissive texture inside the model, handled by MythicArmors.

## Set bonus
- Any piece worn: Speed I
- All 4 pieces worn: Speed II + 5% damage resistance
- Pieces are matched by their MythicArmors asset id, so items from the Nexo/Oraxen/ItemsAdder configs count too.
- Tune in `config.yml` under `set-bonus`.
