package com.volio.basicopengles

import android.opengl.GLSurfaceView
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val glSurfaceView:GLSurfaceView? = findViewById(R.id.test)
        Log.d("zzet", "onCreate: ${glSurfaceView==null}")
        if (glSurfaceView != null) {
            glSurfaceView.setEGLContextClientVersion(2)
            glSurfaceView.setRenderer(MyRender())
            glSurfaceView.renderMode = GLSurfaceView.RENDERMODE_CONTINUOUSLY
        }
    }
}