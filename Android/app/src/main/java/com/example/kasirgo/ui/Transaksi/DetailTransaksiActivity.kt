package com.example.kasirgo.ui.Transaksi

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListBarangPembelian
import com.example.kasirgo.AdapterRV.AdapterListTransaksi
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.databinding.ActivityDetailTransaksiBinding
import com.example.kasirgo.item.itemPembelianBarang
import com.example.kasirgo.item.itemTransaksi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class DetailTransaksiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityDetailTransaksiBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityDetailTransaksiBinding.inflate(layoutInflater)
        recyclerView=binding.rvcartBarangPembelian
        recyclerView.layoutManager= LinearLayoutManager(this)
        setContentView(binding.root)
        _GetKaryawan()

        binding.btnRowBack.setOnClickListener {
            val intent = Intent(this, MenuKasirActivity::class.java)
            intent.putExtra("status","history")
            startActivity(intent)
        }
    }

    private fun _GetKaryawan() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val karyawan_id= SharePreftLogin.id_user
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/transaksi/$karyawan_id").openConnection() as HttpURLConnection
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
                    var idTransaksi:String=""
                    val jsonKaryawan = JSONObject(body!!)
                    val dataKaryawan=jsonKaryawan.getJSONArray("Data")
                    for(i in 0 until dataKaryawan.length()){
                        val jsonObject=dataKaryawan.getJSONObject(i)
                        val id=jsonObject.getString("ID")
                        val nominalPembayaran=jsonObject.getString("nominal_pembayaran")
                        val totalTransaksi=jsonObject.getString("total_price")
                        val ppn=jsonObject.getString("ppn")
                        val nominalKembalian=jsonObject.getString("kembalian")
                        val namaKaryawan=jsonObject.getString("karyawan_name")
                        val codeVoucher=jsonObject.getString("code_voucer")
                        val memberName=jsonObject.getString("member_name")
                        idTransaksi=id
                        binding.tvNamaKaryawanDetailTransaksi.text=namaKaryawan
                        binding.tvTotalTransaksiDetailTransaksi.text=formatIDR(totalTransaksi.toDouble())
                        binding.tvNominalPembayaranDetailTransaksi.text=formatIDR(nominalPembayaran.toDouble())
                        binding.tvNominalKembalianDetailTransaksi.text=formatIDR(nominalKembalian.toDouble())
                        binding.tvMemberNameDetailTransaksi.isVisible = memberName != ""
                        binding.tvCodeVoucherDetailTransaksi.isVisible = codeVoucher != ""
                        binding.tvMemberNameDetailTransaksi.text=memberName
                        binding.tvCodeVoucherDetailTransaksi.text=memberName
                        binding.tvNominalPPnDetailTransaksi.text=formatIDR(ppn.toDouble())
                    }
                    _GetBarang(idTransaksi)
                }
            }
        }
    }
    private fun _GetBarang(id:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val dataList= mutableListOf<itemPembelianBarang>()
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/transaksi/barangTransaksi/$id").openConnection() as HttpURLConnection
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
                    val jsonKaryawan = JSONObject(body!!)
                    Log.e("transaksi",jsonKaryawan.toString())
                    val dataBarang=jsonKaryawan.getJSONArray("Data")
                    for(i in 0 until dataBarang.length()){
                        val barang=dataBarang.getJSONObject(i)
                        val namaBarang=barang.getString("nama_barang")
                        val quantity=barang.getString("quantity")
                        val codebarang=barang.getString("code_barang")
                        val price=barang.getString("price")
                        dataList.add(itemPembelianBarang(id,namaBarang,price,codebarang,quantity))
                    }
                    val adapter= AdapterListBarangPembelian(dataList,this@DetailTransaksiActivity)
                    recyclerView.adapter=adapter
                }
            }
        }
    }

    private fun formatIDR(nominal:Double):String{

        val localeID = Locale("id", "ID")

        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        formatRupiah.currency = Currency.getInstance("IDR")

        val hasilFormat = formatRupiah.format(nominal)

        return hasilFormat
    }
}