package com.example.kasirgo.ui.setting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListData
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.databinding.ActivityDataPribadiBinding
import com.example.kasirgo.databinding.ActivityMenuAdminBinding
import com.example.kasirgo.item.Setting
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class DataPribadiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDataPribadiBinding
    private lateinit var recyclerViewSettings: RecyclerView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDataPribadiBinding.inflate(layoutInflater)
        setContentView(binding.root)
        recyclerViewSettings=binding.recyclerViewListData
        _GetUser()
    }
    private fun _GetUser() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/admin/${SharePreftLogin.id_user}").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"

                    getAuth()?.let {
                        conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                    }
                    conn.setRequestProperty("Content-Type", "application/json")
                    val code = conn.responseCode
                    Log.e("data", code.toString())

                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }


                    withContext(Dispatchers.Main) {
                        val jsonKaryawan = JSONObject(body!!)
                        val data=jsonKaryawan.getJSONObject("Data")
                        val settingsList = listOf(
                            Setting("Nomor Nik",data.getString("nik")),
                            Setting("Nama Lengkap",data.getString("nama_karyawan")),
                            Setting("Username", data.getString("username")),
                            Setting("Phone Number",data.getString("telp")),
                        )
                        val settingsAdapter = AdapterListData(settingsList,this@DataPribadiActivity)
                        recyclerViewSettings.layoutManager = LinearLayoutManager(this@DataPribadiActivity)
                        recyclerViewSettings.adapter = settingsAdapter
                    }
                }catch (e:Exception){
                    Log.e("Error Http",e.message.toString())
                }
            }
        }
    }

}