package com.apdef.tandurapps.ui.home

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.apdef.tandurapps.R
import com.apdef.tandurapps.api.RetrofitClient
import com.apdef.tandurapps.model.PaymentMethod
import com.apdef.tandurapps.model.PlantingPackage
import com.apdef.tandurapps.model.response.ResponseTime
import com.apdef.tandurapps.storage.SharedPref
import com.apdef.tandurapps.ui.profile.ChangeAddressActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_planting_payment.*
import org.json.JSONException
import retrofit2.Call
import retrofit2.Callback
import android.widget.ArrayAdapter
import kotlinx.android.synthetic.main.activity_price.*
import retrofit2.Response
import java.io.IOException
import kotlin.collections.ArrayList

class PlantingPaymentActivity : AppCompatActivity() {
    private lateinit var pref : SharedPref
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var paymentMethodChoosen: String
    private lateinit var paymentMethodCodeChoosen: String
    private var listPaymentMethod = ArrayList<PaymentMethod>()
    private var choosenListPaymentMethod = ArrayList<PaymentMethod>()
    companion object{
        const val EXTRA_PLANTING_PACKAGE = "extra_planting_package"
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_planting_payment)
        pref = SharedPref(this)
        val plantingData = intent.getParcelableExtra(PlantingDetailActivity.EXTRA_PLANTING_PACKAGE) as PlantingPackage
        tv_kecamatan.text = pref.getValues("kecamatan")
        tv_kelurahan.text = pref.getValues("kelurahan")
        tv_kodepos.text = pref.getValues("kodepos")
        tv_add.text = pref.getValues("additionalAddress")
        tv_price.text = "Rp. "+plantingData.price.toString()
        showPaymentMethod(listPaymentMethod)
//        btnChangeAddress.setOnClickListener {
//            startActivity(Intent(this, ChangeAddressActivity::class.java))
//        }
        btn_buy.setOnClickListener {
            getTime(plantingData)
        }

        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onResume() {
        super.onResume()
        tv_kecamatan.text = pref.getValues("kecamatan")
        tv_kelurahan.text = pref.getValues("kelurahan")
        tv_kodepos.text = pref.getValues("kodepos")
        tv_add.text = pref.getValues("additionalAddress")

    }

    private fun showPaymentMethod(data: ArrayList<PaymentMethod>) {
       val paymentString = ArrayList<String?>()
        db = FirebaseDatabase.getInstance().getReference("paymentMethod")
        db.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for(paymentMethodSnapshot in snapshot.children){
                    val paymentMethodFromFirebase = paymentMethodSnapshot.getValue(PaymentMethod::class.java)
                    data.add(paymentMethodFromFirebase!!)
                }
                data.forEach {
                    val sp: Spinner = findViewById(R.id.spinner_payment)
                    paymentString.add(it.name)

                    val adapter = ArrayAdapter(applicationContext, android.R.layout.simple_list_item_1, paymentString)
                    sp.adapter = adapter
                    sp.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
                        override fun onNothingSelected(parent: AdapterView<*>?) {
                            TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
                        }

                        override fun onItemSelected(
                            parent: AdapterView<*>?,
                            view: View?,
                            position: Int,
                            id: Long
                        ) {
                            choosenListPaymentMethod.add(data[position])
                            paymentMethodChoosen = data[position].name
                            paymentMethodCodeChoosen = data[position].code
                        }

                    }
                }

            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "failed", Toast.LENGTH_LONG).show()
            }

        })


    }
    private fun getTime(plantingData : PlantingPackage){
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
                                sendBuyRequest(time, plantingData)

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

    private fun sendBuyRequest(time: String, plantingData : PlantingPackage){
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("transaction")
        var currentUser = mAuth.currentUser
        val token = currentUser?.uid.toString()


        db.child(token).addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                db.child(token).child(time).child("name").setValue(plantingData.name)
                db.child(token).child(time).child("detail").setValue(plantingData.detail)
                db.child(token).child(time).child("education").setValue(plantingData.education)
                db.child(token).child(time).child("images").setValue(plantingData.images)
                db.child(token).child(time).child("info").setValue(plantingData.info)
                db.child(token).child(time).child("price").setValue(plantingData.price.toString())
                db.child(token).child(time).child("status").setValue("0")
                db.child(token).child(time).child("addressKecamatan").setValue(pref.getValues("kecamatan").toString())
                db.child(token).child(time).child("addressKelurahan").setValue(pref.getValues("kelurahan").toString())
                db.child(token).child(time).child("addressKodepos").setValue(pref.getValues("kodepos").toString())
                db.child(token).child(time).child("addressAdditional").setValue(pref.getValues("additionalAddress").toString())
                db.child(token).child(time).child("paymentMethod").setValue(paymentMethodChoosen)
                db.child(token).child(time).child("paymentMethodCode").setValue(paymentMethodCodeChoosen)
                Toast.makeText(applicationContext, "Transaksi berhasil", Toast.LENGTH_LONG).show()
                val i = Intent(this@PlantingPaymentActivity, PlantingPaymentConfirmActivity::class.java)
                i.putExtra(PlantingPaymentConfirmActivity.EXTRA_PAYMENT, choosenListPaymentMethod)
                i.putExtra(PlantingPaymentConfirmActivity.EXTRA_TOTAL_PRICE, plantingData.price.toString())
                startActivity(Intent(i))
                finishAffinity()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, "Tidak dapat melakukan transaksi, coba lagi", Toast.LENGTH_LONG).show()
            }

        })
    }
}