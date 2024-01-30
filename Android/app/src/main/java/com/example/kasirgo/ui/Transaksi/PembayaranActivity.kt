package com.example.kasirgo.ui.Transaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.Util.SharePreftTransaksi
import com.example.kasirgo.databinding.ActivityPembayaranBinding
import com.example.kasirgo.ui.SplashScreen.SuccessSplashActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale
import kotlin.properties.Delegates

class PembayaranActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPembayaranBinding
    private var totalPembayaran = MutableLiveData<Int>()
    private val readPembayaran: LiveData<Int> get() = totalPembayaran
    private var totalKembalian: Int = 0
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPembayaranBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val totalPembelian = SharePreftTransaksi().totalPrice
        binding.tvTotalPembelian.text = formatIDR(totalPembelian.toDouble())

        binding.btnSubmitPembayaran.setOnClickListener {
            if (binding.tiPembayaran.text!!.isEmpty()) {
                binding.tiPembayaran.error = "Form Jumlah pembayaran masih kosong"
            } else {
                val input = binding.tiPembayaran.text?.toString()
                val inputBersih = input?.replace(Regex("[^0-9]"), "")

                try {
                    val hasil = inputBersih?.toInt()
                    if (hasil != null) {
                        totalPembayaran.value = hasil!!
                    } else {
                        Toast.makeText(this, "Input tidak valid", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: NumberFormatException) {
                    Log.e("test", "Terjadi kesalahan saat mengonversi ke integer")
                }
                readPembayaran.observe(this) {
                    if (it < totalPembelian) {
                        binding.tiPembayaran.error = "Jumlah Pembayaran Kurang"
                        binding.tvKembalian.text = "-"
                        binding.tvTotalPembayaran.text = "-"
                    } else {
                        binding.tvTotalPembayaran.text = formatIDR(it.toDouble())
                    }
                }
            }
        }
        readPembayaran.observe(this) {
            totalKembalian = it - totalPembelian
            binding.tvKembalian.text = formatIDR(totalKembalian.toDouble())
        }

        binding.btnSubmitPembelian.setOnClickListener {
            if (binding.tiPembayaran.text!!.isNotEmpty()) {
                Log.e("Total", totalKembalian.toString())
                _AddTransaksi(totalKembalian)
            }
        }
    }


    private fun formatIDR(nominal: Double): String {

        val localeID = Locale("id", "ID")

        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        formatRupiah.currency = Currency.getInstance("IDR")

        val hasilFormat = formatRupiah.format(nominal)

        return hasilFormat
    }

    private fun _AddTransaksi(kembalian: Int) {
        binding.PBSubmit.isVisible=true
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val codeVoucher = SharePreftTransaksi().codeVoucer
                    val idKaryawan = SharePreftLogin.id_user
                    val idMember = SharePreftTransaksi().member_id
                    val point = SharePreftTransaksi().point
                    val ppn = SharePreftTransaksi().ppn
                    val totalPembayaran = SharePreftTransaksi().totalPrice
                    val nominal = binding.tiPembayaran.text.toString()


                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/transaksi").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    OutputStreamWriter(conn.outputStream).use {
                        it.write(JSONObject().apply {
                            put("code_voucer", codeVoucher)
                            put("karyawan_id", idKaryawan.toInt())
                            put("kembalian", kembalian)
                            put("member_id", idMember)
                            put("nominal_pembayaran", nominal.toInt())
                            put("point", point)
                            put("ppn", ppn)
                            put("total_price", totalPembayaran)
                        }.toString())
                    }
                    val code = conn.responseCode
                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }
                    withContext(Dispatchers.Main) {
                        val json=JSONObject(body)
                        if (code !in 200 until 300) {
                            if (code == 400) {
                                Toast.makeText(
                                    this@PembayaranActivity,
                                    "Transaksi Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@PembayaranActivity,
                                    "Transaksi Error",
                                    Toast.LENGTH_SHORT
                                )
                                    .show()
                            }
                        } else {
                            val id_member = SharePreftTransaksi().member_id
                            val transaksiData=json.getJSONObject("Data")
                            val idTransaksi=transaksiData.getString("ID")
                            if (id_member != 0) {
                                _AddPoint(id_member.toString())
                            }
                            _AddBarang(idTransaksi)
                            binding.PBSubmit.isVisible=false
                            startActivity(
                                Intent(
                                    this@PembayaranActivity,
                                    SuccessSplashActivity::class.java
                                )
                            )
                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("Error Http", e.message.toString())
                }
            }

        }

    }

    private fun _AddPoint(idMember: String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val totalPembayaran = SharePreftTransaksi().totalPrice
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/transaksi/calculatePoint/$idMember").openConnection() as HttpURLConnection
                    conn.requestMethod = "POST"
                    conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    OutputStreamWriter(conn.outputStream).use {
                        it.write(JSONObject().apply {
                            put("total_price", totalPembayaran)
                        }.toString())
                    }
                    val code = conn.responseCode
                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }
                    withContext(Dispatchers.Main) {
                        if (code !in 200 until 300) {
                            if (code == 400) {
                                Toast.makeText(
                                    this@PembayaranActivity,
                                    "TransaksiError",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@PembayaranActivity,
                                    "Transaksi Error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        } else {

                        }
                    }
                } catch (e: java.lang.Exception) {
                    Log.e("Error Http", e.message.toString())
                }
            }

        }

    }

    private fun _AddBarang(idTransaksi: String) {
        val ids = CartSharePreft(this).getId()
        val counts = CartSharePreft(this).getCount()
        val prices = CartSharePreft(this).getPrice()
        val idCountPriceMap = mutableMapOf<String, Pair<String, String>>()

        for (index in ids.indices) {
            val id = ids[index]
            val countValue = counts[index]
            val priceValue = prices[index]
            idCountPriceMap[id] = Pair(countValue, priceValue)
        }

        lifecycleScope.launch {
            withContext(Dispatchers.IO) {
                try {
                    val apiUrl = "${BaseAPI.BaseAPI}/api/transaksi/addBarangTransaksi"
                    for ((id, pair) in idCountPriceMap) {
                        val countValue = pair.first
                        val priceValue = pair.second

                        val conn = URL(apiUrl).openConnection() as HttpURLConnection
                        conn.requestMethod = "POST"
                        conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                        conn.doOutput = true
                        conn.setRequestProperty("Content-Type", "application/json")

                        OutputStreamWriter(conn.outputStream).use { writer ->
                            writer.write(
                                JSONObject().apply {
                                    put("barang_id", id)
                                    put("quantity", countValue.toInt())
                                    put("subTotalHarga", priceValue.toInt())
                                    put("transaksi_id", idTransaksi.toInt())
                                }.toString()
                            )
                        }
                        val code = conn.responseCode
                        val body = if (code in 200 until 300) {
                            conn.inputStream?.bufferedReader()?.use { it.readLine() }
                        } else {
                            conn.errorStream?.bufferedReader()?.use { it.readLine() }
                        }

                        withContext(Dispatchers.Main) {
                            if (code !in 200 until 300) {
                                if (code == 400) {
                                    Toast.makeText(
                                        this@PembayaranActivity,
                                        "TransaksiError",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                } else {
                                    Toast.makeText(
                                        this@PembayaranActivity,
                                        "Transaksi Error",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            } else {
                            }
                        }
                    }
                } catch (e: Exception) {
                    Log.e("Error Http", e.message.toString())
                }
            }
        }
    }
}