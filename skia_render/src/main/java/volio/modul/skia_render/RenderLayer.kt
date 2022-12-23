package volio.modul.skia_render

import android.view.MotionEvent
import org.jetbrains.skia.Canvas

abstract class RenderLayer {
    abstract fun changeSize(width: Float, height: Float)
    abstract fun draw(canvas: Canvas, width: Float, height: Float)
    open fun onTouch(event: MotionEvent): Boolean {
        return false
    }
}