package com.example.kasirgo.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.databinding.ActivityUbahDataBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class UbahDataActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUbahDataBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUbahDataBinding.inflate(layoutInflater)
        setContentView(binding.root)

        _GetToko()
        _GetUser()

        binding.btnUpdateData.setOnClickListener {
            if (binding.tiNameUpdate.text!!.isEmpty()){
                binding.tiNameUpdate.setError("Form Nama Masih Kosong")
            }
            if (binding.tiNikUpdate.text!!.isEmpty()){
                binding.tiNikUpdate.setError("Form Nik Masih Kosong ")
            }
            if (binding.tiUsernameUpdate.text!!.isEmpty()){
                binding.tiUsernameUpdate.setError("Form Username Masih Kosong")
            }
            if (binding.tiPhoneUpdate.text!!.isEmpty()){
                binding.tiPhoneUpdate.setError("Form Phone Masih Kosong")
            }
            if (binding.tiNameUpdate.text!!.isEmpty()||binding.tiNikUpdate.text!!.isEmpty()||binding.tiUsernameUpdate.text!!.isEmpty()||binding.tiPhoneUpdate.text!!.isEmpty()) return@setOnClickListener

            _UpdateUser()

        }
    }
    private fun _GetToko() {
        binding.PBUpdatePribadi.isVisible=true
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/tokoprofile").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                val code = conn.responseCode
                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }

                withContext(Dispatchers.Main) {
                    val jsontoko = JSONObject(body!!)
                    val jsonArray=jsontoko.getJSONArray("data")
                    val item =ArrayList<String>()
                    for (i in 0 until jsonArray.length()){
                        val jsonObject=jsonArray.getJSONObject(i)
                        val namaToko=jsonObject.getString("name")
                        item.add(namaToko)
                    }
                    var autoComplite: AutoCompleteTextView =binding.comboBoxListToko
                    var adapter= ArrayAdapter(this@UbahDataActivity,R.layout.list_toko,item)
                    autoComplite.setAdapter(adapter)
                    autoComplite.onItemClickListener=
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                        val itemSelected=adapterView.getItemAtPosition(i)
                    }
                }
            }
        }
    }

    private fun _GetTokoByid(id_toko:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn = URL("${BaseAPI.BaseAPI}/api/tokoprofile/$id_toko").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                val code = conn.responseCode

                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }


                withContext(Dispatchers.Main) {
                    binding.PBUpdatePribadi.isVisible=false
                    val jsontoko = JSONObject(body!!)
                    val jsonjson=jsontoko.getJSONObject("Data")
                    val nama=jsonjson.getString("name")
                    binding.comboBoxListToko.setText(nama)
                }
            }
        }
    }
    private fun _GetUser() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/admin/${SharePreftLogin.id_user}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                val code = conn.responseCode
                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }


                withContext(Dispatchers.Main) {
                    val jsonKaryawan = JSONObject(body!!)
                    val data=jsonKaryawan.getJSONObject("Data")
                    val nik =data.getString("nik")
                    val nama=data.getString("nama_karyawan")
                    val username=data.getString("username")
                    val telp=data.getString("telp")
                    val id_toko=data.getString("TokoProfileID")
                    binding.tiNikUpdate.setText(nik)
                    binding.tiNameUpdate.setText(nama)
                    binding.tiUsernameUpdate.setText(username)
                    binding.tiPhoneUpdate.setText(telp)
                    _GetTokoByid(id_toko)
                }
            }
        }
    }
    private fun _UpdateUser() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn = URL("${BaseAPI.BaseAPI}/api/admin/${SharePreftLogin.id_user}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("nik", binding.tiNikUpdate.text.toString())
                        put("nama_petugas", binding.tiNameUpdate.text.toString())
                        put("telp", binding.tiPhoneUpdate.text.toString())
                        put("username", binding.tiUsernameUpdate.text.toString())
                        put("toko_name", binding.comboBoxListToko.text.toString())
                    }.toString())
                }
                val code = conn.responseCode
                Log.e("data", code.toString())

                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }


                withContext(Dispatchers.Main) {
                    val jsonKaryawan = JSONObject(body!!)
                    if (code !in 200 until 300) {
                        if (code == 400) {
                            Toast.makeText(
                                this@UbahDataActivity,
                                jsonKaryawan.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@UbahDataActivity, "Login Gagal", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(this@UbahDataActivity)
                            .setTitle("Information")
                            .setMessage("Update Data success")
                            .setNeutralButton("Ok") { _, _ -> }
                            .setOnDismissListener {
                                startActivity(Intent(this@UbahDataActivity,UbahDataActivity::class.java))
                            }
                            .create()
                            .show()
                    }
                }
            }
        }

    }

    override fun onBackPressed() {
        super.onBackPressed()
        startActivity(Intent(this@UbahDataActivity,MainSettingActivity::class.java))

    }
}