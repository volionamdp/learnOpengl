package volio.modul.skia_render.sticker

import android.graphics.Matrix
import android.graphics.PointF
import android.graphics.RectF
import android.util.Log
import android.util.SizeF
import org.jetbrains.skia.*
import volio.modul.skia_render.checkChange

open class Sticker {
    var screenWidth: Float = 1000f
    var screenHeight: Float = 1000f
    val standardSize = 1000f

    var stickerSize: SizeF = SizeF(200f, 200f)

    // theo phần trăm
    val centerPoint: PointF = PointF(0.7f, 0.3f)
    var rotate: Float = 0f
    var scale: Float = 1f

    // theo pixel
    var stickerRect: RectF = RectF(0f, 0f, 0f, 0f)
    val paintBackground: Paint by lazy {
         Paint()
    }
    var matrix = Matrix()

    init {
        paintBackground.isAntiAlias = true
        paintBackground.color = Color.RED
    }

    private fun updateRect() {
        val rectWidth: Float
        val rectHeight: Float
        val centerX = centerPoint.x * screenWidth
        val centerY = centerPoint.y * screenHeight
        if (screenWidth < screenHeight) {
            rectWidth = screenWidth * stickerSize.width / standardSize
            rectHeight = screenWidth * stickerSize.height / standardSize
        } else {
            rectWidth = screenHeight * stickerSize.width / standardSize
            rectHeight = screenHeight * stickerSize.height / standardSize
        }
        if (stickerRect.checkChange(centerX, centerY, rectWidth, rectHeight)) {
            stickerRect.set(
                centerX - rectWidth / 2,
                centerY - rectHeight / 2,
                centerX + rectWidth / 2,
                centerY + rectHeight / 2
            )
        }
        Log.d("vvvet", "updateRect: ${stickerRect.toShortString()}")
    }

    open fun changeSize(width: Float, height: Float) {
        screenWidth = width
        screenHeight = height
        updateRect()
    }

    open fun draw(canvas: Canvas, width: Float, height: Float) {
        val save = canvas.save()
        test(canvas, width, height)
        canvas.restoreToCount(save)

    }

    private var path: Path = Path()
    private fun test(canvas: Canvas, width: Float, height: Float) {
//        canvas.drawCircle(stickerRect.left,stickerRect.top,200f,paintBackground)

        path.reset()
        path.moveTo(stickerRect.left, stickerRect.top)
        path.lineTo(stickerRect.left, stickerRect.bottom)
        path.lineTo(stickerRect.right, stickerRect.bottom)
        path.lineTo(stickerRect.right, stickerRect.top)
        path.closePath()
        Log.d("vvet2", "test: ${stickerRect.toShortString()}")

//        canvas.drawPath(path, paintBackground)
        canvas.drawRect(
            Rect.makeXYWH(
                stickerRect.left,
                stickerRect.top,
                stickerRect.width(),
                stickerRect.height()
            ), paintBackground
        )
    }

    fun scale(scale: Float, centerX: Float, centerY: Float) {
        matrix.postScale(scale, scale, centerX, centerY)
    }

    fun rotate(deg: Float, centerX: Float, centerY: Float) {
        matrix.postRotate(deg, centerX, centerY)
    }

    fun translate(x: Float, y: Float) {
        val percentX = x / screenWidth
        val percentY = y / screenHeight
        centerPoint.x += percentX
        centerPoint.y += percentY
        updateRect()
    }
}