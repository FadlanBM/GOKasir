package com.example.kasirgo.AdapterRV

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.item.itemBarang
import com.example.kasirgo.ui.barang.UpdateBarangActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AdapterListBarang(val item:List<itemBarang>, val context: Context, private val coroutineScope: CoroutineScope):RecyclerView.Adapter<AdapterListBarang.ViewHolder>() {
    class ViewHolder(view:View):RecyclerView.ViewHolder(view) {
        val namaBarang:TextView=view.findViewById(R.id.tvNamaBarang)
        val PriceBarang:TextView=view.findViewById(R.id.tvPriceBarang)
        val CodeBarang:TextView=view.findViewById(R.id.tvCodeBarang)
        val btnUpdate:ImageView=view.findViewById(R.id.btnUpdate)
        val btnDelete:ImageView=view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_barang,parent,false))
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items=item[position]
        holder.namaBarang.text=items._NamaBarang
        holder.PriceBarang.text=items._Price
        holder.CodeBarang.text=items._CodeBarang
        holder.btnUpdate.setOnClickListener {
            val intent=Intent(context,UpdateBarangActivity::class.java)
            intent.putExtra("idBarang",items._ID)
            context.startActivity(intent)
        }
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Apakah anda ingin menghapus data ini?")
                .setPositiveButton("Yes") {_,_->
                    _DeleteBarang(items._ID)
                }
                .setNegativeButton("No"){_,_->}
                .create()
                .show()
        }
    }

    private fun _DeleteBarang(id:String) {
        coroutineScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/barang/${id}").openConnection() as HttpURLConnection
                    conn.requestMethod = "DELETE"

                    context.getAuth()?.let {
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
                        if (code in 200 until 300) {
                            val intent=Intent(context, MenuAdminActivity::class.java)
                            intent.putExtra("back","barang")
                            context.startActivity(intent)
                        }
                    }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }
}