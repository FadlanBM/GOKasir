package com.example.kasirgo.ui.member

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
import com.example.kasirgo.databinding.ActivityUpdateMemberBinding
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

class UpdateMemberActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdateMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val id=intent.getStringExtra("id")

        _GetDataKaryawan(id)

        binding.btnSubmit.setOnClickListener {
            _UpdateMember(id)
        }

        binding.btnRowBack.setOnClickListener {
            val intent= Intent(this, MenuAdminActivity::class.java)
            intent.putExtra("back","member")
            startActivity(intent)
        }
    }

    private fun _GetDataKaryawan(id:String?) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/members/${id}").openConnection() as HttpURLConnection
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
                    val memberJson= JSONObject(body!!)
                    val datamember=memberJson.getJSONObject("Data")
                    Log.e("data",datamember.toString())
                    val nama =datamember.getString("name")
                    val alamat =datamember.getString("address")
                    val phone =datamember.getString("phone")
                    binding.tiNamaMember.setText(nama)
                    binding.tiAlamat.setText(alamat)
                    binding.tiNoHp.setText(phone)
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent= Intent(this, MenuAdminActivity::class.java)
        intent.putExtra("back","member")
        startActivity(intent)
        super.onBackPressed()
    }


    private fun _UpdateMember(id: String?) {
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
                val Nama=binding.tiNamaMember
                val alamat=binding.tiAlamat
                val nohp=binding.tiNoHp

                if (Nama.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (alamat.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Alamat masih kosong")
                if (nohp.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form No Hp masih kosong")

                val conn =
                        URL("http://10.0.2.2:8080/api/members/${id}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("name",Nama.text.toString())
                        put("address",alamat.text.toString() )
                        put("phone",nohp.text.toString() )
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
                        AlertDialog.Builder(this@UpdateMemberActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil Update data")
                            .setNeutralButton("OK") {_,_->
                                Nama.setText("")
                                alamat.setText("")
                                nohp.setText("")
                            }
                            .create()
                            .show()
                    }
                }
            }
        }
    }

}