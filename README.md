## TaCZ Tweaks
[![Modrinth](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/modrinth_vector.svg)](https://modrinth.com/mod/tacz-tweaks)
[![CurseForge](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/curseforge_vector.svg)](https://www.curseforge.com/minecraft/mc-mods/tacz-tweaks)
[![GitHub](https://cdn.jsdelivr.net/npm/@intergrav/devins-badges@3/assets/cozy/available/github_vector.svg)](https://github.com/MUKSC/TaCZTweaks)

TaCZ Tweaks is an addon for the [Timeless and Classics Zero](https://modrinth.com/mod/timeless-and-classics-zero) mod that adds random tweaks and features to enhance my experience with TaCZ.  
**Requires [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge) and [YACL](https://modrinth.com/mod/yacl).**

Many features are customizable via the in-game config screen, which can be accessed from the mod list.

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

### Customizable Bullet Particles
Customizable via data packs.  
Visit the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki/Bullet-Particles) for more details.

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
- Disabled by default as it modifies the existing behaviours.

Immersive Vehicles compat:
- Fixes bullet collision on vehicle entities.

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

### Balancing Modifiers
You can tweak various parameters of all the guns at once.  
![Balancing modifiers](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/balancing-modifiers.webp)

### Disable Bullet Culling
Improves the accuracy of bullet rendering at close range.  
![Improved close-range bullet rendering](https://raw.githubusercontent.com/MUKSC/TaCZTweaks/refs/heads/main/assets/bullet-culling.webp)

### Gameplay Tweaks
- Ability to disable certain sounds: headshot sounds, bodyshot sounds and kill sounds
- Ability to hide hit markers

## License
TaCZ Tweaks' code is licensed under the [GPLv3](https://github.com/MUKSC/TaCZTweaks/blob/main/LICENSE).