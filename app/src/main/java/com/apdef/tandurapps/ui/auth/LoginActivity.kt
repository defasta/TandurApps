package com.apdef.tandurapps.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.DataSell
import com.apdef.tandurapps.model.User
import com.apdef.tandurapps.model.UserData
import com.apdef.tandurapps.storage.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private var user: FirebaseUser? = null
    private var listDataUser = ArrayList<UserData>()
    private lateinit var db: DatabaseReference
    private lateinit var pref : SharedPref
    private lateinit var username: String
    private lateinit var password : String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(R.layout.activity_login)

        mAuth = FirebaseAuth.getInstance()
        user = FirebaseAuth.getInstance().currentUser
        pref = SharedPref(this)

        if (user != null) {
            startActivity(Intent(this, MainActivity::class.java))
        }


        btnSignIn.setOnClickListener {
            username = etEmail.text.toString()
            password = etPassword.text.toString()

            if(username.equals("")){
                etEmail.error = "Masukkan email"
                etEmail.requestFocus()
            }
            else if(password.equals("")){
                etPassword.error = "Masukkan password"
                etPassword.requestFocus()
            }else{
                login(username, password)
            }
        }

        tv_to_register.setOnClickListener {
            val i = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(i)
        }
    }

    private fun login(email:String, password:String){
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if(it.isSuccessful){
                    val userAuth = mAuth.currentUser
                    val token  = userAuth?.uid.toString()
//                    val data = User()
                    db = FirebaseDatabase.getInstance().getReference("user")
                    db.child(token).addValueEventListener(object: ValueEventListener {
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user!=null){
//                                db.child(token).setValue(data)
                                pref.setValues("username", user.username.toString())
                                pref.setValues("email", user.email.toString())
                                pref.setValues("kecamatan", user.kecamatan.toString())
                                pref.setValues("kelurahan", user.kelurahan.toString())
                                pref.setValues("kodepos", user.kodepos.toString())
                                pref.setValues("additionalAddress", user.additionalAddress.toString())
                                pref.setValues("imageProfileUrl", user.imageUrl.toString())
                                val i = Intent(this@LoginActivity, MainActivity::class.java)
                                startActivity(i)
                                Toast.makeText(this@LoginActivity, "Welcome!", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            Toast.makeText(this@LoginActivity, "Gagal menyimpan data", Toast.LENGTH_LONG).show()
                        }

                    })
                }else{
                    Toast.makeText(this@LoginActivity, "Autentikasi Gagal", Toast.LENGTH_LONG).show()
                }
            }

    }

//    private fun saveUser(token:String){
//        val data = User()
//        db = FirebaseDatabase.getInstance().getReference("user")
//        db.child(token).addValueEventListener(object: ValueEventListener {
//            override fun onDataChange(snapshot: DataSnapshot) {
//                val user = snapshot.getValue(User::class.java)
//                if (user==null){
//                    db.child(token).setValue(data)
//                    pref.setValues("username", data.username.toString())
//                    pref.setValues("email", data.email.toString())
//                    pref.setValues("kecamatan", data.kecamatan.toString())
//                    pref.setValues("kelurahan", data.kelurahan.toString())
//                    pref.setValues("kodepos", data.kodepos.toString())
//                    pref.setValues("additionalAddress", data.additionalAddress.toString())
//                    val i = Intent(this@LoginActivity, MainActivity::class.java)
//                    startActivity(i)
//                    Toast.makeText(this@LoginActivity, "Welcome!", Toast.LENGTH_LONG).show()
//                }
//            }
//
//            override fun onCancelled(p0: DatabaseError) {
//                Toast.makeText(this@LoginActivity, "Gagal menyimpan data", Toast.LENGTH_LONG).show()
//            }
//
//        })
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        finishAffinity()
        onDestroy()
    }
}