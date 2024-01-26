package com.example.kasirgo.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListKaryawan
import com.example.kasirgo.AdapterRV.AdapterListSetting
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityMenuAdminBinding
import com.example.kasirgo.databinding.ActivitySettingBinding
import com.example.kasirgo.item.Setting
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class   MainSettingActivity : AppCompatActivity() {
    private lateinit var binding:ActivitySettingBinding
    private lateinit var recyclerViewSettings: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivitySettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerViewSettings=binding.recyclerViewSettings

        val settingsList = listOf(
            Setting("Data Pribadi", "Melihat Data Pribadi"),
            Setting("Ubah Data Pribadi", "Edit Data Pribadi"),
            Setting("Reset Password", "Reset Password"),
            Setting("Logout", "Keluar Dari Akun"),
        )

        val settingsAdapter = AdapterListSetting(settingsList,this)
        recyclerViewSettings.layoutManager = LinearLayoutManager(this)
        recyclerViewSettings.adapter = settingsAdapter
    }
}