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

### Changes Over 3.0.0-alpha.11
- Added a new `disableWhileMovingProne` option
- Fixed an issue where bullets stop interacting with anything if TaCZ's Sable compat option is enabled while Sable is present
- Fixed third-person muzzle flashes looking wrong in some cases when the `thirdPersonGunRenderingFix` option is enabled
- Fixed `replace_with` in bullet interactions not working correctly
- Fixed the `constant` and `whizz` sounds not playing when the bullet hits a block or an entity in the same tick