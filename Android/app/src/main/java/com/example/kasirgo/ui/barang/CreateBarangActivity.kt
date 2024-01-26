package com.example.kasirgo.ui.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityInsertBarangBinding
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

class CreateBarangActivity : AppCompatActivity() {
    private lateinit var binding:ActivityInsertBarangBinding


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityInsertBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener {
            _CreateKaryawan()
        }

        binding.btnRowBack.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Warning")
                .setMessage("Apakah anda yakin ingin keluar ?")
                .setPositiveButton("Yes") { _, _ ->
                    val intent=Intent(this@CreateBarangActivity,MenuAdminActivity::class.java)
                    intent.putExtra("back","barang")
                    startActivity(intent)
                }
                .setNegativeButton("No") {_,_->
                }
                .create()
                .show()
        }

        binding.btnRowBack.setOnClickListener {
            val intent=Intent(this@CreateBarangActivity,MenuAdminActivity::class.java)
            intent.putExtra("back","barang")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this@CreateBarangActivity,MenuAdminActivity::class.java)
        intent.putExtra("back","barang")
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
                try {
                val merek=binding.tiMerek
                val nameProduk=binding.tiNamaProduk
                val hargaProduk=binding.tiHargaProduk
                val stokProduk=binding.tiStokProduk
                val tipeProduk=binding.tiTipeProduk
                val codeBarang=intent.getStringExtra("CodeBarang")

                if (merek.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form merek barang masih kosong")
                if (nameProduk.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form nama produk masih kosong")
                if (hargaProduk.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form harga produk kosong")
                if (stokProduk.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form stok produk masih kosong")
                if (tipeProduk.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form tipe produk Petugas masih kosong")

                val conn =
                    URL("${BaseAPI.BaseAPI}/api/barang").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("code",codeBarang.toString())
                        put("merek",merek.text.toString())
                        put("name",nameProduk.text.toString())
                        put("price",hargaProduk.text.toString().toFloat() )
                        put("stok",stokProduk.text.toString().toInt() )
                        put("tipe",tipeProduk.text.toString() )
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
                        AlertDialog.Builder(this@CreateBarangActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil input data")
                            .setNeutralButton("OK") {_,_->
                                merek.setText("")
                                nameProduk.setText("")
                                hargaProduk.setText("")
                                stokProduk.setText("")
                                tipeProduk.setText("")
                                val intent=Intent(this@CreateBarangActivity,MenuAdminActivity::class.java)
                                intent.putExtra("back","barang")
                                startActivity(intent)
                            }
                            .create()
                            .show()
                    }
                }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }
}