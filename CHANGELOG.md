### General
- Added new `disableDesyncCheck` option
  - It potentially fixes the issue with Essential or any other LAN server hosting mod
- Improved bullet hole rendering on VS ships
  - It now follows the movement of the ship
  - It no longer disappear instantly

### Data pack
- Added new bullet particles system
  - Visit the [wiki](https://github.com/MUKSC/TaCZTweaks/wiki/Bullet-Particles) for more details
- Every field with a default value is now strict
  - Previously, it silently fell back to the default value
  - This change is to avoid any unexpected behaviours
- Added new `instant` block break type for convenience
- Fields in bullet sounds now accepts a list