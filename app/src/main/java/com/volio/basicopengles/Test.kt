package com.volio.basicopengles

import android.content.Context
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.util.Log
import org.jetbrains.skia.*
import org.jetbrains.skia.impl.Managed
import org.jetbrains.skia.paragraph.*
import volio.modul.skia_render.sticker.Sticker
import java.nio.IntBuffer
import java.util.Calendar
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

class Test(context: Context?) : GLSurfaceView(context) {

}

class Render (val contextView: Context): GLSurfaceView.Renderer {
    private val paint = Paint()

    init {
        paint.color = Color.RED
        Managed

    }

    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        gl!!
        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        val bitmap = BitmapFactory.decodeResource(contextView.resources, R.drawable.anh)
        val inputStream = contextView.resources.openRawResource(R.raw.anh)
        val byte = ByteArray(inputStream.available())
        inputStream.read(byte)
        font.setTypeface(Typeface.makeFromFile("/data/data/com.volio.basicopengles/files/test.ttf"))
        image = Image.makeFromEncoded(byte)


    }
    var scW = 1f
    var scH = 1f

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        gl!!
        initCanvas(gl, width, height)
        val matrix2 = Matrix()
//        matrix2.postScale(1f,0.5f)
        matrix.setData(matrix2)
        scW = width.toFloat()
        scH = height.toFloat()
        sticker = Sticker()
        sticker.changeSize(scW,scH)

        Log.d("kkkeket", "onSurfaceChanged: ${Font().measureTextWidth("nnnneeemtaeeee")}")

    }
    private fun getString(floatArray: FloatArray):String{
        var text = "";
        for (i in floatArray){
            text += " $i"
        }
        return text
    }
    fun Matrix33.setData(matrix: Matrix){
        matrix.getValues(mat)
    }
    override fun onDrawFrame(gl: GL10?) {
        gl!!
        gl.glClearColor(0f, 0f, 0f, 0f)
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT)
        val time = System.currentTimeMillis()
        Log.d("zzzetf", "onDrawFrame: ${getString(matrix.mat)}")
//        canvas?.setMatrix(matrix)
//
//        for (i in 0..1000){
//            canvas?.drawCircle(0f+i, 0f+i, 200f,paint)
//
//        }
//        val input = ParagraphBuilder(ParagraphStyle().apply {
//            maxLinesCount = 1
//        }, FontCollection()
//            .setDefaultFontManager(FontMgr.default))
//            .pushStyle(textStyle)
//            .addText("TextInput: adfetetetetet têtt")
//            .addText("\ngggae")
//            .popStyle()
//            .build()
//        input.layout(500f)
//        input.paint(canvas, 500f,  500f)
//        font.size = 100f
        image?.let { canvas?.drawImageRect(it,Rect(0f,500f,1440f,2500f)) }
        canvas?.drawString("đặng phương nam",500f,700f,font,paint)

        canvas?.let {
            sticker.draw(it,scW,scH)
        }
//        path.reset()
//        path.moveTo(100f,100f)
//        path.lineTo(100f,300f)
//        path.lineTo(300f,300f)
//        path.lineTo(300f,100f)
//        canvas?.drawPolygon(floatArrayOf(100f,100f,100f,300f,300f,300f,300f,100f,100f,100f),paint)
//        canvas?.drawPath(path,paint)
        context?.flush()
        Log.d("nnnae", "onDrawFrame: ${System.currentTimeMillis() - time}")
    }
    private var path:Path = Path()
    private var sticker:Sticker = Sticker()
    private var image :Image? = null
    private var font = Font()
    val matrix :Matrix33 = Matrix33.IDENTITY
    val textStyle = TextStyle().setColor(0xFFFFFFFF.toInt()).setFontSize(100f)

    private fun initCanvas(gl: GL10, width: Int, height: Int) {
        val intBuf1 = IntBuffer.allocate(1)
        gl.glGetIntegerv(GLES30.GL_DRAW_FRAMEBUFFER_BINDING, intBuf1)
        val fbId = intBuf1[0]
        val renderTarget = BackendRenderTarget.makeGL(
            width,
            height,
            0,
            8,
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )
        context = DirectContext.makeGL()
        val surface = Surface.makeFromBackendRenderTarget(
            context!!,
            renderTarget,
            SurfaceOrigin.BOTTOM_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB
        )
        canvas = surface!!.canvas

    }

    var context: DirectContext? = null
    var canvas: Canvas? = null

}