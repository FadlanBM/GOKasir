package com.example.kasirgo.ui.karyawan

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListKaryawan
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.FragmentKaryawanBinding
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class KaryawanFragment : Fragment() {

    private var _binding: FragmentKaryawanBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentKaryawanBinding.inflate(inflater, container, false)
        val root: View = binding.root
        _GetKaryawan()
        recyclerView=binding.rvListKaryawan
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(),CreateKaryawanActivity::class.java))
        }

        return root
    }

    private fun _GetKaryawan() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val datakarlist= mutableListOf<itemKaryawan>()
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/karyawan").openConnection() as HttpURLConnection
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
                    val jsonKaryawan = JSONObject(body!!)
                    val dataKaryawan=jsonKaryawan.getJSONArray("Data")
                    for(i in 0 until dataKaryawan.length()){
                        val jsonObject=dataKaryawan.getJSONObject(i)
                        val id=jsonObject.getString("ID")
                        val nama=jsonObject.getString("nama_karyawan")
                        val nik=jsonObject.getString("nik")
                        datakarlist.add(itemKaryawan(id,nama,nik))
                    }
                    val adapter=AdapterListKaryawan(datakarlist!!,requireContext(),lifecycleScope)
                    recyclerView.adapter=adapter
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}