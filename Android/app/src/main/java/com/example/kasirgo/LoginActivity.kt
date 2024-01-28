package com.example.kasirgo

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SharePref.Companion.setAuth
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.databinding.ActivityLoginBinding
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

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, MenuKasirActivity::class.java))
        }

        binding.btnLogin.setOnClickListener {
            login()
            _GetMe()
        }

    }

    override fun onBackPressed() {
        System.exit(0)
    }


    private fun _GetMe() {
            lifecycleScope.launch() {
        try {
                withContext(Dispatchers.IO) {
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/me").openConnection() as HttpURLConnection
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
                        val id_user=jsonKaryawan.getString("user_id")
                        SharePreftLogin.id_user=id_user
                    }
                }
        }catch (e:Exception){
            Log.e("Error Htttp",e.message.toString())
        }
            }

    }

    private fun login() {
        val handler = CoroutineExceptionHandler { _, e ->
            if (e is Exception) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setNeutralButton("Ok") { _, _ -> }
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
                try {
                    if (binding.tiUsername.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Email cannot be empty")
                    if (binding.tiPassword.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Password cannot be empty")

                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/auth").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"

                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    OutputStreamWriter(conn.outputStream).use {
                        it.write(JSONObject().apply {
                            put("username",binding.tiUsername.text.toString())
                            put("password", binding.tiPassword.text.toString())
                        }.toString())
                    }

                    val code = conn.responseCode

                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }
                    withContext(Dispatchers.Main) {
                        val json = JSONObject(body!!)
                        if (code !in 200 until 300) {
                            if (code == 400) {
                                Toast.makeText(
                                    this@LoginActivity,
                                    json.getString("message"),
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(this@LoginActivity, "Login Gagal", Toast.LENGTH_SHORT)
                                    .show()
                            }
                        } else {
                            AlertDialog.Builder(this@LoginActivity)
                                .setTitle("Information")
                                .setMessage("Login success")
                                .setNeutralButton("Ok") { _, _ -> }
                                .setOnDismissListener {
                                    setAuth(json)
                                    val role = json.getString("role")
                                    if (role == "kasir") {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                MenuKasirActivity::class.java
                                            )
                                        )
                                    }
                                    if (role == "admin") {
                                        startActivity(
                                            Intent(
                                                this@LoginActivity,
                                                MenuAdminActivity::class.java
                                            )
                                        )
                                    }
                                }
                                .create()
                                .show()
                        }
                    }
                }catch (e:java.lang.Exception){
                    Log.e("Error Http",e.message.toString())
                }
            }

         }

    }
}