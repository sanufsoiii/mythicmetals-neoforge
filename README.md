# Mythic Metals: Unofficial NeoForge Port

Unofficial port of **[Mythic Metals](https://github.com/Noaaan/MythicMetals)** by **Noaaan** (Fabric 1.21.1, version 0.24.7) to **NeoForge 1.21.1**.

## Status: early alpha

The port builds and the client starts to the main menu without errors. **Gameplay has not been tested in a world yet**: expect bugs, and please report them (see below). Dedicated server has not been tested either.

## Requirements

- Minecraft 1.21.1, NeoForge 21.1.200 or newer
- [owo-lib](https://modrinth.com/mod/owo-lib) for NeoForge
- [Alloy Forgery NeoForge 1.21.1 port](https://github.com/sanufsoiii/alloy-forgery-neoforge)

Additional Entity Attributes (NeoForge build) is bundled. Integrations with mods that have no NeoForge 1.21.1 version (Origins, Explosive Enhancement, Cicada, Isometric Renders) and recipe viewer plugins are inactive.

## Getting textures, models and sounds

All Mythic Metals assets are **All Rights Reserved** by their artists (see `CREDITS-ORIGINAL.md`). They are **not included** in this port.

1. Download the original **Mythic Metals 0.24.6** for **Fabric 1.21.1** yourself: [CurseForge](https://www.curseforge.com/minecraft/mc-mods/mythicmetals/files) or [Modrinth](https://modrinth.com/mod/mythicmetals/version/0.24.6+1.21).
2. Put the downloaded `.jar` into your `resourcepacks` folder. **Not** into `mods`.

On startup the port finds the jar and loads **only its client assets** (textures, models, sounds, translations) as a built-in resource pack. Nothing is downloaded by the mod itself. Without the jar the game works, but items and blocks are purple-black, and a one-time notice on the title screen explains what to do.

## Building

Requires JDK 21.

The build expects the Alloy Forgery port in your local Maven repository. Clone [alloy-forgery-neoforge](https://github.com/sanufsoiii/alloy-forgery-neoforge) and run `./gradlew publishToMavenLocal` there first.

```bash
./gradlew build
```

The mod jar is in `build/libs/`.

## Reporting bugs

This is an **unofficial** port. Please **do not** report its bugs to Noaaan or on the original issue tracker. Use this repository instead: https://github.com/sanufsoiii/mythicmetals-neoforge/issues

## License and credits

- Mythic Metals was created by **Noaaan**: https://github.com/Noaaan/MythicMetals. All gameplay design, art, sounds and names belong to them.
- This port is not affiliated with or endorsed by the original author.
- Code: **MIT**, same as the original code (see `LICENSE` and `CREDITS-ORIGINAL.md`).
- Folder `_excluded/` keeps original Fabric-only code (data generators, integrations with mods that have no NeoForge 1.21.1 version) for reference; it is not compiled.
