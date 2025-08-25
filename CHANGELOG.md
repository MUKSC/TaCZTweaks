### General
- Added `playerHeadshot` damage modifier config option
- Added `alwaysFirstPersonShootingSound` config option which forces the gun shooting sounds to always use the first person variant
- Added `betterMonoConversion` config option which improves the stereo to mono conversion, mainly to improve the experience when using the above two config options
- Added `reloadDiscardsMagazine` config option which makes guns discard the rest of the magazine when reloading early
- Added `cancelInspection` config option which allows you to cancel the gun inspection animation
- Added `debug` config options which enables the debug logging for each data pack feat
- In the config screen for the modifier config, replaced number sliders with number input fields for more precise control
- Players can no longer unload guns if they have infinite ammo to prevent unnecessary ammo duplication

### Data pack
Please refer to the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki) for the full documentation.

- Added `silenced` and `random_chance` target types
- Added melee interactions
- Added `airspace` bullet sounds type
  - Only works if you have the sound physics mod installed
  - Triggers on each gun fire, and allows you to conditionally play sounds based on the surrounding environment, calculated by the sound physics mod
- Added `range` field to the sound declaration to control the distance that the sound will be broadcasted to other players
  - Previously it was capped at relatively low value, making other players not be able to hear sounds from far away
- Added `duration` field to the particle declaration to control the number of ticks which the particle will be emitted 
- The `sound` field in the distance sound declaration can now be null