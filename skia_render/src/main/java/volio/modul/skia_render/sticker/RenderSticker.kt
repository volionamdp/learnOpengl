package volio.modul.skia_render.sticker

import android.util.SizeF
import android.view.MotionEvent
import org.jetbrains.skia.Canvas
import volio.modul.skia_render.RenderLayer

class RenderSticker : RenderLayer() {
    private var listSticker: MutableList<Sticker> = mutableListOf()
    init {
        listSticker.add(Sticker())
    }
    override fun changeSize(width: Float, height: Float) {
        listSticker.forEach { it.changeSize(width, height) }
    }

    override fun draw(canvas: Canvas, width: Float, height: Float) {
        listSticker.forEach {
            it.draw(canvas, width, height)
        }
    }
    var lastX:Float =0f
    var lasty = 0f
    override fun onTouch(event: MotionEvent): Boolean {
        when(event.action){
            MotionEvent.ACTION_DOWN->{
                lastX = event.x
                lasty = event.y
            }
            MotionEvent.ACTION_MOVE->{
                listSticker.forEach {
                    it.translate(event.x-lastX,event.y-lasty)
                }
                lastX = event.x
                lasty = event.y
            }
        }
        return true
    }
}