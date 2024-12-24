## TaCZ Tweaks
TaCZ Tweaks is an addon for the [Timeless and Classics Zero](https://modrinth.com/mod/timeless-and-classics-zero) mod that adds random tweaks and features to enhance my experience with TaCZ.  
**Requires [Kotlin for Forge](https://modrinth.com/mod/kotlin-for-forge).**

## Features
- Bullets can now break and/or pierce blocks. Fully customizable via data packs.
- Options to allow: shoot while sprinting, sprint while reloading, reload while shooting.
- Options to change upper and lower pitch limit while crawling.
  - Additionally, an option to change pitch limits based on the hitbox of the blocks.
- Recipes in the Gun Smith Table will be filtered according to the gun you're currently holding  
  - Creative players can now craft items in the Gun Smith Table without requiring items
- A new key config to unloading the gun.
- A small visual tweak to the animation when transitioning into or out of the crawling state.
- Option to completely disable TaCZ crawling for better mod compatibility.
  
## Customize Bullet Interactions
Low effort documentation on how to customize bullet interactions using data packs.

Each bullet interactions is a json file located in `bullet_interactions` directory.  
(e.g. `data/example/bullet_interactions/example_interaction.json`)  
The data structure is as follows:
```json
{
  // A list of block IDs or block tags to which this interaction applies
  "blocks": [
    "minecraft:stone",
    "#forge:glass"
  ],
  // A list of gun IDs to which this interaction applies
  "guns": [
    "tacz:m320"
  ],
  // Specifies how blocks break
  "block_break": {
    // The "never" type will never break the block
    "type": "never",

    // The "count" type will break the block after it's been hit a certain number of times
    "type": "count",
    "count": 2,

    // The "fixed_damage" type will apply a fixed amount of damage to the block
    // If "accumulate" is false, the only way to break the block is if the bullet can one-shot it
    // The block's hardness and the gun's armor-piercing stat will be applied
    "type": "fixed_damage",
    "damage": 5,
    "accumulate": true,

    // The "dynamic_damage" type will apply a variable amount of damage to the block, based on the bullet's damage
    // The "modifier" and "multiplier" properties will modify the damage against the block by a certain amount
    // If "accumulate" is false, the only way to break the block is if the bullet can one-shot it
    // The block's hardness and the gun's armor-piercing stat will be applied
    "type": "dynamic_damage",
    "modifier": 0,
    "multiplier": 1.0,
    "accumulate": true
  },
  // Specifies the conditions for when a bullet will pierces through a block
  "pierce": {
    // The "never" type will never pierces the block
    "type": "never",

    // The "count" type will pierces the bullet a fixed number of times
    // The "condition" property defines additional conditions to determine whether to pierce the block
    // The "damage_falloff" and "damage_multiplier" properties will modify the bullet's damage by a certain amount
    // If "require_gun_pierce" is true, the gun's pierce stat will be applied
    "type": "count",
    "condition": "always / on_break",
    "count": 3,
    "damage_falloff": 5,
    "damage_multiplier": 1.0,
    "require_gun_pierce": false,

    // The "damage" type will pierces the bullet as long as it has a damage of more than zero
    // The "condition" property defines additional conditions to determine whether to pierce the block
    // The "damage_falloff" and "damage_multiplier" properties will modify the bullet's damage by a certain amount
    // If "require_gun_pierce" is true, the gun's pierce stat will be applied
    "type": "damage",
    "condition": "always / on_break",
    "damage_falloff": 5,
    "damage_multiplier": 1.0,
    "require_gun_pierce": false
  },
  // Determines whether the block drops as an item when broken
  "drop": false
}
```