package com.apdef.tandurapps.ui.manage.history

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.DataSell
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_history.*

class HistoryActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    lateinit var dbRef: DatabaseReference
    private var listHistory = ArrayList<DataSell>()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_history)
        dbRef = FirebaseDatabase.getInstance().getReference("sell")

        getHistory()
        iv_back.setOnClickListener {
            onBackPressed()
        }
    }

    private fun getHistory(){
        progressbar_history.visibility = View.VISIBLE
        mAuth = FirebaseAuth.getInstance()
        var currentUser = mAuth.currentUser
        val token = currentUser?.uid.toString()
        dbRef.child(token).addValueEventListener((object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listHistory.clear()
                for (getDataSnapshot in snapshot.children){
                    val dataSellFromFirebase = getDataSnapshot.getValue(DataSell::class.java)
                    listHistory.add(dataSellFromFirebase!!)
                }
                progressbar_history.visibility = View.INVISIBLE
                showListHistory(listHistory)
            }

            override fun onCancelled(error: DatabaseError) {
                progressbar_history.visibility = View.INVISIBLE
                Toast.makeText(this@HistoryActivity, "Gagal menyimpan data", Toast.LENGTH_LONG).show()
            }

        }))
    }

    private fun showListHistory(listHistoy: ArrayList<DataSell>){
        var rvHistory = rv_history
        rvHistory?.setHasFixedSize(true)
        rvHistory?.layoutManager = LinearLayoutManager(this)
        val historyAdapter = HistoryAdapter(listHistoy)
        rvHistory.adapter = historyAdapter
        historyAdapter.setOnClickItem(object: HistoryAdapter.ItemClick{
            override fun itemOnClicked(data: DataSell) {
                startActivity(Intent(this@HistoryActivity, HistoryDetailActivity::class.java).putExtra(HistoryDetailActivity.EXTRA_HISTORY, data))
            }

        })
    }
}