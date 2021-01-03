package com.apdef.tandurapps.ui.home

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.PlantingPackage
import com.apdef.tandurapps.storage.SharedPref
import com.apdef.tandurapps.ui.home.adapter.PlantingPackageAdapter
import com.bumptech.glide.Glide
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.fragment_home.*

class PlantingFragment : Fragment() {
    private lateinit var pref : SharedPref
    lateinit var dbRef: DatabaseReference
    private var listPlantingPackage = ArrayList<PlantingPackage>()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_home, container, false)
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        dbRef = FirebaseDatabase.getInstance().getReference("planting")
        getPlantingPackage()
        pref = SharedPref(activity!!.applicationContext)
        loadProfilePicture()
    }

    override fun onResume() {
        super.onResume()
        loadProfilePicture()
    }

    private fun getPlantingPackage(){
        dbRef.addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                listPlantingPackage.clear()
                for (getDataSnapshot in snapshot.children){
                    val plantingPackage = getDataSnapshot.getValue(PlantingPackage::class.java)
                    listPlantingPackage.add(plantingPackage!!)
                }
                showListPlantingPackage(listPlantingPackage)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(context, ""+ error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    private fun showListPlantingPackage(listPlantingPackage: java.util.ArrayList<PlantingPackage>){
        var rvPlanting = rv_planting as RecyclerView?
        rvPlanting?.setHasFixedSize(true)
        rvPlanting?.layoutManager = LinearLayoutManager(context)
        val plantingPackageAdapter = PlantingPackageAdapter(listPlantingPackage)
        rvPlanting?.adapter = plantingPackageAdapter
        plantingPackageAdapter.setOnClickItem(object : PlantingPackageAdapter.ItemClick{
            override fun itemOnClicked(plantingPackage: PlantingPackage) {
                startActivity(Intent(context, PlantingDetailActivity::class.java).putExtra(PlantingDetailActivity.EXTRA_PLANTING_PACKAGE, plantingPackage))
            }
        })
    }

    private fun loadProfilePicture(){
        val imageUrl = pref.getValues("imageProfileUrl")
        if (imageUrl!=null){
            Glide.with(this)
                .load(imageUrl)
                .into(img_profile)
        }
    }
}