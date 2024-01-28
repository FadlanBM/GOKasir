package com.example.kasirgo.ui.setting

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.databinding.ActivityResetPasswordBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

class ResetPasswordActivity : AppCompatActivity() {
    private lateinit var binding:ActivityResetPasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityResetPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnUpdateData.setOnClickListener {
            if (binding.tiPasswordOld.text!!.isEmpty()){
                binding.tiPasswordOld.setError("Form Password Old Masih Kosong")
            }
            if (binding.tiPasswordNew.text!!.isEmpty()){
                binding.tiPasswordNew.setError("Form Password New Masih Kosong")
            }
            if (binding.tiPasswordConfirm.text!!.isEmpty()){
                binding.tiPasswordConfirm.setError("Form Password Confirm New Masih Kosong")
            }

            if (binding.tiPasswordOld.text!!.isEmpty()||binding.tiPasswordNew.text!!.isEmpty()||binding.tiPasswordConfirm.text!!.isEmpty()) return@setOnClickListener

            _ResetPassword()
        }
    }
    private fun _ResetPassword() {
        Log.e("uid",SharePreftLogin.id_user)
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn = URL("${BaseAPI.BaseAPI}/api/admin/resetPass/${SharePreftLogin.id_user}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("password_new", binding.tiPasswordNew.text.toString())
                        put("password_old", binding.tiPasswordOld.text.toString())
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
                                this@ResetPasswordActivity,
                                jsonKaryawan.getString("message"),
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(this@ResetPasswordActivity, "Password Old Salah", Toast.LENGTH_SHORT)
                                .show()
                        }
                    } else {
                        AlertDialog.Builder(this@ResetPasswordActivity)
                            .setTitle("Information")
                            .setMessage("Update Password success")
                            .setNeutralButton("Ok") { _, _ -> }
                            .setOnDismissListener {
                                startActivity(Intent(this@ResetPasswordActivity,MainSettingActivity::class.java))
                            }
                            .create()
                            .show()
                    }
                }
            }
        }

    }
}