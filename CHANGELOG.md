### General
- Added partial compatibility with Arclight
  - It no longer crashes the server, but some features may not work correctly
- Added compatibility with VS Addition Continue
- Added new option values for the `tiltGun` option
- Added new `fireSelectWhileShooting`, `unloadBulletInBarrel`, `manualBolting`, `betterGunTilt`, `disableUnderwater` and `infiniteAmmoDisablesConsumption` options
- Added a new `endless_ammo` status effect that disables the ammo consumption while active
- Added a warning message for when Sound Physics Remastered is required by installed packs but missing from the client
- Suppressed some mixin warnings on dedicated server
- Fixed a minor issue related to the data pack error messages

### Data pack
- Added new `block_break.replace_with` field for block bullet interactions
- The `DistanceSound.sound` field (used in whizz and airspace sounds) now accepts a list
- Added `target`, `blocks` and `entities` fields to each particle and sound definition in bullet particles and bullet sounds