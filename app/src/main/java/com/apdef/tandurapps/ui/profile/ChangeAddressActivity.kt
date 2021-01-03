package com.apdef.tandurapps.ui.profile

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.User
import com.apdef.tandurapps.storage.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_change_address.*

class ChangeAddressActivity : AppCompatActivity() {
    private lateinit var pref : SharedPref
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_address)

        pref = SharedPref(this)

        et_kecamatan.setText(pref.getValues("kecamatan").toString())
        et_kelurahan.setText(pref.getValues("kelurahan").toString())
        et_pos.setText(pref.getValues("kodepos").toString())
        et_add.setText(pref.getValues("additionalAddress").toString())

        val kecamatan = et_kecamatan.text.toString()
        val kelurahan = et_kelurahan.text.toString()
        val pos = et_pos.text.toString()
        val add = et_add.text.toString()

        btn_save.setOnClickListener {
            if(kecamatan.equals("")){
                et_kecamatan.setError("Mohon isi kecamatan Anda")
                et_kecamatan.requestFocus()
            }else if(kelurahan.equals("")){
                et_kelurahan.setError("Mohon isi kelurahan Anda")
                et_kelurahan.requestFocus()
            }else if(pos.equals("")){
                et_pos.setError("Mohon isi kode pos Anda")
                et_pos.requestFocus()
            }else if (add.equals("")){
                et_add.setError("Mohon isi keterangan tambahan Alamat Anda")
                et_add.requestFocus()
            }else{
                saveAddress(pref)
            }
        }
    }

    private fun saveAddress(pref: SharedPref){
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("user")
        var currentUser = mAuth.currentUser
        val token = currentUser?.uid.toString()

        db.child(token).addValueEventListener(object :ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                db.child(token).child("kecamatan").setValue(et_kecamatan.text.toString())
                db.child(token).child("kelurahan").setValue(et_kelurahan.text.toString())
                db.child(token).child("kodepos").setValue(et_pos.text.toString())
                db.child(token).child("additionalAddress").setValue(et_add.text.toString())

                pref.setValues("kecamatan", et_kecamatan.text.toString())
                pref.setValues("kelurahan", et_kelurahan.text.toString())
                pref.setValues("kodepos", et_pos.text.toString())
                pref.setValues("additionalAddress", et_add.text.toString())

                Toast.makeText(applicationContext, "Alamat berhasil disimpan", Toast.LENGTH_LONG).show()
                finish()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChangeAddressActivity, "Tidak dapat mengubah data, coba lagi", Toast.LENGTH_LONG).show()
            }

        })
    }
}