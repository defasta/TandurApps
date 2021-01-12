package com.apdef.tandurapps.ui.manage.mypackage

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.api.RetrofitClient
import com.apdef.tandurapps.model.Transaction
import com.apdef.tandurapps.model.response.ResponseTime
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.*
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.btn_change_pic
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.btn_take_camera
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.btn_upload_bukti
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.btn_upload_to_firebase
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.tv_payment_code
import kotlinx.android.synthetic.main.activity_my_package_payment_confirm.tv_total_payment
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.IOException
import java.util.*
import kotlin.collections.HashMap

class MyPackagePaymentConfirmActivity : AppCompatActivity(), PermissionListener {
    companion object{
        const val EXTRA_PAYMENT = "extra_payment"
        const val EXTRA_TOTAL_PRICE = "extra_total_price"
        const val TAKE_IMAGE_REQUEST = 13
        const val PICK_IMAGE_REQUEST = 71
        const val FILE_NAME = "photo.jpg"
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }
    private var filePath: Uri? = null
    private var user: FirebaseUser? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference : StorageReference? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var filePhoto: File

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_package_payment_confirm)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage!!.reference
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        val transaction = intent.getParcelableExtra(EXTRA_PAYMENT)  as Transaction

        tv_payment_code.text = transaction.paymentMethod+"\n"+transaction.paymentMethodCode
        tv_total_payment.text = "Rp. "+transaction.price

        btn_upload_bukti.setOnClickListener {
            launchGallery()
        }

        btn_take_camera.setOnClickListener {
            Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.CAMERA)
                .withListener(this)
                .check()
        }

        btn_upload_to_firebase.setOnClickListener {
            requestRead()
        }

        btn_change_pic.setOnClickListener {
            btn_change_pic.visibility = View.GONE
            btn_upload_to_firebase.visibility = View.GONE
            btn_take_camera.visibility = View.VISIBLE
            btn_upload_bukti.visibility = View.VISIBLE
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val ivPreview: ImageView = this.findViewById(R.id.iv_preview)
        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK&& data!= null && data.data!=null) {
            filePath = data.data
            try {
                val bitmap = MediaStore.Images.Media.getBitmap(contentResolver, filePath)
                ivPreview.setImageBitmap(bitmap)
            } catch (e: IOException) {
                e.printStackTrace()
            }
        }else if(requestCode == TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            filePath = data?.data
            val bitmap = data?.extras?.get("data") as Bitmap
            Glide.with(this)
                .load(bitmap)
                .into(ivPreview)
        }
    }

    override fun onResume() {
        super.onResume()
        if (filePath != null){
            btn_upload_to_firebase.visibility = View.VISIBLE
            btn_upload_bukti.visibility = View.GONE
            btn_take_camera.visibility = View.GONE
            btn_change_pic.visibility = View.VISIBLE
        }
    }

    private fun uploadImage(){
        val userAuth = mAuth.currentUser
        val token  = userAuth?.uid.toString()
        if (filePath !=null){
            val ref = storageReference!!.child("transaction/"+ UUID.randomUUID().toString())

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
                    getTime(downloadUri.toString())

//                    addUploadRecordToDb(downloadUri.toString())
                }else{

                    Toast.makeText(this, "failed task", Toast.LENGTH_SHORT).show()
                }
            }.addOnFailureListener {

                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
            }
        }else{
            Toast.makeText(this, "Mohon upload bukti pembayaran", Toast.LENGTH_LONG).show()
        }
    }

    private fun getTime(uri:String){
        RetrofitClient.instance.getTime("XAEEV5QFFFLL", "json", "zone", "Asia/Jakarta" )
            .enqueue(object : Callback<ResponseTime> {
                override fun onFailure(call: Call<ResponseTime>, t: Throwable) {
                    Log.e("ERROR FAILURE",t.toString())
                }

                override fun onResponse(
                    call: Call<ResponseTime>,
                    response: Response<ResponseTime>
                ) {
                    if (response.isSuccessful) {
                        try {
                            if (response.code() == 200) {
                                val time = response.body()?.formatted.toString()

                                addUploadRecordToDb(time, uri)

                            } else {

                                Log.e("gagal muat", response.message().toString())
                            }
                        } catch (e: JSONException) {

                            Log.e("ERROR JSON", e.printStackTrace().toString())
                        } catch (e: IOException) {

                            Log.e("ERROR IO", e.printStackTrace().toString())
                        }
                    }
                    progressbar_package_confirm.visibility = View.INVISIBLE
                }
            })

    }

    private fun addUploadRecordToDb(time: String, uri: String){
        val userAuth = mAuth.currentUser
        val token  = userAuth?.uid.toString()
        val db = FirebaseDatabase.getInstance().getReference("buktiPembayaran")
        val data = HashMap<String, Any>()
        data["imageUrl"] = uri

        db.child(token).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                db.child(token).child(time).setValue(data)

                Toast.makeText(applicationContext, "Bukti pembayaran telah diupload", Toast.LENGTH_LONG).show()
                startActivity(Intent(this@MyPackagePaymentConfirmActivity, MainActivity::class.java))
            }

            override fun onCancelled(error: DatabaseError) {

                Toast.makeText(applicationContext, "gagal upload ke database", Toast.LENGTH_SHORT).show()
            }

        })

    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent,
                    TAKE_IMAGE_REQUEST
                )
            }
        }
    }

    override fun onPermissionDenied(response: PermissionDeniedResponse?) {
        Toast.makeText(this, "Gagal menambahkan foto", Toast.LENGTH_SHORT).show()
    }

    override fun onPermissionRationaleShouldBeShown(
        permission: PermissionRequest?,
        token: PermissionToken?
    ) {
        TODO("Not yet implemented")
    }

    private fun requestRead(){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }else{
            uploadImage()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                uploadImage()
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}