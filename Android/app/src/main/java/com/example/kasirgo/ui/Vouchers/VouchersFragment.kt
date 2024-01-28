package com.example.kasirgo.ui.Vouchers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListVoucer
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.databinding.FragmentVouchersBinding
import com.example.kasirgo.item.itemVoucer
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class VouchersFragment : Fragment() {

    private var _binding: FragmentVouchersBinding? = null

    private val binding get() = _binding!!
    private lateinit var recyclerView:RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentVouchersBinding.inflate(inflater, container, false)
        val root: View = binding.root
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(),CreateVoucerActivity::class.java))
        }
        recyclerView=binding.rvListVoucer
        _GetVoucer()
        return root
    }

    private fun _GetVoucer() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val datakarlist= mutableListOf<itemVoucer>()
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/voucer").openConnection() as HttpURLConnection
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
                        var statusVoucer=""
                        val jsonVoucer = JSONObject(body!!)
                        val dataVoucer=jsonVoucer.getJSONArray("Data")
                           for(i in 0 until dataVoucer.length()){
                               val jsonObject=dataVoucer.getJSONObject(i)
                               val id=jsonObject.getString("ID")
                               val codeVoucer=jsonObject.getString("code")
                               val discount=formatIDR(jsonObject.getString("discount").toDouble())
                               val status=jsonObject.getString("is_active")
                               if (status=="false"){
                                   statusVoucer="Tidak Aktif"
                               }else{
                                   statusVoucer="Aktif"
                               }
                               datakarlist.add(itemVoucer(id,codeVoucer,discount,statusVoucer))
                           }
                           val adapter= AdapterListVoucer(datakarlist!!,requireContext(),lifecycleScope)
                           recyclerView.adapter=adapter
                    }
                } catch (e: Exception) {
                    Log.e("Error ", e.toString())
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}