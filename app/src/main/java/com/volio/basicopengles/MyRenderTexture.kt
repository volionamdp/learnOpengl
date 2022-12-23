package com.volio.basicopengles

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.opengl.*
import android.util.Log
import android.view.MotionEvent
import org.jetbrains.skia.*
import volio.modul.skia_render.RenderLayer
import volio.modul.skia_render.SkiaRender
import volio.modul.skia_render.sticker.RenderSticker
import volio.modul.skia_render.sticker.Sticker
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import java.nio.IntBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "MyRender"

class MyRenderTexture(val context: Context) : GLSurfaceView.Renderer {
    private val vertexData = floatArrayOf(
        -1f, -1f, 0f, 1f,
        -1f, 1f, 0f, 0f,
        1f, -1f, 1f, 1f,
        1f, 1f, 1f, 0f,
    )

    val vertexShader = """attribute vec4 a_Position;
                           attribute vec4 a_TextureCoordinate;
                           varying vec2 textureCoordinate;
                           void main()
                           {
                              gl_Position = a_Position;
                              textureCoordinate = a_TextureCoordinate.xy;
                           }
                           """

    val fragmentShader = """precision mediump float;
                            varying vec2 textureCoordinate;
                            uniform sampler2D texture;   
                            void main()
                            {
                               gl_FragColor = texture2D(texture,textureCoordinate);
                            }
                            """

    private var triangleVertices: FloatBuffer
    private val FLOAT_BYTE_SIZE = 4

    private var program: Int = 0
    private var aPosition: Int = 0
    private var aTextureCoordinate: Int = 0

    private var bitmap: Bitmap
    private var textureId: Int = NO_TEXTURE
    private var framebufferObject:GlFramebufferObject = GlFramebufferObject()
    private var skiaRender = SkiaRender()
    init {
        // Initialize the buffers.
        /// "allocateDirect" dùng bộ nhớ trực tiếp (trên os) thay vì dùng gián tiếp trên bộ nhớ máy ảo như "allocate"
        triangleVertices = ByteBuffer.allocateDirect(vertexData.size * FLOAT_BYTE_SIZE)
            //cách thức đọc ghi bộ nhớ ByteOrder.BIG_ENDIAN thứ tự a->z || ByteOrder.LITTLE_ENDIAN ngược lại z->a
            .order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer()
        triangleVertices.put(vertexData)?.position(0)
        bitmap = BitmapFactory.decodeResource(context.resources, R.drawable.anh)
    }

    fun loadTexture(img: Bitmap, usedTexId: Int, recycle: Boolean): Int {
        val textures = IntArray(1)
        if (usedTexId == NO_TEXTURE) {
            GLES20.glGenTextures(1, textures, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textures[0])
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER, GLES20.GL_LINEAR.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLES20.glTexParameterf(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T, GLES20.GL_CLAMP_TO_EDGE.toFloat()
            )
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0)
        } else {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, usedTexId)
            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, img, 0)
            textures[0] = usedTexId
        }
        if (recycle) {
            img.recycle()
        }
        return textures[0]
    }

    // type ở đây gồm 2 loại là GLES20.GL_VERTEX_SHADER và GLES20.GL_FRAGMENT_SHADER tương ứng với
    // vertex shader và fragment shader trong biểu đồ, codeStr là mã code shader hướng dẫn ở bài đầu
    private fun loadShader(type: Int, codeStr: String): Int {
        //Tạo một shader mới theo type và trả về liên kết với shader
        val shader = GLES20.glCreateShader(type)
        if (shader > 0) {
            // Set codeStr vào vào shader đã được tạo
            GLES20.glShaderSource(shader, codeStr)
            //Biên dịch bộ đổ bóng
            GLES20.glCompileShader(shader)


            // kiểm tra lỗi
            val status = IntArray(1)
            GLES20.glGetShaderiv(shader, GLES20.GL_COMPILE_STATUS, status, 0)
            Log.i(TAG, "loadShader: status[0]=" + status[0])
            if (status[0] == 0) {
                //loại bỏ shader
                GLES20.glDeleteShader(shader)
                return 0
            }
        }
        return shader
    }

    fun loadProgram(verCode: String?, fragmentCode: String?): Int {
        //1. Tạo một chương trình xử lý Shader và trả liên kết chương trình
        val programId = GLES20.glCreateProgram()
        if (programId == 0) {
            Log.e(TAG, "loadProgram: glCreateProgram error")
            return 0
        }
        //2. Đính kèm shader vào chương trình
        GLES20.glAttachShader(programId, loadShader(GLES20.GL_VERTEX_SHADER, verCode!!))
        GLES20.glAttachShader(programId, loadShader(GLES20.GL_FRAGMENT_SHADER, fragmentCode!!))
        //3. Liên kết
        GLES20.glLinkProgram(programId)
        //4. Sử dụng
        GLES20.glUseProgram(programId)
        return programId
    }

    init {
//        var paint = Paint()
//        paint.isAntiAlias = true
//        var font = Font()
//        font.size = 50f
//        paint.color = Color.YELLOW
//        skiaRender.addLayer(object : RenderLayer() {
//            override fun changeSize(width: Float, height: Float) {
//                Log.d("nameeee", "changeSize: ")
//
//            }
//
//            override fun draw(canvas: Canvas, width: Float, height: Float) {
//                Log.d("nameeee", "draw: ")
//                canvas.drawCircle(300f,300f,200f,paint)
//                canvas.drawRect(Rect.makeXYWH(500f,500f,300f,300f),paint)
//                canvas.drawString("đăng kí",500f,900f,font,paint)
//            }
//
//        })
        skiaRender.addLayer(RenderSticker())
    }
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        program = loadProgram(vertexShader, fragmentShader)
        aPosition = GLES20.glGetAttribLocation(program, "a_Position")
        aTextureCoordinate = GLES20.glGetAttribLocation(program, "a_TextureCoordinate")
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)
//        framebufferObject.setup(width, height)
        skiaRender.changeSize(width.toFloat(), height.toFloat())
