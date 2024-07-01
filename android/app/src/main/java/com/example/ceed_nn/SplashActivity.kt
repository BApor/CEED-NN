package com.example.ceed_nn

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Handler
import androidx.appcompat.app.AppCompatActivity
import android.util.Log

class SplashActivity : AppCompatActivity() {
    private val TAG = "Lifecycle"
    private val SPLASH_TIMEOUT: Long = 2000

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.setBackgroundDrawable(ColorDrawable(Color.parseColor("#FFCED993")))
        setContentView(R.layout.activity_splash)
        Log.d(TAG, "onCreate: SplashActivity created.")



        Handler().postDelayed({
            val mainIntent = Intent(this@SplashActivity, MainActivity::class.java)
            startActivity(mainIntent)
            finish()
        }, SPLASH_TIMEOUT)
    }
}
