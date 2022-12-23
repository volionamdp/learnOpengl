package volio.modul.skia_render

import android.view.MotionEvent
import org.jetbrains.skia.*


class SkiaRender {
    private val mOffScreenFrameBuffer = IntArray(1)
    private val textureId = IntArray(1)
    private var directContext: DirectContext? = null
    private var canvas: Canvas? = null
    private var rectScreen = Rect(0f, 0f, 0f, 0f)
    private var listLayer: MutableList<RenderLayer> = mutableListOf()
    private var glFramebufferObject = GlFramebufferObject()
    private var rectBoundDraw = Rect.makeWH(1f,1f)
    fun changeSize(width: Float, height: Float) {
        rectBoundDraw = Rect.makeWH(width,height)
        if (textureId[0] <= 0 || rectScreen.width != width || rectScreen.height != height) {
            glFramebufferObject.setup(width.toInt(), height.toInt())
            initCanvas(width.toInt(), height.toInt())

            rectScreen = Rect.makeWH(width, height)
            listLayer.forEach {
                it.changeSize(width, height)
            }
        }
    }
    private val pictureRecorder: PictureRecorder by lazy {
        PictureRecorder()
    }
    private var pickHolder:Picture? = null
    fun draw() {
        val canvasRecorder = pictureRecorder.beginRecording(rectBoundDraw)
        listLayer.forEach {
            it.draw(canvasRecorder, rectScreen.width, rectScreen.height)
        }
        pickHolder?.close()
        val picture = pictureRecorder.finishRecordingAsPicture()
        pickHolder = picture
        canvas?.clear(Color.TRANSPARENT)
        canvas?.drawPicture(picture)
        directContext?.flush()
        directContext?.resetGLAll()
    }
    fun getTextureId():Int{
        return glFramebufferObject.texName
    }
    fun addLayer(renderLayer: RenderLayer){
        listLayer.add(renderLayer)
    }
    fun removeLayer(renderLayer: RenderLayer){
        listLayer.remove(renderLayer)
    }
    fun setListLayer(list:List<RenderLayer>){
        listLayer.clear()
        listLayer.addAll(list)
    }
    fun clearLayer(){
        listLayer.clear()
    }

    fun release() {
        glFramebufferObject.release()
    }



    private fun initCanvas(width: Int, height: Int) {
        glFramebufferObject.enable()
        val renderTarget = BackendRenderTarget.makeGL(
            width,
            height,
            0,
            8,
            glFramebufferObject.framebufferName,
            FramebufferFormat.GR_GL_RGBA8
        )
        directContext = DirectContext.makeGL()
        val surface = Surface.makeFromBackendRenderTarget(
            directContext!!,
            renderTarget,
            SurfaceOrigin.TOP_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB
        )
        canvas = surface!!.canvas
    }

    fun onTouch(event: MotionEvent) {
        listLayer.forEach {
            it.onTouch(event)
        }
    }


}