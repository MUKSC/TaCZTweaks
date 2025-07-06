### General
- Revamped the config screen structure
- Added new balancing modifiers options
- Added new `endermenEvadesBullets` option
- Fixed the broken bullet hole rendering on VS ships
- Fixed adventure mode players cannot break blocks though bullet interactions

### Data pack
- Added new `target` types, as well as new `blocks` and `entities` types, which allows more advanced customization
  - Visit the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki/Common-Types) for more detail
- `target` field now accepts a list
- Added new `priority` field
- Added new `constant` bullet sounds type
- Deprecated "block_break.tier" and "block_break.hardness", use "tier" and "hardness" target types instead
  - It's not removed, but I don't recommend using it anymore