//        if (gl != null&&!isInitFrameBuffer) {
//            initCanvas(gl,width, height)
//        }
    }
    private fun drawTexture(idTexture:Int,isFrameBuffer:Boolean){
        if (isFrameBuffer){
            framebufferObject.enable()
        }else{
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
        GLES20.glUseProgram(program)

        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        GLES20.glClearColor(0f, 0f, 0f, 0f)
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT or GLES20.GL_DEPTH_BUFFER_BIT)
        triangleVertices.position(0)
        GLES20.glVertexAttribPointer(
            aPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * FLOAT_BYTE_SIZE,
            triangleVertices
        )
        GLES20.glEnableVertexAttribArray(aPosition)


        triangleVertices.position(2)
        GLES20.glVertexAttribPointer(
            aTextureCoordinate,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * FLOAT_BYTE_SIZE,
            triangleVertices
        )
        GLES20.glEnableVertexAttribArray(aTextureCoordinate)
//
//
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,idTexture)
        GLES20.glUniform1i(aTextureCoordinate, 0)
//
//
//
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
//
//
//
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aTextureCoordinate)
    }

    private fun drawTexture2(idTexture:Int,isFrameBuffer:Boolean){
        if (isFrameBuffer){
            framebufferObject.enable()
        }else{
            GLES20.glBindFramebuffer(GLES20.GL_FRAMEBUFFER, 0)
        }
        GLES20.glUseProgram(program)

        GLES20.glClearColor(0f, 1f, 0f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA);
        GLES20.glEnable(GLES20.GL_BLEND);
        triangleVertices.position(0)
        GLES20.glVertexAttribPointer(
            aPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * FLOAT_BYTE_SIZE,
            triangleVertices
        )
        GLES20.glEnableVertexAttribArray(aPosition)


        triangleVertices.position(2)
        GLES20.glVertexAttribPointer(
            aTextureCoordinate,
            2,
            GLES20.GL_FLOAT,
            false,
            4 * FLOAT_BYTE_SIZE,
            triangleVertices
        )
        GLES20.glEnableVertexAttribArray(aTextureCoordinate)
//
//
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,idTexture)
        GLES20.glUniform1i(aTextureCoordinate, 0)
