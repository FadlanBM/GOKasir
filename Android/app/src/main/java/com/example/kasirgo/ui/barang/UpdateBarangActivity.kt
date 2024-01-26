package com.example.kasirgo.ui.barang

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.IdKoneksi
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityUpdateBarangBinding
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

class UpdateBarangActivity : AppCompatActivity() {
    private lateinit var binding:ActivityUpdateBarangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityUpdateBarangBinding.inflate(layoutInflater)
        setContentView(binding.root)



        binding.btnUpdateQR.setOnClickListener {
            val intent=Intent(this,ScanBCBarangActivity::class.java)
            intent.putExtra("status","updateBarang")
            startActivity(intent)
        }

        binding.btnSubmit.setOnClickListener {
            _UpdateBarang(IdKoneksi.idbarang)
        }
        val codeBarang=intent.getStringExtra("CodeBarang")
        val id=intent.getStringExtra("idBarang")
        if (id==null){
            _GetDataBarang(IdKoneksi.idbarang,codeBarang.toString())
        }
        else{
            _GetDataBarang(id.toString(),"")
        }

        binding.btnRowBack.setOnClickListener {
            val intent=Intent(this@UpdateBarangActivity,MenuAdminActivity::class.java)
            intent.putExtra("back","barang")
            startActivity(intent)
        }
    }

    override fun onBackPressed() {
        val intent=Intent(this@UpdateBarangActivity,MenuAdminActivity::class.java)
        intent.putExtra("back","barang")
        startActivity(intent)
        super.onBackPressed()
    }

    private fun _UpdateBarang(id:String) {
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
                val timerekBarang=binding.tiMerek
                val tinameBarang=binding.tiNamaProduk
                val tihargaBarang=binding.tiHargaProduk
                val tistokBarang=binding.tiStokProduk
                val tipeBarang=binding.tiTipeProduk
                val tiCodeBarang=binding.tiCodeBarang

                if (timerekBarang.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form merek barang masih kosong")
                if (tinameBarang.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form nama produk masih kosong")
                if (tihargaBarang.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form harga produk kosong")
                if (tistokBarang.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form stok produk masih kosong")
                if (tipeBarang.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form tipe produk Petugas masih kosong")

                val conn =
                    URL("${BaseAPI.BaseAPI}/api/barang/${id}").openConnection() as HttpURLConnection
                conn.requestMethod = "PUT"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("code",tiCodeBarang.text.toString())
                        put("merek",timerekBarang.text.toString())
                        put("name",tinameBarang.text.toString())
                        put("price",tihargaBarang.text.toString().toFloat() )
                        put("stok",tistokBarang.text.toString().toInt() )
                        put("tipe",tipeBarang.text.toString() )
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
                        AlertDialog.Builder(this@UpdateBarangActivity)
                            .setTitle("Information")
                            .setMessage("Berhasil input data")
                            .setNeutralButton("OK") {_,_->
                                timerekBarang.setText("")
                                tinameBarang.setText("")
                                tihargaBarang.setText("")
                                tistokBarang.setText("")
                                tipeBarang.setText("")
                                val intent=Intent(this@UpdateBarangActivity, MenuAdminActivity::class.java)
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
    private fun _GetDataBarang(id:String,codeBarangQr:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val timerekBarang=binding.tiMerek
                    val tinameBarang=binding.tiNamaProduk
                    val tihargaBarang=binding.tiHargaProduk
                    val tistokBarang=binding.tiStokProduk
                    val titipeBarang=binding.tiTipeProduk
                    val ticodeBarang=binding.tiCodeBarang

                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/barang/${id}").openConnection() as HttpURLConnection
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
                        val dataBarang=karyawanJson.getJSONObject("Data")
                        val merekbarang=dataBarang.getString("merek")
                        val idbarang=dataBarang.getString("ID")
                        val namabarang=dataBarang.getString("name")
                        val hargabarang=dataBarang.getString("price")
                        val stockbarang=dataBarang.getString("stock")
                        val tipebarang=dataBarang.getString("tipe")
                        val codeBarang=dataBarang.getString("code_barang")
                        IdKoneksi.idbarang=idbarang
                        timerekBarang.setText(merekbarang)
                        tinameBarang.setText(namabarang)
                        tihargaBarang.setText(hargabarang)
                        tistokBarang.setText(stockbarang)
                        titipeBarang.setText(tipebarang)
                        if (codeBarangQr.length!=0){
                            Log.e("code barang",codeBarangQr.length.toString())
                            ticodeBarang.setText(codeBarangQr)
                        }else{
                            ticodeBarang.setText(codeBarang)
                        }
                    }
                }catch (e:Exception){
                 Log.e("Error ",e.toString())
                }
            }
        }
    }
}