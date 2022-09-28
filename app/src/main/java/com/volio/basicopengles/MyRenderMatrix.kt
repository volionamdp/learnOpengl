package com.volio.basicopengles

import android.opengl.GLES20
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.opengl.Matrix
import android.util.Log
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

private const val TAG = "MyRender"

class MyRenderMatrix : GLSurfaceView.Renderer {
    private val vertexData = floatArrayOf(
        -0.5f, -0.5f, 1f, 0f, 0f,
        -0.5f, 0.5f, 0f, 1f, 0f,
        0.5f, -0.5f, 1f, 0f, 1f,
        0.5f, 0.5f, 0f, 0f, 1f,
    )

    val vertexShader = """attribute vec4 a_Position;
                           attribute vec3 a_color;
                           varying mediump vec3 color;
                           uniform mat4 u_MVPMatrix;      

                           void main()
                           {
                              gl_Position = u_MVPMatrix*a_Position;
                              color = a_color;
                           }
                           """

    val fragmentShader = """precision mediump float;
                            varying mediump vec3 color;
                            void main()
                            {
                               gl_FragColor = vec4(color,1.);
                            }
                            """

    private var triangleVertices: FloatBuffer
    private val FLOAT_BYTE_SIZE = 4
    private var program: Int = 0
    private var aPosition: Int = 0
    private var aColor: Int = 0
    private var uMvp: Int = 0

    private var modelMatrix: FloatArray = FloatArray(16)
    private var viewMatrix: FloatArray = FloatArray(16)
    private var projectMatrix: FloatArray = FloatArray(16)
    private var mvpMatrix: FloatArray = FloatArray(16)

    init {
        // Initialize the buffers.
        /// "allocateDirect" dùng bộ nhớ trực tiếp (trên os) thay vì dùng gián tiếp trên bộ nhớ máy ảo như "allocate"
        triangleVertices = ByteBuffer.allocateDirect(vertexData.size * FLOAT_BYTE_SIZE)
            //cách thức đọc ghi bộ nhớ ByteOrder.BIG_ENDIAN thứ tự a->z || ByteOrder.LITTLE_ENDIAN ngược lại z->a
            .order(
                ByteOrder.nativeOrder()
            ).asFloatBuffer()
        triangleVertices.put(vertexData)?.position(0)
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


    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        program = loadProgram(vertexShader, fragmentShader)
        aPosition = GLES20.glGetAttribLocation(program, "a_Position")
        aColor = GLES20.glGetAttribLocation(program, "a_color")
        uMvp = GLES20.glGetUniformLocation(program,"u_MVPMatrix")
        Matrix.setLookAtM(viewMatrix, 0, 0f, 0f, 1.5f, 0f, 0f, 0f, 0f, -1f, 0f)
    }

    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        GLES20.glViewport(0, 0, width, height)

        // Create a new perspective projection matrix. The height will stay the same
        // while the width will vary as per aspect ratio.
        val ratio = width.toFloat() / height
        val left = -ratio
        val bottom = -1.0f
        val top = 1.0f
        val near = 1.0f
        val far = 10.0f
        Matrix.frustumM(projectMatrix, 0, left, ratio, bottom, top, near, far)
    }

    private var rotate = 0f
    private fun updateMatrix() {
        rotate+=0.3f
        Log.d(TAG, "updateMatrix: $rotate")

        // Draw the triangle facing straight on.
        Matrix.setIdentityM(modelMatrix, 0)
        Matrix.translateM(modelMatrix,0,0f,0f,-2f)
        Matrix.rotateM(modelMatrix,0,rotate%360,1f,1f,1f)

        Matrix.multiplyMM(mvpMatrix, 0, viewMatrix, 0, modelMatrix, 0)
        Matrix.multiplyMM(mvpMatrix, 0, projectMatrix, 0, mvpMatrix, 0)
    }

    override fun onDrawFrame(gl: GL10?) {
        updateMatrix()
        GLES20.glClearColor(0f, 0f, 0f, 1f)
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT)

        triangleVertices.position(0)

        GLES20.glEnableVertexAttribArray(aPosition)
        GLES20.glVertexAttribPointer(
            aPosition,
            2,
            GLES20.GL_FLOAT,
            false,
            5 * FLOAT_BYTE_SIZE,
            triangleVertices
        )

        triangleVertices.position(2)
        GLES20.glEnableVertexAttribArray(aColor)
        GLES20.glVertexAttribPointer(
            aColor,
            3,
            GLES20.GL_FLOAT,
            false,
            5 * FLOAT_BYTE_SIZE,
            triangleVertices
        )
        GLES20.glUniformMatrix4fv(uMvp,1,false,mvpMatrix,0)
        GLES20.glDrawArrays(GLES20.GL_TRIANGLE_STRIP, 0, 4)
        GLES20.glDisableVertexAttribArray(aPosition)
        GLES20.glDisableVertexAttribArray(aColor)

    }
}