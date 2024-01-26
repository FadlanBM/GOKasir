package com.example.kasirgo.AdapterRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.R
import com.example.kasirgo.item.itemAdmin
import com.example.kasirgo.item.itemKaryawan
import kotlinx.coroutines.CoroutineScope

class AdaterListAdmin(val item:List<itemAdmin>, val context: Context, private val coroutineScope: CoroutineScope): RecyclerView.Adapter<AdaterListAdmin.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val nama: TextView =view.findViewById(R.id.tvName)
        val nik: TextView =view.findViewById(R.id.tvNik)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return AdaterListAdmin.ViewHolder(
            LayoutInflater.from(parent.context)
                .inflate(R.layout.adapter_list_admin, parent, false)
        )

    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val itemCount=item[position]
        holder.nama.text=itemCount.nama
        holder.nik.text=itemCount.nik
    }
}