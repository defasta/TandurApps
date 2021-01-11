package com.apdef.tandurapps.ui.auth

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import com.apdef.tandurapps.MainActivity
import com.apdef.tandurapps.R
import com.apdef.tandurapps.model.User
import com.apdef.tandurapps.storage.SharedPref
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_register.*

class RegisterActivity : AppCompatActivity() {
    private lateinit var mAuth : FirebaseAuth
    private lateinit var db: DatabaseReference
    private lateinit var pref : SharedPref
    private lateinit var username: String
    private lateinit var email: String
    private lateinit var password: String
    private lateinit var passwordConfirm: String
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        setContentView(     R.layout.activity_register)

        pref = SharedPref(this)
        mAuth = FirebaseAuth.getInstance()
        db = FirebaseDatabase.getInstance().getReference("user")

        btnSignUp.setOnClickListener {
            if(etUsername.text.toString().equals("")){
                etUsername.error = "Username tidak boleh kosong!"
                etUsername.requestFocus()
            }else if (etEmail.text.toString().equals("")){
                etEmail.error = "Email tidak boleh kosong!"
                etEmail.requestFocus()
            }else if(etPassword.text.toString().equals("")){
                etPassword.error = "Password tidak boleh kosong!"
                etPassword.requestFocus()
            }else if(etPassConfirm.text.toString().equals("")){
                etPassConfirm.error = "Konfirmasi password tidak boleh kosong!"
                etPassConfirm.requestFocus()
            }else if(etPassConfirm.text.toString() != etPassword.text.toString()){
                etPassConfirm.error = "Password belum sama!"
                etPassConfirm.requestFocus()
            }else{
                username = etUsername.text.toString()
                password = etPassword.text.toString()
                passwordConfirm = etPassConfirm.text.toString()
                email = etEmail.text.toString()
                saveUser(username, email, password)
            }
        }

        tv_to_login.setOnClickListener {
            val i = Intent(this@RegisterActivity, LoginActivity::class.java)
            startActivity(i)
        }
    }

    private fun saveUser(username:String, email: String, password: String) {
        progressbar_register.visibility = View.VISIBLE
        mAuth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    var currentUser = mAuth.currentUser
                    val token = currentUser?.uid.toString()
                    val data = User()
                    data.username = username
                    data.token = token
                    data.email = email

                    db.child(token).addValueEventListener(object: ValueEventListener{
                        override fun onDataChange(snapshot: DataSnapshot) {
                            val user = snapshot.getValue(User::class.java)
                            if (user==null){
                                db.child(token).setValue(data)
                                pref.setValues("username", data.username.toString())
                                pref.setValues("email", data.email.toString())
                                pref.setValues("kecamatan", data.kecamatan.toString())
                                pref.setValues("kelurahan", data.kelurahan.toString())
                                pref.setValues("kodepos", data.kodepos.toString())
                                pref.setValues("additionalAddress", data.additionalAddress.toString())
                                progressbar_register.visibility = View.INVISIBLE
                                val i = Intent(this@RegisterActivity, MainActivity::class.java)
                                startActivity(i)
                                Toast.makeText(applicationContext, "Selamat Datang", Toast.LENGTH_LONG).show()
                            }
                        }

                        override fun onCancelled(p0: DatabaseError) {
                            progressbar_register.visibility = View.INVISIBLE
                            Toast.makeText(this@RegisterActivity, "Autentikasi Gagal", Toast.LENGTH_LONG).show()
                        }

                    })


                }else{
                    progressbar_register.visibility = View.INVISIBLE
                    Toast.makeText(this@RegisterActivity, "Autentikasi Gagal", Toast.LENGTH_LONG).show()
                }
            }
    }
}