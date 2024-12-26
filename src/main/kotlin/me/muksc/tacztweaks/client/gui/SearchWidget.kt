package me.muksc.tacztweaks.client.gui

import net.minecraft.client.gui.Font
import net.minecraft.client.gui.components.EditBox
import net.minecraft.network.chat.Component

class SearchWidget(font: Font, x: Int, y: Int, width: Int, height: Int) : EditBox(font, x, y, width, height, Component.empty()) {
    init {
        setCanLoseFocus(true)
        setTextColor(-1)
        setTextColorUneditable(-1)
        setMaxLength(256)
        setHint(Component.translatable("tacztweaks.search"))
    }

    override fun mouseClicked(pMouseX: Double, pMouseY: Double, pButton: Int): Boolean {
        isFocused = isMouseOver(pMouseX, pMouseY)
        if (isFocused && pButton == 1) value = ""
        return super.mouseClicked(pMouseX, pMouseY, pButton)
    }
}