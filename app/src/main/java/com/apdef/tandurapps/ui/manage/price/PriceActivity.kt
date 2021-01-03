package com.apdef.tandurapps.ui.manage.price

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.PriceList
import com.apdef.tandurapps.model.UpdatePriceTime
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_price.*

class PriceActivity : AppCompatActivity() {
    lateinit var dbRef: DatabaseReference
    private var listPrice = ArrayList<PriceList>()
    private var updatePrice = ArrayList<UpdatePriceTime>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_price)
        getPriceList()
        dbRef = FirebaseDatabase.getInstance().getReference("price").child("update")
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                updatePrice.clear()
                for (getDataSnapshot in snapshot.children){
                    val updatePriceListFromFirebase = getDataSnapshot.getValue(UpdatePriceTime::class.java)
                    updatePrice.add(updatePriceListFromFirebase!!)
                }
                updatePrice.forEach {
                    tv_price_date.text = "Update: "+it.date
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PriceActivity, ""+ error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun getPriceList(){
        dbRef = FirebaseDatabase.getInstance().getReference("price").child("list")
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listPrice.clear()
                for (getDataSnapshot in snapshot.children){
                    val priceListFromFirebase = getDataSnapshot.getValue(PriceList::class.java)
                    listPrice.add(priceListFromFirebase!!)
                }
                showPriceList(listPrice)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@PriceActivity, ""+ error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun showPriceList(listPrice: ArrayList<PriceList>){
        var rvPrice = rv_price
        rvPrice?.setHasFixedSize(true)
        rvPrice?.layoutManager = LinearLayoutManager(this)
        val priceAdapter = PriceAdapter(listPrice)
        rvPrice?.adapter = priceAdapter
    }
}