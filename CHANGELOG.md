## TaCZ Tweaks V3 Alpha
Status: Feature parity with V2; feature incomplete for V3  
It's marked as alpha because it's still feature incomplete, not because it's unstable per se  
The config file will be migrated automatically; this version should serve as a drop-in replacement

### Summary of Changes
- New logo
- Complete rewrite
- Multi-loader, multi-version support (`1.20.1-forge`, `1.21.1-neoforge`, `1.20.1-fabric` and `1.21.1-fabric`)
- Reorganized config screen and config structure
- New attributes system (modify damage, disable guns, etc.)
- New status effect (disarm)
- New command (`/tacztweaks refill_ammo [<targets>]`)
- New options (Sable/Aeronautics compat, Cuffed compat, bolt key, etc.)

### Planned Features
- New overhauled and much more capable data pack system
- More attributes (suggestions welcome!)
- More commands (suggestions welcome!)

### Changes Over 3.0.0-alpha.10
- TaCZ 1.2.0
  - Removed the unload key and pitch lower/upper limit option as they're now implemented in the base mod
- Added new weight modifier config options
- Added a new `suppressorsHideDeathMessages` option
- Added a new option for Apothic Attributes compatibility
- Valkyrien Skies and Sable compats are now enabled by default
- Fixed `constant` and `whizz` bullet sounds not working at all
- Fixed some datapack features acting weirdly with Sable active
- Fixed incompatibility with Sound Physics Remastered 1.5.2
- Fixed config synchronization not working correctly on LAN servers