package com.example.kasirgo.ui.Transaksi

import android.app.Dialog
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListBarangTransaksi
import com.example.kasirgo.AdapterRV.AdapterListKaryawan
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityTransaksiBinding
import com.example.kasirgo.item.itemCart
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransaksiBinding
    private lateinit var recyclerView: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView=binding.rvcartBarang
        getData()

        binding.btnRowBack.setOnClickListener {
            val intent= Intent(this@TransaksiActivity, MenuKasirActivity::class.java)
            intent.putExtra("back","barang")
            startActivity(intent)
        }



        val price=CartSharePreft(this).getPrice()
        var totalPrice=0
        for (price in price){
            totalPrice+=price.toInt()
        }
        val itemCount=CartSharePreft(this).getCount()
        var totalCount=0
        for (count in itemCount){
            totalCount+=count.toInt()
        }

        binding.tvCountCart.setText("(${ totalCount.toString()} Barang )")
        binding.tvCartPrice.setText(formatIDR(totalPrice.toDouble()))

        binding.btnAddVoucer.setOnClickListener {
            showModalSearchVoucer()
        }

    }

    private fun formatIDR(nominal:Double):String{

        val localeID = Locale("id", "ID")

        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        formatRupiah.currency = Currency.getInstance("IDR")

        val hasilFormat = formatRupiah.format(nominal)

        return hasilFormat
    }


    private fun showModalSearchVoucer() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_modal_search_voucer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.window?.setBackgroundDrawableResource(R.drawable.background_modal)
        }
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val buttonCancel=dialog.findViewById<Button>(R.id.btnCanceldVoucerModal)
        val buttonSearch=dialog.findViewById<Button>(R.id.btnSearchVoucerModal)
        val code=dialog.findViewById<EditText>(R.id.tiCodeVoucer)
        buttonSearch.setOnClickListener {
            if (code.text.isEmpty()){
                code.setError("Form Code Masih Kosong")
            }else{
                _GetKaryawan(code.text.toString())
                dialog.dismiss()
            }
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun showModalAddVoucher(code: String, totalVoucher: String, expDiskon: String, active: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_modal_add_voucer)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.window?.setBackgroundDrawableResource(R.drawable.background_modal)
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)

        val btnCancel = dialog.findViewById<Button>(R.id.btnCancelShowModal)
        val btnAdd = dialog.findViewById<Button>(R.id.btnAddVoucerModal)
        val tvCode = dialog.findViewById<TextView>(R.id.tvCodeVocher)
        val tvTotalDiskon = dialog.findViewById<TextView>(R.id.tvTotalDiskon)
        val tvExpDiskon = dialog.findViewById<TextView>(R.id.tvExpDiskon)
        val tvStatus = dialog.findViewById<TextView>(R.id.tvStatusDiskon)

        tvCode.text = code
        tvTotalDiskon.text = formatIDR(totalVoucher.toDouble())
        tvExpDiskon.text = expDiskon
        if (active.toBoolean()){
            tvStatus.text="Berlaku"
        }else{
            tvStatus.text="Tidak Berlaku"
        }

        btnAdd.setOnClickListener {
            binding.tvCodeVoucherTransaksi.text=code
            binding.tvTotalDiskomTransaksi.text=formatIDR(totalVoucher.toDouble())
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun _GetKaryawan(code:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/transaksi/voucher/$code").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"

                    getAuth()?.let {
                        conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                    }
                    conn.setRequestProperty("Content-Type", "application/json")
                    val code = conn.responseCode

                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }


                    withContext(Dispatchers.Main) {
                        if (code in 200 until 300){
                            val jsonVocher = JSONObject(body!!)/*
                              val voucherData=jsonVocher.getJSONObject("Data")
                            Log.e("json",voucherData.toString())
                            val codeVoucher=voucherData.getString("code")
                            val totalDiskon=voucherData.getString("discount")
                            val endDiskon=voucherData.getString("end_date")
                            val active=voucherData.getString("is_active")
                            showModalAddVoucher(codeVoucher,totalDiskon,endDiskon,active)*/
                            val voucherDataArray=jsonVocher.getJSONArray("Data")
                            for(i in 0 until voucherDataArray.length()){
                                val voucherData=voucherDataArray.getJSONObject(i)
                                val codeVoucher=voucherData.getString("code")
                                val totalDiskon=voucherData.getString("discount")
                                val endDiskon=voucherData.getString("end_date")
                                val active=voucherData.getString("is_active")
                                showModalAddVoucher(codeVoucher,totalDiskon,endDiskon,active)
                            }
                        }
                        else{
                            Toast.makeText(this@TransaksiActivity,"Code Voucer tidak di temukkan",Toast.LENGTH_SHORT).show()
                        }
                    }
                }catch (e:Exception){
                    Log.e("Error Htttp",e.message.toString())
                }
            }
        }
    }

    private fun getData() {
        lifecycleScope.launch {
            val dataCartList = mutableListOf<itemCart>()
            withContext(Dispatchers.IO) {
                try {
                    val ids = CartSharePreft(this@TransaksiActivity).getId()
                    Log.e("idCart", ids.toString())

                    for (dataId in ids) {
                        val conn =
                            URL("${BaseAPI.BaseAPI}/api/barang/WithQr/${dataId}").openConnection() as HttpURLConnection
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
                        val jsonBarang = JSONObject(body!!)
                        val dataBarang = jsonBarang.getJSONArray("Data")
                        Log.e("data", dataBarang.toString())

                        for (i in 0 until dataBarang.length()) {
                            val jsonObject = dataBarang.getJSONObject(i)
                            val id = jsonObject.getString("ID")
                            val nama = jsonObject.getString("name")
                            val codeBarang = jsonObject.getString("code_barang")
                            val price = jsonObject.getString("price")
                            dataCartList.add(itemCart("1", id, nama, price,price, codeBarang))
                        }
                        val count = CartSharePreft(this@TransaksiActivity).getCount()
                        dataCartList.mapIndexed { indexData, itemCart ->
                            count.mapIndexed { index, s ->
                                if (indexData==index){
                                    itemCart._Count=s
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            Log.e("database", dataCartList.toString())
                            val adapter = AdapterListBarangTransaksi(dataCartList, this@TransaksiActivity, this@TransaksiActivity)
                            recyclerView.adapter = adapter
                        }
                    }


                } catch (e: Exception) {
                    Log.e("Error ", e.toString())
                }
            }
        }
    }

}