package com.example.kasirgo.ui.barang

import android.app.Instrumentation.ActivityResult
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListBarang
import com.example.kasirgo.AdapterRV.AdapterListKaryawan
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.FragmentBarangBinding
import com.example.kasirgo.item.itemBarang
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class BarangFragment : Fragment() {

    private var _binding: FragmentBarangBinding? = null
    private lateinit var recyclerView:RecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentBarangBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView=binding.rvBarang

        binding.btnAddBarang.setOnClickListener {
            startActivity(Intent(requireContext(),ScanBCBarangActivity::class.java))
        }
        getData("")

        binding.tiFilter.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                getData(binding.tiFilter.text.toString())
            }

            override fun afterTextChanged(s: Editable?) {

            }

        })

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun getData(nama:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                val databaranglist= mutableListOf<itemBarang>()
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/barang?nama=${nama}").openConnection() as HttpURLConnection
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
                    Log.e("data",dataKaryawan.toString())
                    for(i in 0 until dataKaryawan.length()){
                        val jsonObject=dataKaryawan.getJSONObject(i)
                        val id=jsonObject.getString("ID")
                        val nama=jsonObject.getString("name")
                        val codebarang=jsonObject.getString("code_barang")
                        val price=jsonObject.getString("price")
                        databaranglist.add(itemBarang(id,nama,price,codebarang))
                    }
                    val adapter= AdapterListBarang(databaranglist!!,requireContext(),lifecycleScope)
                    recyclerView.adapter=adapter
                }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }
}