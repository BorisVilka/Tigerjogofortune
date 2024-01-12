package com.fortkto.gametigerone

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.WindowManager

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        window.addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN)
        SoundsManager.createInstance(this)
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onResume() {
        SoundsManager.getInstance().start()
        super.onResume()
    }

    override fun onPause() {
        SoundsManager.getInstance().pause()
        super.onPause()
    }

    override fun onDestroy() {
        SoundsManager.getInstance().destroy()
        super.onDestroy()
    }
}