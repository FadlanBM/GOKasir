package com.example.kasirgo.AdapterRV

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.item.itemMember
import com.example.kasirgo.ui.member.UpdateMemberActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL

class AdapterListMember(val item:List<itemMember>, val context: Context, private val coroutineScope: CoroutineScope):RecyclerView.Adapter<AdapterListMember.ViewHolder>() {
        class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val nama:TextView=view.findViewById(R.id.tvName)
        val code_member:TextView=view.findViewById(R.id.tvCode)
        val update:Button=view.findViewById(R.id.btnUpdate)
        val delete:Button=view.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_member,parent,false))
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCount=item[position]
        holder.nama.text=itemCount.nama
        holder.code_member.text=itemCount.code_member
        holder.update.setOnClickListener {
            val intent=Intent(context,UpdateMemberActivity::class.java)
            intent.putExtra("id",itemCount.id)
            context.startActivity(intent)
        }
        holder.delete.setOnClickListener {
            AlertDialog.Builder(context)
                .setTitle("Warning")
                .setMessage("Apakah anda ingin menghapus data ini?")
                .setPositiveButton("Yes") {_,_->
                    _DeleteDataKaryawan(itemCount.id)
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
                        URL("${BaseAPI.BaseAPI}/api/members/${id}").openConnection() as HttpURLConnection
                    conn.requestMethod = "DELETE"
                        conn.setRequestProperty("Authorization", "Bearer ${SharePref.token}")
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
                            val intent=Intent(context,MenuAdminActivity::class.java)
                            intent.putExtra("back","member")
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