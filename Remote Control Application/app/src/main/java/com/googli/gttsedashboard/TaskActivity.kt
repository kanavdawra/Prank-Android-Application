package com.googli.gttsedashboard

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.support.v4.content.ContextCompat
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_task.*
import java.util.*

class TaskActivity : AppCompatActivity() {

    var status = ""
    var name = ""
    val allTracks = ArrayList<String>()
    val playAbleTracks = ArrayList<String>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task)
        FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(10)
        setData()
        getTracksList()
        getPlayAbleTracksList()
        setListeners()
        onClickListeners()

    }

    fun setListeners() {

        PlayAbleSongSelecter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TrackName").setValue(allTracks[position])
            }

        }
        AllSongSelecter.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TrackName").setValue(allTracks[position])
            }

        }
        FirebaseDatabase.getInstance().reference.child(name).addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                PlayAbleSongSelecterConfirmation.text = p0!!.child("Task").child("TrackName").value.toString() + "--SeekTo: " + p0!!.child("Task").child("SeekTo").value.toString()
                AllSongSelecterConfirmation.text = p0.child("DownloadProgress").value.toString()
                if(status!=p0.child("Status").value.toString()){
                    status=p0.child("Status").value.toString()
                    Task_ToolBar.background=ContextCompat.getDrawable(this@TaskActivity,R.color.Green)
                }
            }

        })
        SeekTo.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                FirebaseDatabase.getInstance().reference.child(name).child("Task").child("SeekTo").setValue(s.toString())

            }

            override fun afterTextChanged(s: Editable?) {
            }
        })

    }

    fun onClickListeners() {
        Download.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(0)
        }
        Play.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(1)
        }
        Pause_Play.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(2)
        }
        Stop.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(3)
        }
        Restart.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(4)
        }
        SetVolume.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(5)
        }
        Task_Refresh.setOnClickListener {
            val status=this.status
            FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(10)
            Handler().postDelayed({
                if(status==this.status){
                Task_ToolBar.background=ContextCompat.getDrawable(this,R.color.Red)}
                FirebaseDatabase.getInstance().reference.child(name).child("Task").child("TaskID").setValue(100)

            },5000)
        }
    }

    fun setData() {
        val name = intent.extras.getString("Name")
        Task_Name.text = name
        this.name = name
        this.status=intent.extras.getString("Status")
    }

    fun getTracksList() {
        FirebaseDatabase.getInstance().reference.child("Tracks").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                allTracks.add("")
                for (i in p0!!.children) {
                    println(i.key)
                    allTracks.add(i.key)
                }
                allTracksSpinner()
            }
        })
    }

    fun allTracksSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, allTracks)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        AllSongSelecter.adapter = spinnerAdapter

    }

    fun getPlayAbleTracksList() {
        FirebaseDatabase.getInstance().reference.child(intent.extras.getString("Name")).child("Tracks").addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onDataChange(p0: DataSnapshot?) {
                playAbleTracks.clear()
                playAbleTracks.add("")
                for (i in p0!!.children) {
                    println(i.key)
                    playAbleTracks.add(i.key)
                }
                playAbleTracksSpinner()
            }
        })
    }

    fun playAbleTracksSpinner() {
        val spinnerAdapter = ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, allTracks)
        spinnerAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        PlayAbleSongSelecter.adapter = spinnerAdapter
    }

}

