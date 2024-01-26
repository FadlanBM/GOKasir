package com.example.kasirgo.MenuFragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.activity.OnBackPressedCallback
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.AdapterRV.AdapterCart
import com.example.kasirgo.LoginActivity
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.Util.SwipeToDeleteCallback
import com.example.kasirgo.item.itemCart
import com.example.kasirgo.ui.barang.ScanBCBarangActivity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

class KasirFragment : Fragment() {
    private lateinit var recyclerView: RecyclerView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_kasir, container, false)
        view.findViewById<Button>(R.id.btnScanQR).setOnClickListener {
            val intent = Intent(requireContext(), ScanBCBarangActivity::class.java)
            intent.putExtra("status", "addCart")
            startActivity(intent)
        }

        val addTransaksi:Button=view.findViewById(R.id.btn_Transaksi)
        val ids=CartSharePreft(requireContext()).getId()
        if (ids.isEmpty()){
            addTransaksi.isVisible=false
        }
/*
        CartSharePreft(requireContext()).clearCart()
*/

        recyclerView = view.findViewById(R.id.rvcartBarang)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        val swipeToDeleteCallback = object : SwipeToDeleteCallback() {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                CartSharePreft(requireContext()).deleteData(position)
                val ids=CartSharePreft(requireContext()).getId()
                if (ids.isEmpty()){
                    addTransaksi.isVisible=false
                }
                getData(view)
            }
        }
        val itemTouchHelper = ItemTouchHelper(swipeToDeleteCallback)
        itemTouchHelper.attachToRecyclerView(recyclerView)

        getData(view)

        return view
    }


    private fun getData(view: View) {
        lifecycleScope.launch {
            val dataCartList = mutableListOf<itemCart>()
            withContext(Dispatchers.IO) {
                try {
                    val ids = CartSharePreft(requireContext()).getId()
                    Log.e("idCart", ids.toString())

                    for (dataId in ids) {
                        val conn =
                            URL("${BaseAPI.BaseAPI}/api/barang/WithQr/${dataId}").openConnection() as HttpURLConnection
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
                        val count = CartSharePreft(requireContext()).getCount()
                        dataCartList.mapIndexed { indexData, itemCart ->
                            count.mapIndexed { index, s ->
                                if (indexData==index){
                                    itemCart._Count=s
                                }
                            }
                        }
                        withContext(Dispatchers.Main) {
                            Log.e("database", dataCartList.toString())
                            val adapter = AdapterCart(dataCartList, requireContext(), lifecycleScope, viewLifecycleOwner,view)
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
