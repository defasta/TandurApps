package com.apdef.tandurapps.ui.manage.mypackage

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.Transaction
import com.apdef.tandurapps.ui.manage.adapter.MyPackageAdapter
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_my_package.*

class MyPackageActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    lateinit var dbRef: DatabaseReference
    private var listPlantingMyPackage = ArrayList<com.apdef.tandurapps.model.Transaction>()
    private var user: FirebaseUser? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_package)
        dbRef = FirebaseDatabase.getInstance().getReference("transaction")
        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser


        getPlantingMyPackage()
    }

    private fun getPlantingMyPackage(){
        val userAuth = mAuth.currentUser
        val token  = userAuth?.uid.toString()
        dbRef.child(token).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listPlantingMyPackage.clear()
                for (getDataSnapshot in snapshot.children){
                    val plantingMyPackage = getDataSnapshot.getValue(com.apdef.tandurapps.model.Transaction::class.java)
                    listPlantingMyPackage.add(plantingMyPackage!!)
                }
                showListPlantingMyPackage(listPlantingMyPackage)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(applicationContext, ""+ error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun showListPlantingMyPackage(transaction: java.util.ArrayList<Transaction>){
        var rvMyPlanting = rv_my_planting
        rvMyPlanting?.setHasFixedSize(true)
        rvMyPlanting?.layoutManager = LinearLayoutManager(this)
        val plantingMyPackageAdapter = MyPackageAdapter(listPlantingMyPackage)
        rvMyPlanting?.adapter = plantingMyPackageAdapter
        plantingMyPackageAdapter.setOnClickItem(object : MyPackageAdapter.ItemClick{
            override fun itemOnClicked(transaction: com.apdef.tandurapps.model.Transaction) {
                when (transaction.status) {
                    "1" -> startActivity(Intent(this@MyPackageActivity, MyPackageDetailActivity::class.java).putExtra(
                        MyPackageDetailActivity.EXTRA_PLANTING_PACKAGE, transaction))
                    "0" -> startActivity(Intent(this@MyPackageActivity, MyPackagePaymentConfirmActivity::class.java).putExtra(
                        MyPackagePaymentConfirmActivity.EXTRA_PAYMENT, transaction))
                }
            }

        })
    }
}