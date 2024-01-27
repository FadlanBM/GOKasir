package com.example.kasirgo.AdapterRV

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.R
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.item.itemCart
import kotlinx.coroutines.CoroutineScope
import java.text.NumberFormat
import java.util.Currency
import java.util.Locale

class AdapterListBarangTransaksi(val item:List<itemCart>, val context: Context, val lifecycleOwner: LifecycleOwner): RecyclerView.Adapter<AdapterListBarangTransaksi.ViewHolder>()  {

    private var result= MutableLiveData<Int>()
    private val readresuld: LiveData<Int> get() = result
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val namaBarang: TextView =view.findViewById(R.id.tvNamaBarangTransaksi)
        val priceBarang: TextView =view.findViewById(R.id.tvPriceBarangTransaksi)
        val codeBarang: TextView =view.findViewById(R.id.tvCodeBarangTransaksi)
        val jumCart:TextView=view.findViewById(R.id.tvJumTransaksi)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_cart_transaksi, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items=item[position]
        holder.namaBarang.text=items._NamaBarang
        holder.priceBarang.text=formatIDR(items._Price.toDouble())
        holder.codeBarang.text=items._CodeBarang
        result.value=items._Count.toInt()
        val priceSave=items.Default_Price

        readresuld.observe(lifecycleOwner){
            items._Count=it.toString()
        }

        readresuld.observe(lifecycleOwner){
            items._Price=(priceSave.toInt()*it).toString()
        }
        CartSharePreft(context).updatePrice(position,items._Price)
        holder.jumCart.text=items._Count
        holder.priceBarang.text=formatIDR(items._Price.toDouble())
    }

    private fun formatIDR(nominal:Double):String{

        val localeID = Locale("id", "ID")

        val formatRupiah = NumberFormat.getCurrencyInstance(localeID)

        formatRupiah.currency = Currency.getInstance("IDR")

        val hasilFormat = formatRupiah.format(nominal)

        return hasilFormat
    }

}