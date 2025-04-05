package com.thingsenz.devinfo.ui

import android.annotation.SuppressLint
import android.content.Intent
import android.opengl.GLES30
import android.opengl.GLSurfaceView
import android.os.Bundle
import android.view.SurfaceView
import androidx.activity.ComponentActivity
import androidx.appcompat.app.AppCompatActivity
import com.thingsenz.devinfo.MainActivity
import com.thingsenz.devinfo.R
import com.thingsenz.devinfo.utils.Prefs
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10

@SuppressLint("CustomSplashScreen")
class SplashActivity: ComponentActivity(), GLSurfaceView.Renderer {

    private lateinit var glSurfaceView: GLSurfaceView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)
        if (!Prefs.isFirstRun(this)) {
            gotoHome()
        } else {
            glSurfaceView = findViewById(R.id.glSurfaceView)
            glSurfaceView.setRenderer(this)
        }
    }

    private fun gotoHome() {
        startActivity(Intent(this, MainActivity::class.java).apply {
            addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        })
        finish()
    }

    override fun onDrawFrame(p0: GL10?) {

    }

    override fun onSurfaceChanged(p0: GL10?, p1: Int, p2: Int) {

    }

    override fun onSurfaceCreated(p0: GL10?, p1: EGLConfig?) {
        try {
            Prefs.setGPURenderer(this,p0?.glGetString(GL10.GL_RENDERER) ?: "")
            Prefs.setGPUVersion(this,p0?.glGetString(GL10.GL_VERSION) ?: "")
            p0?.glGetString(GL10.GL_VENDOR)?.let {
                if (it.isEmpty()) {
                    Prefs.setGPUVendor(this, GLES30.glGetString(GLES30.GL_VENDOR) ?: "")
                }
            }
        } catch (e: Exception) {

        } finally {
            Prefs.setFirstRun(this,false)
            gotoHome()
        }
    }

}