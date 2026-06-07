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

### Changes Over 3.0.0-alpha.6
- Fixed mixin related crashes
- Fixed bullet interactions not being able to destroy blocks in Fabric
- Removed the accidentally bundled `glass.json` bullet interaction file