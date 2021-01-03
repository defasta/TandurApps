package com.apdef.tandurapps.ui.manage.sell

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
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Spinner
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.marginBottom
import com.apdef.tandurapps.R
import com.apdef.tandurapps.api.RetrofitClient
import com.apdef.tandurapps.model.DataSell
import com.apdef.tandurapps.model.Satuan
import com.apdef.tandurapps.model.response.ResponseTime
import com.apdef.tandurapps.storage.SharedPref
import com.apdef.tandurapps.ui.home.PlantingPaymentConfirmActivity
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
import kotlinx.android.synthetic.main.activity_sell_detail.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*
import kotlin.collections.ArrayList

class SellProductDetailActivity : AppCompatActivity(), PermissionListener {
    companion object{
        const val TAKE_IMAGE_REQUEST = 13
        const val PICK_IMAGE_REQUEST = 71
        const val MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 1
    }

    private var user: FirebaseUser? = null
    private var filePath: Uri? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference : StorageReference? = null
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var name: String
    private lateinit var total: String
    private lateinit var satuanChosen: String
    private lateinit var info: String
    private lateinit var pref : SharedPref
    private var listSatuan = ArrayList<Satuan>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_detail)

        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage!!.reference
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser

        pref = SharedPref(this)
        showSatuan(listSatuan)
        btn_process.setOnClickListener {
            if(et_name.text.toString().equals("")){
                et_name.error = "Kolom tidak boleh kosong!"
                et_name.requestFocus()
            }else if (et_satuan.text.toString().equals("")){
                et_satuan.error = "Kolom tidak boleh kosong!"
                et_satuan.requestFocus()
            }else{
                name = et_name.text.toString()
                total= et_satuan.text.toString()
                info = et_add_info.text.toString()
                requestRead(name, total, info)
            }
        }

        btn_add_pic.setOnClickListener {
            Dexter.withActivity(this)
                .withPermission(android.Manifest.permission.CAMERA)
                .withListener(this)
                .check()
        }
    }

    override fun onResume() {
        super.onResume()
        if(filePath!= null){
            btn_process.visibility = View.VISIBLE
            btn_add_pic.text = "Ganti Foto"
        }
    }
    private fun showSatuan(satuan: ArrayList<Satuan>){
        val satuanString = ArrayList<String?>()
        db = FirebaseDatabase.getInstance().getReference("satuan")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(satuanSnapshot in snapshot.children){
                    val satuanFromFirebase = satuanSnapshot.getValue(Satuan::class.java)
                    satuan.add(satuanFromFirebase!!)
                }
                satuan.forEach {
                    val sp: Spinner = findViewById(R.id.spinner_satuan)
                    satuanString.add(it.name)

                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, satuanString)
                    sp.adapter = adapter
                    sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            satuanChosen = satuan[position].name
                        }

                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("Not yet implemented")
                        }

                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "failed", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onPermissionGranted(response: PermissionGrantedResponse?) {
        Intent(MediaStore.ACTION_IMAGE_CAPTURE).also { takePictureIntent ->
            takePictureIntent.resolveActivity(packageManager)?.also {
                startActivityForResult(takePictureIntent, TAKE_IMAGE_REQUEST)
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == TAKE_IMAGE_REQUEST && resultCode == Activity.RESULT_OK){
            filePath = data?.data
            val bitmap = data?.extras?.get("data") as Bitmap
            Glide.with(this)
                .load(bitmap)
                .into(iv_product)
        }
    }

    private fun requestRead(name: String, total: String, info: String){
        if(ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE),
                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE
            )
        }else{
            processRequest(name, total, info)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE){
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED){
                processRequest(name, total, info)
            }else{
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show();
            }
            return
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

//    private fun uploadImage(name: String, total: String, info: String){
//        val userAuth = mAuth.currentUser
//        val token  = userAuth?.uid.toString()
//        if (filePath !=null){
//            val ref = storageReference!!.child("transaction/"+ UUID.randomUUID().toString())
//
//            val uploadTask = ref.putFile(filePath!!)
//            val urlTask= uploadTask.continueWithTask(Continuation<UploadTask.TaskSnapshot, Task<Uri>> { task ->
//                if (!task.isSuccessful) {
//                    task.exception?.let {
//                        throw it
//                    }
//                }
//                return@Continuation ref.downloadUrl
//            }).addOnCompleteListener { task ->
//                if (task.isSuccessful){
//                    val downloadUri = task.result
//                    processRequest(name, total, info)
////                    addUploadRecordToDb(downloadUri.toString())
//                }else{
//                    Toast.makeText(this, "failed task", Toast.LENGTH_SHORT).show()
//                }
//            }.addOnFailureListener {
//                Toast.makeText(this, "failed", Toast.LENGTH_SHORT).show()
//            }
//        }else{
//            Toast.makeText(this, "Mohon upload bukti pembayaran", Toast.LENGTH_LONG).show()
//        }
//    }


    private fun processRequest(name: String, total: String, info:String){
        db = FirebaseDatabase.getInstance().getReference("satuan")
        val dataSell = DataSell()
        dataSell.productName = name
        dataSell.productTotal = total+" "+satuanChosen
        dataSell.productInfo = info
        dataSell.userName = pref.getValues("username").toString()
        dataSell.userKecamatan = pref.getValues("kecamatan").toString()
        dataSell.userKelurahan = pref.getValues("kelurahan").toString()
        dataSell.userKodepos = pref.getValues("kodepos").toString()
        dataSell.userAdditionalAddress = pref.getValues("additionalAddress").toString()
        val i =Intent(this, SellConfirmActivity::class.java)
        i.putExtra(SellConfirmActivity.EXTRA_SELL_DATA, dataSell)
        i.putExtra(SellConfirmActivity.EXTRA_FILE_PATH, filePath.toString())
        startActivity(i)
    }
}
