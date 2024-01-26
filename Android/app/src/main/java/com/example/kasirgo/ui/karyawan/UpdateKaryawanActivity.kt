package com.example.kasirgo.ui.karyawan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityCreateKaryawanBinding
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


    private fun _GetDataKaryawan(id:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/karyawan/${id}").openConnection() as HttpURLConnection
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
                    val karyawanJson= JSONObject(body!!)
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
                var tokoID=0

                if (Nama.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (Nik.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nik Petugas masih kosong")
                if (Username.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Username masih kosong")
                if (NoTelp.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form No Telp Petugas masih kosong")

                val conn =
                    URL("http://10.0.2.2:8080/api/karyawan/${id}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                    tokoID=it.getInt("toko_id")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("nama_petugas",Nama.text.toString())
                        put("nik",Nik.text.toString())
                        put("telp",NoTelp.text.toString() )
                        put("username",Username.text.toString() )
                        put("toko_id", tokoID)
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