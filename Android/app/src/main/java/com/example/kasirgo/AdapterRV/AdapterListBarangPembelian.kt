package com.example.kasirgo.AdapterRV

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.item.itemBarang
import com.example.kasirgo.item.itemPembelianBarang
import com.example.kasirgo.item.itemTransaksi
import com.example.kasirgo.ui.Transaksi.DetailTransaksiActivity
import com.example.kasirgo.ui.barang.UpdateBarangActivity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.net.HttpURLConnection
import java.net.URL
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class AdapterListBarangPembelian(val item:List<itemPembelianBarang>, val context: Context):RecyclerView.Adapter<AdapterListBarangPembelian.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val namaBarang=view.findViewById<TextView>(R.id.tvNamaBarangTransaksi)
        val price=view.findViewById<TextView>(R.id.tvPriceBarangTransaksi)
        val codeBarang=view.findViewById<TextView>(R.id.tvCodeBarangTransaksi)
        val totalCount=view.findViewById<TextView>(R.id.tvJumTransaksi)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterListBarangPembelian.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_cart_transaksi,parent,false))
    }

    override fun onBindViewHolder(holder: AdapterListBarangPembelian.ViewHolder, position: Int) {
        val items=item[position]
        holder.namaBarang.text=items.namaBarang
        holder.price.text=items.price
        holder.codeBarang.text=items.codeBarang
        holder.totalCount.text=items.count
    }

    override fun getItemCount(): Int {
        return item.size
    }

    private fun formatIDR(nominal:Double):String{

        val localeID = Locale("id", "ID")

        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        formatRupiah.currency = Currency.getInstance("IDR")

        val hasilFormat = formatRupiah.format(nominal)

        return hasilFormat
    }
}