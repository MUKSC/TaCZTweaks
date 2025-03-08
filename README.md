## TaCZ Tweaks
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/tacz-tweaks)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/tacz-tweaks)
[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/MUKSC/TaCZTweaks)

TaCZ Tweaks is an addon for the [Timeless and Classics Zero](https://modrinth.com/mod/timeless-and-classics-zero) mod that adds random tweaks and features to enhance my experience with TaCZ.  
**Requires [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge) and [YACL](https://modrinth.com/mod/yacl).**

If you have any questions, you can reach me on the [TaCZ Official Discord](https://discord.gg/uX6TdWUVpA) in the [#community-showcase > TaCZ Tweaks](https://discord.com/channels/1243278348399022252/1313570204000980992) channel.  
Alternatively, you can use the [GitHub Discussions](https://github.com/MUKSC/TaCZTweaks/discussions) page or the [issues](https://github.com/MUKSC/TaCZTweaks/issues) page in general.

## Example Pack
You can download the example pack from the download page on Modrinth or GitHub, but not available on CurseForge due to limitations.  
You can install it by putting the .zip file in the `.minecraft/tacz` directory, just like gun packs.  
It contains:
- Glass block piercing
- Drip stone breaking
- Whizzing sound
- Metal hitting sound

## Features
### Customizable Advanced Piercing
Customizable via data packs.  
Visit the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki/Bullet-Interactions) for more details.  
![Piercing glass blocks](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/piercing.webp)
![Conditional piercing](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/conditional-piercing.webp)

### Customizable Bullet Sounds
Customizable via data packs.  
You can add various sounds to bullets, such as:
- Whizzing
- Block hit/pierce/break
- Entity hit/pierce/kill

Visit the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki/Bullet-Sounds) for more details.

### Mod Compatibilities
FirstAid / Legendary Survival Overhaul compat:
- Allows bullets to damage correct body parts on hit.

Valkyrien Skies compat:
- Collision compat allows bullets to properly collide with VS ships.
- Explosion compat allows explosive bullets to damage and knockback VS ships.

Valkyrien Skies compat is disabled by default as it modifies the existing behaviours.

### Shoot / Reload While Sprinting
Allows you to shoot and reload while sprinting.  
Additionally, you can immediately start reloading after a shot.  
![Shoot and reload while sprinting](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/sprint-shoot-reload.webp) 

### Crawl Tweaks
- Ability to completely disable the TaCZ crawl.
  - This allows crawling feature from other mods to be functional.
- Customizable upper and lower pitch limit.
- Dynamic pitch limit based on block collisions.
- Minor visual tweak.

### Gun Unloading
The new key bind is located under the TaCZ category.  
Can be disabled on server side.

### Search & Filter in the Gun Smith Table
- Show only compatible attachments by interacting while holding a gun
- New search bar and pack filter
- Craft items without cost in creative mode

![Filtering in the Gun Smith Table](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/gun-smith-table-filter.webp)

### Disable Bullet Culling
Improves the accuracy of bullet rendering at close range.  
![Improved close-range bullet rendering](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/bullet-culling.webp)

## Migrate to Bullet Interactions V2
There has been a rework to the bullet interactions in v2.0.0.  
Although you can still use the old format, here's how you can migrate to the new format.

### What Changed
- New `type` field, along with the new customizable entity piercing.
- The `guns` field has been replaced by the new and more expressive `target` field.
- The `pierce.condition` field has been simplified and replaced by the new `conditional` field.
- The `pierce.require_gun_pierce` field has been replaced by the new `gunPierce` field.
- The `drop` field has moved to the new `block_break.drop` field.

### Instruction
Add new `type` field.  
```json5
{
  "type": "block",
  ...
}
```
Add new `target` field and place the content of the `guns` field in there.
```json5
{
  ...,
  "guns": [
    ... // Copy these
  ],
  "target": {
    "type": "gun",
    "value": [
      ... // Paste in here
    ],
  },
  ...
}
```
Change the `pierce.condition` field to the new `pierce.conditional` field.
```json5
{
  ...,
  "pierce": {
    ...,
    "condition": "always",
    // Change the above to the below
    "conditional": false,
    
    "condition": "on_break",
    // Change the above to the below
    "conditional": true,
    ...
  },
  ...
}
```
Change the `pierce.require_gun_pierce` field to the new `gunPierce` field.
```json5
{
  ...,
  "pierce": {
    ...,
    "require_gun_pierce": ... // Copy this
  },
  "gunPierce": {
    "required": ..., // Paste in here
    "consume": false
  }
}
```
Move the `drop` field to the new `block_break.drop` field.
```json5
{
  ...,
  "block_break": {
    ...,
    "drop": ... // Paste here
  },
  ...,
  "drop": ... // Copy this
}
```

### Example
Before:
```json5
{
  "blocks": [
    "minecraft:stone",
    "#forge:glass"
  ],
  "guns": [
    "tacz:m320"
  ],
  "block_break": {
    "type": "dynamic_damage",
    "modifier": 0,
    "multiplier": 1.0,
    "accumulate": true
  },
  "pierce": {
    "type": "damage",
    "condition": "on_break",
    "damage_falloff": 5,
    "damage_multiplier": 1.0,
    "require_gun_pierce": false
  },
  "drop": false
}
```
After:
```json5
{
  "type": "block",
  "target": {
    "type": "gun",
    "values": [
      "tacz:m320"
    ]
  },
  "blocks": [
    "minecraft:stone",
    "#forge:glass"
  ],
  "block_break": {
    "type": "dynamic_damage",
    "modifier": 0,
    "multiplier": 1.0,
    "accumulate": true,
    "drop": false
  },
  "pierce": {
    "type": "damage",
    "conditional": true,
    "damage_falloff": 5,
    "damage_multiplier": 1.0
  },
  "gun_pierce": {
    "required": false,
    "consume": false
  }
}
```