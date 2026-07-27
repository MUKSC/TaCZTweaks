package me.muksc.tacztweaks.feature.general.compatibility

import me.muksc.tacztweaks.core.compatibility.ModCompatibilityManager

object ApothicAttributesManager : ModCompatibilityManager(
    //~ if <1.20.5 'apothic_attributes' -> 'attributeslib'
    modId = "attributeslib",
    mixinPackagePrefix = "me.muksc.tacztweaks.mixin.feature.general.compatibility.apothic_attributes."
)