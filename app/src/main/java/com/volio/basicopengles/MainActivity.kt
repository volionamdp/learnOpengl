package com.volio.basicopengles

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Matrix
import android.graphics.Paint
import android.opengl.GLSurfaceView
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper

import android.util.Log
import android.view.ViewGroup.LayoutParams
import org.jetbrains.skiko.SkikoSurfaceView
import java.lang.ref.PhantomReference


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val glSurfaceView:GLSurfaceView? = findViewById(R.id.test)
        Log.d("zzet", "onCreate: ${glSurfaceView==null}")
        val render = MyRenderTexture(this)
        if (glSurfaceView != null) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(render)
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
        glSurfaceView?.setOnTouchListener { v, event ->
            render.onTouch(event)
            true
        }
//        PhantomReference.reachabilityFence(Any())//        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//           val layoutParams =  glSurfaceView?.layoutParams
//            layoutParams?.width = LayoutParams.MATCH_PARENT
//            layoutParams?.height =1000
//            glSurfaceView?.layoutParams = layoutParams
//        },3000)
//        Handler(Looper.getMainLooper()).postDelayed(Runnable {
//            val layoutParams =  glSurfaceView?.layoutParams
//            layoutParams?.width = LayoutParams.MATCH_PARENT
//            layoutParams?.height = LayoutParams.MATCH_PARENT
//            glSurfaceView?.layoutParams = layoutParams
//        },6000)
//        glSurfaceView?.apply {
//            setEGLConfigChooser(8, 8, 8, 0, 24, 8)
//            setEGLContextClientVersion(2)
//            setRenderer(Render(this@MainActivity))
//            setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY)
//        }
        """
            fjie
            êt
        """.trimIndent()
    }


}