//
//
//
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
//
//
//
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aTextureCoordinate)
        GLES20.glBindBuffer( GLES20.GL_ARRAY_BUFFER, 0);
        GLES20.glBindBuffer( GLES20.GL_ELEMENT_ARRAY_BUFFER, 0);
    }
    private var isInitFrameBuffer:Boolean = true
    override fun onDrawFrame(gl: GL10?) {
//        if (textureId == NO_TEXTURE){
//            textureId = loadTexture(bitmap,textureId,false)
//        }
//        drawTexture2(textureId,false)

//        framebufferObject.enable()
//        drawCanvas()
//        drawTexture(textureId,true)
        val time = System.currentTimeMillis()
        GLES20.glUseProgram(0)
        skiaRender.draw()

//        Log.d(TAG, "onDrawFrame: ${System.currentTimeMillis() - time}")
//        drawTexture(framebufferObject.texName,false)
        drawTexture(skiaRender.getTextureId(),false)

//        GLES20.glClearColor(0f, 0f, 0f, 1f)
//        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)
//
//        triangleVertices.position(0)
//        GLES20.glVertexAttribPointer(
//            aPosition,
//            2,
//            GLES20.GL_FLOAT,
//            false,
//            4 * FLOAT_BYTE_SIZE,
//            triangleVertices
//        )
//        GLES20.glEnableVertexAttribArray(aPosition)
//
//
//        triangleVertices.position(2)
//        GLES20.glVertexAttribPointer(
//            aTextureCoordinate,
//            2,
//            GLES20.GL_FLOAT,
//            false,
//            4 * FLOAT_BYTE_SIZE,
//            triangleVertices
//        )
//        GLES20.glEnableVertexAttribArray(aTextureCoordinate)
////
////
//        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
//        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D,textureId)
//        GLES20.glUniform1i(aTextureCoordinate, 0)
////
////
////
//        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
////
////
////
//        GLES20.glDisableVertexAttribArray(aPosition)
//        GLES20.glDisableVertexAttribArray(aTextureCoordinate)
//        drawCanvas()

    }
    val paint = Paint()
    val path = Path().apply {
        moveTo(200f,900f)
        lineTo(200f,1300f)
        lineTo(600f,1300f)
        lineTo(600f,900f)
//        close()
    }
    private val  pictureRecorder: PictureRecorder by lazy {
        PictureRecorder()
    }
    private var pickHolder:Picture? = null
    var count = 0
    fun drawCanvas(){
//        val bounds = Rect.makeWH(framebufferObject.width.toFloat(), framebufferObject.height.toFloat())
//        val canvas = pictureRecorder.beginRecording(bounds)
        canvas?.saveLayer(0f,0f, framebufferObject.width.toFloat(),
            framebufferObject.height.toFloat(),null)
        paint.color = Color.RED
        canvas?.clear(-1)
//        canvas?.drawRect(Rect.makeXYWH(500f,500f,300f,300f),paint)
        if (count>100) {
            canvas?.drawCircle(500f, 500f, 200f, paint)
        }
        count++
        canvas?.drawRect(Rect.makeXYWH(500f,500f,300f,300f),paint)
        canvas?.drawRect(Rect.makeXYWH(200f,200f,300f,300f),paint)
        canvas?.drawRect(Rect.makeXYWH(900f,200f,300f,300f),paint)
        canvas?.drawPath(path,paint)
//        canvas?.drawRectShadow(Rect.makeXYWH(0f,0f,1f,1f),0f,0f,0f,Color.TRANSPARENT)
//        canvas?.drawCircle(200f,500f,200f,paint)
//        pickHolder?.close()
//        pickHolder = pictureRecorder.finishRecordingAsPicture()
//        pickHolder?.let { canvas.drawPicture(it) }
        canvas?.restore()
        context2?.flush()

    }
    private fun initCanvas(gl: GL10, width: Int, height: Int) {
//        framebufferObject.enable()

        var fbId = 0
        if (isInitFrameBuffer){
            framebufferObject.enable()
            fbId = framebufferObject.framebufferName
        }else {
            val intBuf1 = IntBuffer.allocate(1)
            gl.glGetIntegerv(GLES30.GL_DRAW_FRAMEBUFFER_BINDING, intBuf1)
             fbId = intBuf1[0]
        }
        Log.d(TAG, "initCanvas: ${fbId}")

        val renderTarget = BackendRenderTarget.makeGL(
            width,
            height,
            0,
            8,
            fbId,
            FramebufferFormat.GR_GL_RGBA8
        )
        context2 = DirectContext.makeGL()
        val surface = Surface.makeFromBackendRenderTarget(
            context2!!,
            renderTarget,
            SurfaceOrigin.TOP_LEFT,
            SurfaceColorFormat.RGBA_8888,
            ColorSpace.sRGB
        )
        canvas = surface!!.canvas
    }

    fun onTouch(event: MotionEvent?) {
        event?.let {
            skiaRender.onTouch(it)
        }
    }

    var context2: DirectContext? = null
    var canvas: org.jetbrains.skia.Canvas? = null

    companion object {
        const val NO_TEXTURE = -1
    }
}