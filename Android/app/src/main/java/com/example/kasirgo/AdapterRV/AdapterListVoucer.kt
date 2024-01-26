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
import com.example.kasirgo.item.itemKaryawan
import com.example.kasirgo.item.itemVoucer
import com.example.kasirgo.ui.Vouchers.UpdateVoucerActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AdapterListVoucer(val item:List<itemVoucer>, val context: Context, private val coroutineScope: CoroutineScope): RecyclerView.Adapter<AdapterListVoucer.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val code:TextView=view.findViewById(R.id.tvCodeVoucer)
        val discont:TextView=view.findViewById(R.id.tvPriceVoucer)
        val status:TextView=view.findViewById(R.id.tvStatusVoucer)
        val btnUpdate:ImageView=view.findViewById(R.id.btnUpdate)
        val btnDelete:ImageView=view.findViewById(R.id.btnDelete)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AdapterListVoucer.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_list_voucer, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items=item[position]
        holder.code.text=items.Code
        holder.discont.text=items.Discont
        holder.status.text=items.Status
        holder.btnUpdate.setOnClickListener {
            val intent=Intent(context,UpdateVoucerActivity::class.java)
            intent.putExtra("idvoucer",items.ID)
            context.startActivity(intent)
        }
        holder.btnDelete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Apakah anda ingin menghapus data ini?")
                .setPositiveButton("Yes") {_,_->
                    _DeleteDataKaryawan(items.ID)
                }
                .setNegativeButton("No"){_,_->}
                .create()
                .show()
        }
    }

    private fun _DeleteDataKaryawan(id:String) {
        coroutineScope.launch() {
            withContext(Dispatchers.IO) {
                Log.e("id",id)
                try {
                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/voucer/${id}").openConnection() as HttpURLConnection
                    conn.requestMethod = "DELETE"

                    context.getAuth()?.let {
                        conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                    }
                    conn.setRequestProperty("Content-Type", "application/json")

                    val code = conn.responseCode
                    Log.e("delete", code.toString())

                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }
                    withContext(Dispatchers.Main) {
                        Log.e("delete",body.toString())
                        if (code in 200 until 300) {
                            val intent=Intent(context, MenuAdminActivity::class.java)
                            intent.putExtra("back","voucer")
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