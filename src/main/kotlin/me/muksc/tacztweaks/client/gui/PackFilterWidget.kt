package me.muksc.tacztweaks.client.gui

import com.tacz.guns.client.resource.pojo.PackInfo
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.narration.NarrationElementOutput
import net.minecraft.network.chat.Component
import java.util.function.BiConsumer
import kotlin.math.ceil

class PackFilterWidget(
    val packs: Map<String, PackInfo>,
    val font: Font,
    x: Int, y: Int, width: Int, height: Int
) : AbstractWidget(x, y, width, height, Component.empty()) {
    private var indexOffset = 0
    private val list = packs.toList()
    private val selected = packs.keys.associate { it to false }.toMutableMap()
    var onFilterChanged: BiConsumer<String, Boolean>? = null

    fun shouldFilter(): Boolean = selected.values.any { it }

    fun include(id: String): Boolean = selected[id]!!

    override fun mouseScrolled(mouseX: Double, mouseY: Double, delta: Double): Boolean {
        if (!isMouseOver(mouseX, mouseY)) return false
        indexOffset += if (delta < 0) 1 else -1
        return true
    }

    override fun mouseClicked(mouseX: Double, mouseY: Double, button: Int): Boolean {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button)
        val minX = (PADDING + x).toDouble()
        val maxX = (x + width - PADDING).toDouble()
        val minY = (PADDING + y + ROW_HEIGHT).toDouble()
        val maxY = (y + height - PADDING).toDouble()
        if (mouseX !in minX..maxX || mouseY !in minY..maxY) return false

        val index = ceil((mouseY - minY) / ROW_HEIGHT).toInt() - 1 + indexOffset
        if (index !in 0..list.lastIndex) return false
        val id = list[index].first
        val filter = !selected[id]!!
        selected[id] = filter
        onFilterChanged?.accept(id, filter)
        return true
    }

    override fun renderWidget(graphics: GuiGraphics, mouseX: Int, mouseY: Int, partialTick: Float) {
        graphics.fill(x, y, x + width, y + height, -267386864)
        graphics.drawString(font, Component.translatable("tacztweaks.packs.filter"), x + PADDING, y + PADDING, -1)
        graphics.enableScissor(PADDING + x, PADDING + y + ROW_HEIGHT, x + width - PADDING, y + height - PADDING)
        for ((index, pair) in list.withIndex()) {
            val selected = selected[pair.first]!!
            val pack = pair.second
            val offset = PADDING + ROW_HEIGHT * (index + 1 - indexOffset)
            if (selected) graphics.fill(PADDING + x, PADDING + y + offset, x + width - PADDING, y + offset + ROW_HEIGHT + PADDING / 2, -265079235)
            graphics.drawString(font, Component.translatable(pack.name), PADDING + x, PADDING + y + offset, 14737632)
        }
        graphics.disableScissor()
    }

    override fun updateWidgetNarration(narrationElementOutput: NarrationElementOutput) { /* I'm lazy */}

    companion object {
        private const val PADDING = 2
        private const val ROW_HEIGHT = 10
    }
}