package com.example.kasirgo.MenuFragment

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListTransaksi
import com.example.kasirgo.AdapterRV.AdaterListAdmin
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePreftLogin
import com.example.kasirgo.item.itemAdmin
import com.example.kasirgo.item.itemTransaksi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
class RiwayatFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view=inflater.inflate(R.layout.fragment_riwayat, container, false)
        recyclerView=view.findViewById(R.id.recyclerViewListDataTransaksi)
        recyclerView.layoutManager=LinearLayoutManager(requireContext())
        _GetKaryawan()
        return view
    }
    private fun _GetKaryawan() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val daTalist= mutableListOf<itemTransaksi>()
                val karyawan_id=SharePreftLogin.id_user
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
                    val jsonKaryawan = JSONObject(body!!)
                    Log.e("transaksi",jsonKaryawan.toString())
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
                        Log.e("membername",memberName)
                        daTalist.add(itemTransaksi(id,nominalPembayaran,totalTransaksi,ppn,nominalKembalian,namaKaryawan,codeVoucher,memberName))
                    }
                    val adapter= AdapterListTransaksi(daTalist,requireContext())
                    recyclerView.adapter=adapter
                }
            }
        }
    }

}