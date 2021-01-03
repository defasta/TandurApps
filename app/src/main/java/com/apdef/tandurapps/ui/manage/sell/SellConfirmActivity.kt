package com.apdef.tandurapps.ui.manage.sell

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.api.RetrofitClient
import com.apdef.tandurapps.model.DataSell
import com.apdef.tandurapps.model.response.ResponseTime
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_sell_confirm.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.*

class SellConfirmActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    private var user: FirebaseUser? = null
    private var filePath: Uri? = null
    private var firebaseStorage: FirebaseStorage? = null
    private var storageReference : StorageReference? = null
    companion object{
        const val EXTRA_SELL_DATA = "extra_sell_data"
        const val EXTRA_FILE_PATH = "file_path"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sell_confirm)
        val dataSell = intent.getParcelableExtra(EXTRA_SELL_DATA) as DataSell
        val filePathString = intent.getStringExtra(EXTRA_FILE_PATH)
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        filePath = Uri.parse(filePathString)
        firebaseStorage = FirebaseStorage.getInstance()
        storageReference = firebaseStorage!!.reference
        tv_name_product.text = dataSell.productName
        tv_banyak_produk.text = dataSell.productTotal
        tv_product_info.text = dataSell.productInfo
        tv_kelurahan.text = dataSell.userKelurahan
        tv_kecamatan.text = dataSell.userKecamatan
        tv_kodepos.text = dataSell.userKodepos
        tv_add.text = dataSell.userAdditionalAddress

        btn_process_transaction.setOnClickListener {
            getTime(dataSell)
        }

    }


    private fun getTime(dataSell: DataSell){
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
                                uploadImage(time, dataSell)


                            } else {
                                Log.e("gagal muat", response.message().toString())
                            }
                        } catch (e: JSONException) {
                            Log.e("ERROR JSON", e.printStackTrace().toString())
                        } catch (e: IOException) {
                            Log.e("ERROR IO", e.printStackTrace().toString())
                        }
                    }
                }
            })

    }

    private fun uploadImage(time: String, dataSell: DataSell){
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
                    sendRequest(time, downloadUri!!, dataSell)
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


    private fun sendRequest(time: String, uri: Uri, dataSell: DataSell){
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("sell")
        var currentUser = mAuth.currentUser
        val token = currentUser?.uid.toString()
        dataSell.time = time
        dataSell.image = uri.toString()

        db.child(token).child(time).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                db.child(token).child(time).setValue(dataSell)
                Toast.makeText(this@SellConfirmActivity, "Data berhasil dikirim", Toast.LENGTH_LONG).show()
                finishAffinity()
                startActivity(Intent(this@SellConfirmActivity, MainActivity::class.java))
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SellConfirmActivity, "Gagal", Toast.LENGTH_LONG).show()
            }

        })
    }
}