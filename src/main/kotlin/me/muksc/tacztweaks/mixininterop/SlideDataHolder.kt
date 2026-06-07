package me.muksc.tacztweaks.mixininterop

import me.muksc.tacztweaks.mixininterface.feature.synced_slide.SlideDataHolder

inline var SlideDataHolder.shouldSlide: Boolean
    get() = `tacztweaks$getShouldSlide`()
    set(value) = `tacztweaks$setShouldSlide`(value)