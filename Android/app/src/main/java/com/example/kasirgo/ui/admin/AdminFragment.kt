package com.example.kasirgo.ui.admin

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdaterListAdmin
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.databinding.FragmentAdminBinding
import com.example.kasirgo.item.itemAdmin
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class AdminFragment : Fragment() {

    private var _binding: FragmentAdminBinding? = null
    private val binding get() = _binding!!
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView=binding.rvListAdmin
        _GetKaryawan()
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(),CreateAdminActivity::class.java))
        }

        return root
    }

    private fun _GetKaryawan() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val datakarlist= mutableListOf<itemAdmin>()
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/admin").openConnection() as HttpURLConnection
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
                    val dataKaryawan=jsonKaryawan.getJSONArray("Data")
                    for(i in 0 until dataKaryawan.length()){
                        val jsonObject=dataKaryawan.getJSONObject(i)
                        val id=jsonObject.getString("ID")
                        val nama=jsonObject.getString("nama_karyawan")
                        val nik=jsonObject.getString("nik")
                        datakarlist.add(itemAdmin(id,nama,nik))
                    }
                    val adapter=AdaterListAdmin(datakarlist!!,requireContext(),lifecycleScope)
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