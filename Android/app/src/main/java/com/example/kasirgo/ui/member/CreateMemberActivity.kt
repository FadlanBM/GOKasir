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
import com.example.kasirgo.databinding.ActivityCreateMemberBinding
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

class CreateMemberActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCreateMemberBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateMemberBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRowBack.setOnClickListener {
            val intent= Intent(this, MenuAdminActivity::class.java)
            intent.putExtra("back","member")
            startActivity(intent)
        }


        binding.btnSubmit.setOnClickListener {
            _CreateMember()
        }
    }

    private fun _CreateMember() {
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
                val Alamat=binding.tiAlamat
                val NoHp=binding.tiNoHp
                val Password=binding.tiPasswordMember
                val ConfirmPassword=binding.tiConPassword

                if (Nama.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (Alamat.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Alamat masih kosong")
                if (NoHp.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form No Hp masih kosong")
                if (Password.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Password Petugas masih kosong")
                if (ConfirmPassword.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Comfirm Password Petugas masih kosong")
                if (Password.text.toString()!=ConfirmPassword.text.toString()) throw  ExceptionMessage.IgnorableException("Confirm Password tidak sama")

                val conn =
                    URL("${BaseAPI.BaseAPI}/api/members").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("name",Nama.text.toString())
                        put("address",Alamat.text.toString())
                        put("password",Password.text.toString() )
                        put("phone",NoHp.text.toString() )
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
                        AlertDialog.Builder(this@CreateMemberActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil input data")
                            .setNeutralButton("OK") {_,_->
                                Nama.setText("")
                                Alamat.setText("")
                                NoHp.setText("")
                                Password.setText("")
                                ConfirmPassword.setText("")
                            }
                            .create()
                            .show()
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
}