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
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListBarangTransaksi
import com.example.kasirgo.AdapterRV.AdapterListMember
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SharePreftTransaksi
import com.example.kasirgo.databinding.ActivityTransaksiBinding
import com.example.kasirgo.item.itemCart
import com.example.kasirgo.item.itemMember
import com.example.kasirgo.library.ExceptionMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import org.w3c.dom.Text
import java.io.OutputStreamWriter
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class TransaksiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransaksiBinding
    private lateinit var recyclerView: RecyclerView
    private var voucherTotal= MutableLiveData<Int>()
    private val readVoucher: LiveData<Int> get() = voucherTotal
    private var memberTotal= MutableLiveData<Int>()
    private val readMember: LiveData<Int> get() = memberTotal
    private var TotalOb= MutableLiveData<Int>()
    private val readTotal: LiveData<Int> get() = TotalOb
    private var idMembermut= MutableLiveData<Int>()
    private val readidMember: LiveData<Int> get() = idMembermut
    private var pointOb= MutableLiveData<Int>()
    private val readPoint: LiveData<Int> get() = pointOb
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)

        recyclerView = binding.rvcartBarang
        getData()

        binding.btnRowBack.setOnClickListener {
            val intent = Intent(this@TransaksiActivity, MenuKasirActivity::class.java)
            intent.putExtra("back", "barang")
            startActivity(intent)
        }

        val priceList = CartSharePreft(this).getPrice()
        var totalPrice = 0

        for (price in priceList) {
            totalPrice += price.toInt()
        }

        val itemCountList = CartSharePreft(this).getCount()
        var totalCount = 0

        for (count in itemCountList) {
            totalCount += count.toInt()
        }

        binding.tvCountCart.text = "( ${totalCount} Barang )"
        binding.tvCartPrice.text = formatIDR(totalPrice.toDouble())

        val ppn = (totalPrice * 0.10).toInt()
        binding.tvPpnTransaksi.text = formatIDR(ppn.toDouble())

        var member=0
        var price=0
        readMember.observe(this) {
            member = it
            TotalOb.value=totalPrice + ppn - member - price
        }
        readVoucher.observe(this) {
            price = it
            TotalOb.value=totalPrice + ppn - member - price
        }
        TotalOb.value=totalPrice + ppn - member - price

        readTotal.observe(this){
            binding.tvtotalPembelanjaan.text = formatIDR(it.toDouble())
        }

        binding.btnAddVoucer.setOnClickListener {
            showModalSearchVoucer()
        }

        binding.btnAddMember.setOnClickListener {
            showModalAuthMember()
        }

        binding.btnAddTransaksi.setOnClickListener {
            readidMember.observe(this){
                SharePreftTransaksi().member_id=it
            }
            readPoint.observe(this){
                SharePreftTransaksi().point=it
            }
            SharePreftTransaksi().ppn=ppn
            readTotal.observe(this){
            SharePreftTransaksi().totalPrice=it
            }
            startActivity(Intent(this,PembayaranActivity::class.java))
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
                _GetVoucher(code.text.toString(),dialog)
            }
        }
        buttonCancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()

    }
    private fun showModalAuthMember() {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_modal_auth_member)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.window?.setBackgroundDrawableResource(R.drawable.background_modal)
        }
        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val buttonCancel=dialog.findViewById<Button>(R.id.btnCanceldMemberModal)
        val buttonValidasi=dialog.findViewById<Button>(R.id.btnValidasiMemberModal)
        val tvCode=dialog.findViewById<EditText>(R.id.tiCodeMemberTran)
        val tvPassword=dialog.findViewById<EditText>(R.id.tiPasswordMemberTran)
        buttonValidasi.setOnClickListener {
            _CreateMember(tvCode.text.toString(),tvPassword.text.toString(),dialog)
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
            binding.tvTotalDiskomTransaksi.text="- ${formatIDR(totalVoucher.toDouble())}"
            voucherTotal.value=totalVoucher.toInt()
            dialog.dismiss()
        }

        btnCancel.setOnClickListener {
            dialog.dismiss()
        }

        dialog.show()
    }
    private fun showModalAddMember(code: String, nama: String, alamat: String, point: String) {
        val dialog = Dialog(this)
        dialog.setContentView(R.layout.layout_modal_add_member)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.window?.setBackgroundDrawableResource(R.drawable.background_modal)
        }

        dialog.window?.setLayout(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT)
        val tvnama=dialog.findViewById<TextView>(R.id.tvNamaMember)
        val tvcode=dialog.findViewById<TextView>(R.id.tvCodeMember)
        val tvalamat=dialog.findViewById<TextView>(R.id.tvAlamatMember)
        val tvpoint=dialog.findViewById<TextView>(R.id.tvTotalPointMember)
        val cancel=dialog.findViewById<Button>(R.id.btnCancelModalMember)
        val add=dialog.findViewById<Button>(R.id.btnAddMember)
        tvnama.text=nama
        tvcode.text=code
        tvalamat.text=alamat
        tvpoint.text=point

        add.setOnClickListener {
            binding.tvTotalPointMemberTran.text="- ${formatIDR(point.toDouble())}"
            binding.tvNameMemberTransaksi.text=nama
            binding.tvCodeMemberTransaksi.text=code
            pointOb.value=point.toInt()
            dialog.dismiss()
        }
        cancel.setOnClickListener {
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun _GetVoucher(code:String,dialog: Dialog) {
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
                            val jsonVocher = JSONObject(body!!)
                            val voucherDataArray=jsonVocher.getJSONArray("Data")
                            for(i in 0 until voucherDataArray.length()){
                                val voucherData=voucherDataArray.getJSONObject(i)
                                val codeVoucher=voucherData.getString("code")
                                val totalDiskon=voucherData.getString("discount")
                                val endDiskon=voucherData.getString("end_date")
                                val active=voucherData.getString("is_active")
                                showModalAddVoucher(codeVoucher,totalDiskon,endDiskon,active)
                                SharePreftTransaksi().codeVoucer=codeVoucher
                            }
                            dialog.dismiss()
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
    private fun _CreateMember(code:String,password:String,dialog:Dialog) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/transaksi/addmember").openConnection() as HttpURLConnection
                conn.requestMethod = "POST"

                getAuth()?.let {
                    conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                }
                conn.doOutput = true
                conn.setRequestProperty("Content-Type", "application/json")
                OutputStreamWriter(conn.outputStream).use {
                    it.write(JSONObject().apply {
                        put("code",code)
                        put("password",password)
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
                    val jsonMember = JSONObject(body!!)
                    if (code in 200 until 300){
                        val member=jsonMember.getJSONObject("Data")
                        val idMember=member.getString("ID")
                        val code=member.getString("code_member")
                        val nama_member=member.getString("name")
                        val alamat=member.getString("address")
                        val point=member.getString("point")
                        showModalAddMember(code,nama_member,alamat,point)
                        idMembermut.value=idMember.toInt()
                    }
                    dialog.dismiss()
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
                            URL("${BaseAPI.BaseAPI}/api/transaksi/WithQr/${dataId}").openConnection() as HttpURLConnection
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