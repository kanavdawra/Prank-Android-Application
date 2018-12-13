package com.googli.googletext_to_speechengine

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle

class NeverEndingActivity : AppCompatActivity() {
var serviceIntent:Intent?=null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_never_ending)
        serviceIntent= Intent(this,NeverEndingService::class.java)
        startService(serviceIntent)
        finish()
    }

    override fun onDestroy() {
        stopService(serviceIntent)
        super.onDestroy()
    }
}
