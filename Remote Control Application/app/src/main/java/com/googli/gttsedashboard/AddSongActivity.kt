package com.googli.gttsedashboard

import android.annotation.TargetApi
import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import com.nononsenseapps.filepicker.FilePickerActivity
import android.content.Intent
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.support.annotation.RequiresApi
import android.view.LayoutInflater
import android.view.View
import android.widget.EdgeEffect
import android.widget.EditText
import com.nononsenseapps.filepicker.Utils
import android.widget.Toast
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_add_song.*
import java.io.File

@TargetApi(24)
class AddSongActivity : AppCompatActivity() {
    private var fileCode = 16
    lateinit var file: File
    lateinit var name: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_song)
        clickListeners()
    }

    private fun songPicker() {
        val i = Intent(this, FilePickerActivity::class.java)
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_MULTIPLE, false)
        i.putExtra(FilePickerActivity.EXTRA_ALLOW_CREATE_DIR, false)
        i.putExtra(FilePickerActivity.EXTRA_MODE, FilePickerActivity.MODE_FILE)
        i.putExtra(FilePickerActivity.EXTRA_START_PATH, Environment.getExternalStorageDirectory().path)

        startActivityForResult(i, fileCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent) {
        if (requestCode == fileCode && resultCode == Activity.RESULT_OK) {
            val files = Utils.getSelectedFilesFromResult(intent)
            for (uri in files) {
                val file = Utils.getFileForUri(uri)
                this.file = file
                SongName.text = file.toURI().toString()
            }
        }
    }

    private fun clickListeners() {
        Upload.setOnClickListener {
            nameDialog()
        }
        Select.setOnClickListener {
            songPicker()
        }
    }

    fun nameDialog() {
        val dialogView = this.layoutInflater.inflate(R.layout.sound_name_layout, null)
        AlertDialog.Builder(this).setView(dialogView)
                .setPositiveButton("Upload") { dialog, which ->
                    name = dialogView.findViewById<EditText>(R.id.soundName).text.toString()
                    upload(name)
                }
                .setNegativeButton("Cancel") { dialog, which ->

                }.show()

    }

    fun upload(name: String) {
        Upload.visibility = View.GONE
        UploadProgressBar.visibility = View.VISIBLE
        val storageRef = FirebaseStorage.getInstance().reference
        val file = Uri.fromFile(file)
        val soundRef = storageRef.child(name)
        val uploadTask = soundRef.putFile(file).addOnProgressListener {
            val progress = (it.bytesTransferred / it.totalByteCount) * 100
            Progress.text = progress.toString()
        }
        uploadTask.addOnFailureListener({
            Toast.makeText(this, "UploadFailed", Toast.LENGTH_LONG).show()
            Upload.visibility = View.VISIBLE
            UploadProgressBar.visibility = View.GONE
        }).addOnSuccessListener({
            Toast.makeText(this, "Upload Success", Toast.LENGTH_LONG).show()
            finish()
            Upload.visibility = View.VISIBLE
            UploadProgressBar.visibility = View.GONE
        })
    }


}
