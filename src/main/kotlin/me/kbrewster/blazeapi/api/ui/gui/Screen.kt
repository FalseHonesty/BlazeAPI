package me.kbrewster.blazeapi.api.ui.gui

import me.kbrewster.blazeapi.api.ui.Renderer
import me.kbrewster.blazeapi.api.ui.gui.widget.WidgetButton
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiScreen

@Target(AnnotationTarget.FIELD)
@Retention(AnnotationRetention.RUNTIME)
annotation class InjectWidget

open class Screen : GuiScreen(), Renderer {

    private val buttons = ArrayList<WidgetButton>()

    private var selectedWidgetButton: WidgetButton? = null

    override fun initGui() {
        super.initGui()
        val clazz = this.javaClass
        clazz.declaredFields
                .filter { it.isAnnotationPresent(InjectWidget::class.java) }
                .forEach {field ->
                    field.isAccessible = true
                    val widget = field[this]
                    when (widget) {
                        is WidgetButton -> buttons.add(widget)
                    }
                }
    }

    /**
     * Draws the screen and all the components in it.
     */
    override fun drawScreen(mouseX: Int, mouseY: Int, partialTicks: Float) {
        super.drawScreen(mouseX, mouseY, partialTicks)
        for (i in this.buttons.indices) {
            this.buttons[i].drawButton(this.mc, mouseX, mouseY)
        }
    }

    override fun mouseClicked(mouseX: Int, mouseY: Int, mouseButton: Int) {
        super.mouseClicked(mouseX, mouseY, mouseButton)
        if (mouseButton == 0) {
            buttons.forEach { widget ->
                if (widget.mousePressed(this.mc, mouseX, mouseY)) {
                    this.selectedWidgetButton= widget
                    widget.playPressSound(this.mc.soundHandler)
                    widget.callback.onClick(widget)
                }
            }
        }
    }

    override fun setWorldAndResolution(mc: Minecraft?, width: Int, height: Int) {
        this.mc = mc
        this.itemRender = mc!!.renderItem
        this.fontRendererObj = mc.fontRendererObj
        this.width = width
        this.height = height
        this.buttonList.clear()
        this.buttons.clear()
        this.initGui()
    }

    override fun mouseReleased(mouseX: Int, mouseY: Int, state: Int) {
        super.mouseReleased(mouseX, mouseY, state)
        if (this.selectedWidgetButton != null && state == 0) {
            this.selectedWidgetButton?.mouseReleased(mouseX, mouseY)
            this.selectedWidgetButton = null
        }
    }


}