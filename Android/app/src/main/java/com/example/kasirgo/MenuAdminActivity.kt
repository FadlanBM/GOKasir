package com.example.kasirgo

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.navigation.NavigationView
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import androidx.drawerlayout.widget.DrawerLayout
import androidx.appcompat.app.AppCompatActivity
import com.example.kasirgo.databinding.ActivityMenuAdminBinding
import com.example.kasirgo.ui.setting.MainSettingActivity

class MenuAdminActivity : AppCompatActivity() {

    private lateinit var appBarConfiguration: AppBarConfiguration
    private lateinit var binding: ActivityMenuAdminBinding
    private var doubleBackToExitPressedOnce = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMenuAdminBinding.inflate(layoutInflater)
        setContentView(binding.root)

        setSupportActionBar(binding.appBarMenuAdmin.toolbar)

        val drawerLayout: DrawerLayout = binding.drawerLayout
        val navView: NavigationView = binding.navView
        val navController = findNavController(R.id.nav_host_fragment_content_menu_admin)
        val status=intent.getStringExtra("back")

        if (status=="karyawan"){
            navController.navigate(R.id.nav_gallery)
        }
        if (status=="barang"){
            navController.navigate(R.id.nav_slideshow)
        }
        if (status=="voucer"){
            navController.navigate(R.id.nav_voucer)
        }
        if (status=="admin"){
            navController.navigate(R.id.nav_admin)
        }
        if (status=="member"){
            navController.navigate(R.id.nav_member)
        }

        appBarConfiguration = AppBarConfiguration(
            setOf(
                 R.id.nav_admin,R.id.nav_gallery, R.id.nav_slideshow , R.id.nav_voucer,R.id.nav_member
            ), drawerLayout
        )

        setupActionBarWithNavController(navController, appBarConfiguration)
        navView.setupWithNavController(navController)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_admin, menu)
        return true
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
    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.action_settings -> {
                startActivity(Intent(this,MainSettingActivity::class.java))
            }R.id.action_logout -> {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Apakah anda ingin logout")
                .setPositiveButton("Yes") { _, _ ->
                    startActivity(Intent(this,LoginActivity::class.java))
                }
                .setNegativeButton("No"){_,_->

                }
                .create()
                .show()
                return true
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onSupportNavigateUp(): Boolean {
        val navController = findNavController(R.id.nav_host_fragment_content_menu_admin)
        return navController.navigateUp(appBarConfiguration) || super.onSupportNavigateUp()
    }


}