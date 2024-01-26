package com.example.kasirgo.ui.Vouchers

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListKaryawan
import com.example.kasirgo.AdapterRV.AdapterListVoucer
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SharePref.Companion.setAuth
import com.example.kasirgo.databinding.FragmentVouchersBinding
import com.example.kasirgo.item.itemKaryawan
import com.example.kasirgo.item.itemVoucer
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

class VouchersFragment : Fragment() {

    private var _binding: FragmentVouchersBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
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

                    requireContext().getAuth()?.let {
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
                        var statusVoucer=""
                        val jsonVoucer = JSONObject(body!!)
                        val dataVoucer=jsonVoucer.getJSONArray("Data")
                        Log.e("dataVoucer",dataVoucer.toString())
                           for(i in 0 until dataVoucer.length()){
                               val jsonObject=dataVoucer.getJSONObject(i)
                               val id=jsonObject.getString("ID")
                               val codeVoucer=jsonObject.getString("code")
                               val discount=jsonObject.getString("discount")
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
    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}