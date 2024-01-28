    package com.example.kasirgo.ui.SplashScreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.CartSharePreft

    class SuccessSplashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_splash)
        Handler().postDelayed({
            val intent = Intent(this, MenuKasirActivity::class.java)
            intent.putExtra("status","history")
            startActivity(intent)
            finish()
        }, 2000)
        CartSharePreft(this).clearCart()
    }
}