package com.example.kasirgo.ui.karyawan

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SharePref.Companion.setAuth
import com.example.kasirgo.databinding.ActivityCreateKaryawanBinding
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

class CreateKaryawanActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCreateKaryawanBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateKaryawanBinding.inflate(layoutInflater)
        setContentView(binding.root)
        _GetToko()

        binding.btnRowBack.setOnClickListener {
            val intent=Intent(this,MenuAdminActivity::class.java)
            intent.putExtra("back","karyawan")
            startActivity(intent)
        }

        binding.btnSubmit.setOnClickListener {
            _CreateKaryawan()
        }
    }

    private fun _GetToko() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/tokoprofile").openConnection() as HttpURLConnection
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
                    val jsontoko = JSONObject(body!!)
                    val jsonArray=jsontoko.getJSONArray("data")
                    val item =ArrayList<String>()
                    for (i in 0 until jsonArray.length()){
                        val jsonObject=jsonArray.getJSONObject(i)
                        val namaToko=jsonObject.getString("name")
                        item.add(namaToko)
                    }
                    var autoComplite: AutoCompleteTextView =binding.comboBoxListToko
                    var adapter= ArrayAdapter(this@CreateKaryawanActivity,R.layout.list_toko,item)

                    autoComplite.setAdapter(adapter)
                    autoComplite.onItemClickListener=
                        AdapterView.OnItemClickListener { adapterView, view, i, l ->
                            val itemSelected=adapterView.getItemAtPosition(i)
                        }
                }
            }
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this,MenuAdminActivity::class.java)
        intent.putExtra("back","karyawan")
        startActivity(intent)
        super.onBackPressed()
    }

    private fun _CreateKaryawan() {
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
                val Password=binding.tiPasswordPetugas
                val ConfirmPassword=binding.tiComPassword
                val comboDitentitas=binding.comboBoxListToko
                val NoTelp=binding.tiNoTelp
                var tokoID=0

                if (Nama.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (Nik.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nik Petugas masih kosong")
                if (Username.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Username masih kosong")
                if (Password.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Password Petugas masih kosong")
                if (ConfirmPassword.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Comfirm Password Petugas masih kosong")
                if (comboDitentitas.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Comfirm Password Petugas masih kosong")
                if (NoTelp.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form No Telp Petugas masih kosong")
                if (Password.text.toString()!=ConfirmPassword.text.toString()) throw  ExceptionMessage.IgnorableException("Confirm Password tidak sama")

                val conn =
                    URL("${BaseAPI.BaseAPI}/api/karyawan").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"

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
                        put("password",Password.text.toString() )
                        put("telp",NoTelp.text.toString() )
                        put("username",Username.text.toString() )
                        put("toko_id", tokoID)
                        put("toko_name", comboDitentitas.text)
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
                    val json = JSONObject(body!!)
                    Log.e("Hasil",json.toString())

                    if (code in 200 until 300){
                        AlertDialog.Builder(this@CreateKaryawanActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil input data")
                            .setNeutralButton("OK") {_,_->
                                Nama.setText("")
                                Nik.setText("")
                                Username.setText("")
                                Password.setText("")
                                ConfirmPassword.setText("")
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