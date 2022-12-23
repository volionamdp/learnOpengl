package volio.modul.skia_render

import android.graphics.RectF
import org.jetbrains.skia.Rect

fun Rect.checkChange(centerX: Float, centerY: Float, width: Float, height: Float): Boolean {
    return (this.left + this.width / 2f) != centerX ||
            (this.top + this.height / 2f) != centerY ||
            (this.width) != width ||
            (this.height) != height
}
fun RectF.checkChange(centerX: Float, centerY: Float, width: Float, height: Float): Boolean {
    return (this.left + this.width() / 2f) != centerX ||
            (this.top + this.height() / 2f) != centerY ||
            (this.width()) != width ||
            (this.height()) != height
}