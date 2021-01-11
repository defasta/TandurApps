package com.apdef.tandurapps.ui.profile

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.User
import com.apdef.tandurapps.storage.SharedPref
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_change_profile_picture.*
import java.io.IOException
import java.util.*

class ChangeProfilePicture : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private var user: FirebaseUser? = null
    private var filePath: Uri? = null
    private lateinit var pref : SharedPref
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference : StorageReference? = null

    companion object{
        const val PICK_IMAGE_REQUEST = 71
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_profile_picture)

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage!!.reference
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        pref = SharedPref(this)

        val ivProfile = pref.getValues("imageProfileUrl")
        if (ivProfile != ""){
            Glide.with(this)
                .load(ivProfile)
                .into(iv_profile)
        }else{
            iv_profile.setImageResource(R.drawable.ic_baseline_person_24)
        }


        btn_upload_bukti.setOnClickListener {
            launchGallery()
        }

        btn_save.setOnClickListener {
            uploadImage()
        }

        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun launchGallery(){
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
           PICK_IMAGE_REQUEST
        )
    }

    override fun onResume() {
        super.onResume()
        if (filePath!= null){
            btn_save.visibility = View.VISIBLE
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivProfile: ImageView = this.findViewById(R.id.iv_profile)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data!= null && data.data!=null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivProfile.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }
    }

    private fun uploadImage(){
        progressbar_change_profile.visibility = View.VISIBLE
        val userAuth = mAuth.currentUser
        val token  = userAuth?.uid.toString()
        if (filePath !=null){
            val ref = storageReference!!.child("user/"+ UUID.randomUUID().toString())

            val uploadTask = ref.putFile(filePath!!)
            val urlTask= uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
                if (!task.isSuccessful) {
                    task.exception?.let {
                        throw it
                    }
                }
                return@Continuation ref.downloadUrl
            }).addOnCompleteListener { task ->
                if (task.isSuccessful){
                    val downloadUri = task.result
                    progressbar_change_profile.visibility = View.INVISIBLE
                    addUploadRecordToDb(downloadUri.toString())
                }else{
                    progressbar_change_profile.visibility = View.INVISIBLE
                    Toast.makeText(this, "failed task", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {
                progressbar_change_profile.visibility = View.INVISIBLE
                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        }else{
            progressbar_change_profile.visibility = View.INVISIBLE
            Toast.makeText(this, "Mohon pilih foto", Toast.LENGTH_LONG).show()
        }
    }

    private fun addUploadRecordToDb(uri: String){
        val userAuth = mAuth.currentUser
        val token  = userAuth?.uid.toString()
        val db = FirebaseDatabase.getInstance().getReference("user")
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri


        db.child(token).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                db.child(token).setValue(data)
                val user = snapshot.getValue(User::class.java)
                if (user!= null){
                    pref.setValues("email", user.email.toString())
                    pref.setValues("kecamatan", user.kecamatan.toString())
                    pref.setValues("kelurahan", user.kelurahan.toString())
                    pref.setValues("kodepos", user.kodepos.toString())
                    pref.setValues("additionalAddress", user.additionalAddress.toString())
                    pref.setValues("imageProfileUrl", user.imageUrl.toString())
                    pref.setValues("username", user.username.toString())
                }

                Toast.makeText(this@ChangeProfilePicture, "Foto Profil telah disimpan", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(this@ChangeProfilePicture, "gagal upload ke database", Toast.LENGTH_SHORT).show()
            }

        })

    }
} 