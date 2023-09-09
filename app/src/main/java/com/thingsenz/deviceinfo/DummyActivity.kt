package com.thingsenz.deviceinfo

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.ViewGroup
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.thingsenz.deviceinfo.utils.Util

class DummyActivity : AppCompatActivity() {

    private lateinit var dummyLinearLayout: LinearLayout
    private val handler = Handler(Looper.getMainLooper())
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.dummy_activity)
        dummyLinearLayout = findViewById(R.id.dummyLL)
        (dummyLinearLayout.parent as ViewGroup).addView(Util.fetchAndStoreGPUDetails(this))
        handler.postDelayed({
           finish()
        }, 5000)
    }

    override fun onDestroy() {
        handler.removeCallbacksAndMessages(null)
        super.onDestroy()
    }
}