package com.example.kasirgo.AdapterRV

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.item.itemBarang
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

class AdapterListTransaksi(val item:List<itemTransaksi>, val context: Context):RecyclerView.Adapter<AdapterListTransaksi.ViewHolder>() {
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val namaKaryawan=view.findViewById<TextView>(R.id.tvNamaKaryawan_Transaksi)
        val totalPrice=view.findViewById<TextView>(R.id.tvTotalTransaksi_Transaksi)
        val nominalPembayaran=view.findViewById<TextView>(R.id.tvNominalPembayaran_Transaksi)
        val nominalKembalian=view.findViewById<TextView>(R.id.tvNominalKembalian_Transaksi)
        val nominalPPn=view.findViewById<TextView>(R.id.tvNominalPPN_Transaksi)
        val detailBtn=view.findViewById<Button>(R.id.btnDetailTransaksi)

    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdapterListTransaksi.ViewHolder {
        return ViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_transaksi,parent,false))
    }

    override fun onBindViewHolder(holder: AdapterListTransaksi.ViewHolder, position: Int) {
        val items=item[position]
        holder.namaKaryawan.text=items.namaKaryawan
        holder.totalPrice.text=formatIDR(items.totalPrice.toDouble())
        holder.nominalPembayaran.text=formatIDR(items.nominalPembayaran.toDouble())
        holder.nominalPPn.text=formatIDR(items.ppn.toDouble())
        holder.nominalKembalian.text=formatIDR(items.nominalKembalian.toDouble())
        holder.detailBtn.setOnClickListener {
            val intent=Intent(context,DetailTransaksiActivity::class.java)
            intent.putExtra("id_transaksi",items.id)
            context.startActivity(intent)
        }
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