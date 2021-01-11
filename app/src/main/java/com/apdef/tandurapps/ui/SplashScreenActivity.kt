package com.apdef.tandurapps.ui

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.apdef.tandurapps.R
import com.apdef.tandurapps.ui.auth.LoginActivity

class SplashScreenActivity : AppCompatActivity() {
    private val SPLASH_TIME_OUT: Long = 1000
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        Handler().postDelayed({
            Intent(this, LoginActivity::class.java).also {
                it.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(it)
            }
        }, SPLASH_TIME_OUT)
    }
}