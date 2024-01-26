package com.example.kasirgo.AdapterRV

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.R
import com.example.kasirgo.item.Setting

class AdapterListData(private val settingsList: List<Setting>, val context: Context): RecyclerView.Adapter<AdapterListData.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewSettingTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewSettingDescription)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = settingsList[position]
        holder.titleTextView.text = setting.title
        holder.descriptionTextView.text = setting.description
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }
}