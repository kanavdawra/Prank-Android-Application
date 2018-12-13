package com.googli.googletext_to_speechengine

import android.content.Intent
import android.content.BroadcastReceiver
import android.content.Context


class RestartServiceReciver : BroadcastReceiver() {
    override fun onReceive(context: Context, intent: Intent) {
        println("RestartServiceReciver")
        context.startService(Intent(context,NeverEndingService::class.java))
    }
}
