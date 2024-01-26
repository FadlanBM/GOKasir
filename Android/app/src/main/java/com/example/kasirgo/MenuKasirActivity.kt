package com.example.kasirgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.viewpager.widget.ViewPager
import com.example.kasirgo.AdaptorTL.AdapterMenuTL
import com.example.kasirgo.databinding.ActivityMenuKasirBinding
import com.google.android.material.tabs.TabLayout

class MenuKasirActivity : AppCompatActivity() {
    private lateinit var binding:ActivityMenuKasirBinding
    private lateinit var tabLayout:TabLayout
    private lateinit var viewPager: ViewPager
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityMenuKasirBinding.inflate(layoutInflater)
        setContentView(binding.root)

        tabLayout=binding.tlMenu
        viewPager=binding.vpMenu


        val adapter=AdapterMenuTL(supportFragmentManager,tabLayout.tabCount)
        viewPager.adapter=adapter

        viewPager.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(tabLayout))
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener{
            override fun onTabSelected(tab: TabLayout.Tab?) {
                viewPager.currentItem=tab!!.position
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
            }

        })

        if (intent.getStringExtra("status")=="kasir"){
            val tab= tabLayout.getTabAt(0)
            tabLayout.selectTab(tab)
        }

    }

    override fun onBackPressed() {

        if (doubleBackToExitPressedOnce) {
            startActivity(Intent(this,LoginActivity::class.java))
            return
        }

        this.doubleBackToExitPressedOnce = true
        Toast.makeText(this, "Klik sekali lagi untuk keluar", Toast.LENGTH_SHORT).show()

        Handler().postDelayed({
            doubleBackToExitPressedOnce = false
        }, 2000)

    }
}