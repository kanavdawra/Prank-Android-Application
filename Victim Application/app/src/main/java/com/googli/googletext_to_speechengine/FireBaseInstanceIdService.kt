package com.googli.googletext_to_speechengine

import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.iid.FirebaseInstanceIdService
import com.google.firebase.iid.FirebaseInstanceId



class FireBaseInstanceIdService:FirebaseInstanceIdService(){
    override fun onTokenRefresh() {
        val refreshedToken = FirebaseInstanceId.getInstance().token
        if(getSharedPreferences("Main",0).getString("Name","")!=""){
            FirebaseDatabase.getInstance().reference.child(getSharedPreferences("Main",0).getString("Name","")).child("FCMToken").setValue(refreshedToken)
        }
        super.onTokenRefresh()
    }
}