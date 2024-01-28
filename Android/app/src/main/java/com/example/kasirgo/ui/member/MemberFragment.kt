package com.example.kasirgo.ui.member

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterListMember
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.databinding.FragmentMemberBinding
import com.example.kasirgo.item.itemMember
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class MemberFragment : Fragment() {

    private var _binding: FragmentMemberBinding? = null
    private lateinit var recyclerView: RecyclerView
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentMemberBinding.inflate(inflater, container, false)
        val root: View = binding.root
        recyclerView=binding.rvLisMember
        binding.btnAdd.setOnClickListener {
            startActivity(Intent(requireContext(),CreateMemberActivity::class.java))
        }
        _GetMember()
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun _GetMember() {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                val datakarlist= mutableListOf<itemMember>()
                val conn =
                    URL("${BaseAPI.BaseAPI}/api/members").openConnection() as HttpURLConnection
                conn.requestMethod = "GET"
                conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
                conn.setRequestProperty("Content-Type", "application/json")
                val code = conn.responseCode
                val body = if (code in 200 until 300) {
                    conn.inputStream?.bufferedReader()?.use { it.readLine() }
                } else {
                    conn.errorStream?.bufferedReader()?.use { it.readLine() }
                }


                withContext(Dispatchers.Main) {
                    val jsonKaryawan = JSONObject(body!!)
                    Log.e("json",jsonKaryawan.toString())
                    val dataKaryawan=jsonKaryawan.getJSONArray("Data")
                    for(i in 0 until dataKaryawan.length()){
                        val jsonObject=dataKaryawan.getJSONObject(i)
                        val id=jsonObject.getString("ID")
                        val nama=jsonObject.getString("name")
                        val code_member=jsonObject.getString("code_member")
                        val alamat=jsonObject.getString("address")
                        val phone=jsonObject.getString("phone")
                        val point=jsonObject.getString("point")
                        datakarlist.add(itemMember(id,nama,alamat,phone,code_member,point))
                    }
                    val adapter= AdapterListMember(datakarlist!!,requireContext(),lifecycleScope)
                    recyclerView.adapter=adapter
                }
            }
        }
    }


}