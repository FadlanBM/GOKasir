package com.example.kasirgo.ui.karyawan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.databinding.ActivityUpdateKaryawanBinding
import com.example.kasirgo.library.ExceptionMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL

class UpdateKaryawanActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdateKaryawanBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityUpdateKaryawanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _GetToko()

        binding.btnRowBack.setOnClickListener {
            val intent=Intent(this,MenuAdminActivity::class.java)
            intent.putExtra("back","karyawan")
            startActivity(intent)
        }

        val id=intent.getStringExtra("id")
        _GetDataKaryawan(id!!)
        binding.btnSubmit.setOnClickListener {
            _UpdateKaryawan(id!!)
        }
    }

    override fun onBackPressed() {
        val intent= Intent(this, MenuAdminActivity::class.java)
        intent.putExtra("back","karyawan")
        startActivity(intent)
        super.onBackPressed()
    }

    private fun _GetToko() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn = URL("${BaseAPI.BaseAPI}/api/tokoprofile").openConnection() as HttpURLConnection
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
                    var adapter= ArrayAdapter(this@UpdateKaryawanActivity, R.layout.list_toko,item)

                    autoComplite.setAdapter(adapter)
                    autoComplite.onItemClickListener=
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val itemSelected=adapterView.getItemAtPosition(i)
                        }
                }
            }
        }
    }


    private fun _GetDataKaryawan(id:String) {
        binding.PBUpdateKaryawan.isVisible=true
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn = URL("${BaseAPI.BaseAPI}/api/karyawan/${id}").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")

                val code = conn.responseCode
                Log.e("data", code.toString())

                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }

                withContext(Dispatchers.Main) {
                    val karyawanJson= JSONObject(body!!)
                    binding.PBUpdateKaryawan.isVisible=false
                    val dataKaryawan=karyawanJson.getJSONObject("Data")
                    val nik =dataKaryawan.getString("nik")
                    val nama=dataKaryawan.getString("nama_karyawan")
                    val username=dataKaryawan.getString("username")
                    val telp=dataKaryawan.getString("telp")
                    binding.tiNamaPetugas.setText(nama)
                    binding.tiUsername.setText(username)
                    binding.tiNikPetugas.setText(nik)
                    binding.tiNoTelp.setText(telp)
                }
            }
        }
    }
    private fun _UpdateKaryawan(id: String) {
        val handler = CoroutineExceptionHandler { _, e ->
            if (e is Exception) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setNeutralButton("Yes") { _, _ -> }
                    .setOnDismissListener {
                        if (e !is ExceptionMessage.IgnorableException) {
                            throw RuntimeException(e)
                        }
                    }
                    .create()
                    .show()
            } else {
                throw RuntimeException(e)
            }
        }

        lifecycleScope.launch(handler) {
            withContext(Dispatchers.IO) {
                val Nama=binding.tiNamaPetugas
                val Nik=binding.tiNikPetugas
                val Username=binding.tiUsername
                val NoTelp=binding.tiNoTelp
                val comboDitentitas=binding.comboBoxListToko

                if (Nama.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (Nik.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nik Petugas masih kosong")
                if (Username.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Username masih kosong")
                if (NoTelp.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form No Telp Petugas masih kosong")
                if (comboDitentitas.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Comfirm Password Petugas masih kosong")

                val conn = URL("http://10.0.2.2:8080/api/karyawan/${id}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Authorization", "Bearer $SharePref.token}")
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("nama_petugas",Nama.text.toString())
                        put("nik",Nik.text.toString())
                        put("telp",NoTelp.text.toString() )
                        put("username",Username.text.toString() )
                        put("toko_name", comboDitentitas.text)
                    }.toString())
                }
                val code = conn.responseCode
                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }


                withContext(Dispatchers.Main) {
                    if (code in 200 until 300){
                        AlertDialog.Builder(this@UpdateKaryawanActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil input data")
                            .setNeutralButton("OK") {_,_->
                                Nama.setText("")
                                Nik.setText("")
                                Username.setText("")
                                NoTelp.setText("")
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
    }
}