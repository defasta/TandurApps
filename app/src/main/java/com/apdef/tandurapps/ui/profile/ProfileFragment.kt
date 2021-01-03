package com.apdef.tandurapps.ui.profile

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.Transaction
import com.apdef.tandurapps.storage.SharedPref
import com.apdef.tandurapps.ui.auth.LoginActivity
import com.apdef.tandurapps.ui.home.PlantingPaymentConfirmActivity
import com.apdef.tandurapps.ui.manage.adapter.MyPackageAdapter
import com.apdef.tandurapps.ui.manage.mypackage.MyPackageDetailActivity
import com.apdef.tandurapps.ui.manage.mypackage.MyPackagePaymentConfirmActivity
import com.bumptech.glide.Glide
import com.google.android.gms.tasks.Continuation
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.fragment_profile.*
import java.util.*
import kotlin.collections.ArrayList

class ProfileFragment : Fragment() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var pref : SharedPref
    lateinit var dbRef: DatabaseReference

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_profile, container, false)
    }


    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        pref = SharedPref(activity!!.applicationContext)
        mAuth = FirebaseAuth.getInstance()
        dbRef = FirebaseDatabase.getInstance().getReference("transaction")


        val username = pref.getValues("username")
        val email = pref.getValues("email")
        val additionalAddres = pref.getValues("additionalAddress")
        val kelurahan = pref.getValues("kelurahan")
        val kecamatan = pref.getValues("kecamatan")
        val kodepos = pref.getValues("kodepos")
        val ivProfile = pref.getValues("imageProfileUrl")

        if (ivProfile != ""){
            Glide.with(this)
                .load(ivProfile)
                .into(img_profile)
        }

        tv_username.text = username
        tv_email.text = email
        tv_address.text = additionalAddres+" "+kelurahan+", "+kecamatan+" "+kodepos


        cv_change_address.setOnClickListener {
            startActivity(Intent(context, ChangeAddressActivity::class.java))
        }

        cv_logout.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(context, LoginActivity::class.java))
        }

        cv_change_pic.setOnClickListener{
            startActivity(Intent(context, ChangeProfilePicture::class.java))
        }
    }

    override fun onResume() {
        super.onResume()
        pref = SharedPref(activity!!.applicationContext)

        val username = pref.getValues("username")
        val email = pref.getValues("email")
        val additionalAddres = pref.getValues("additionalAddress")
        val kelurahan = pref.getValues("kelurahan")
        val kecamatan = pref.getValues("kecamatan")
        val kodepos = pref.getValues("kodepos")
        val ivProfile = pref.getValues("imageProfileUrl")

        if (ivProfile != ""){
            Glide.with(this)
                .load(ivProfile)
                .into(img_profile)
        }

        tv_username.text = username
        tv_email.text = email
        tv_address.text = additionalAddres+" "+kelurahan+", "+kecamatan+" "+kodepos
    }

}