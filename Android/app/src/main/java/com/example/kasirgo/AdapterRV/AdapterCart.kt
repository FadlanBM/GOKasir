package com.example.kasirgo.AdapterRV

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.R
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SwipeToDeleteCallback
import com.example.kasirgo.item.itemBarang
import com.example.kasirgo.item.itemCart
import kotlinx.coroutines.CoroutineScope

class AdapterCart(val item:List<itemCart>, val context: Context, private val coroutineScope: CoroutineScope,val lifecycleOwner: LifecycleOwner,val view: View): RecyclerView.Adapter<AdapterCart.ViewHolder>() {

    private var result= MutableLiveData<Int>()
    private val readresuld: LiveData<Int> get() = result
    class ViewHolder(view: View):RecyclerView.ViewHolder(view) {
        val namaBarang:TextView=view.findViewById(R.id.tvNamaBarangCart)
        val priceBarang:TextView=view.findViewById(R.id.tvPriceBarangCart)
        val codeBarang:TextView=view.findViewById(R.id.tvCodeBarangCart)
        val btnPlus:ImageView=view.findViewById(R.id.btnPlusCart)
        val btnMinus:ImageView=view.findViewById(R.id.btnMinusCart)
        val jumCart:TextView=view.findViewById(R.id.tvJumCart)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.adapter_list_barang_cart, parent, false)
        )
    }

    override fun getItemCount(): Int {
        return item.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val items=item[position]
        holder.namaBarang.text=items._NamaBarang
        holder.priceBarang.text="IDR "+items._Price
        holder.codeBarang.text=items._CodeBarang
        result.value=items._Count.toInt()
        val priceSave=items.Default_Price

        readresuld.observe(lifecycleOwner){
            items._Count=it.toString()
            holder.btnMinus.isEnabled = it != 1
        }

        readresuld.observe(lifecycleOwner){
            items._Price=(priceSave.toInt()*it).toString()
            Log.e("priceAwal",items._Price)
        }
            CartSharePreft(context).updatePrice(position,items._Price)
        holder.jumCart.text=items._Count
        holder.priceBarang.text="IDR "+items._Price

        holder.btnPlus.setOnClickListener {
            addCount(position)
            readresuld.observe(lifecycleOwner){
                items._Price=(priceSave.toInt()*it).toString()
            }
            CartSharePreft(context).countUpdate(position,items._Count)
            Log.e("item",priceSave)

            CartSharePreft(context).updatePrice(position,items._Price)
            holder.priceBarang.text="IDR "+items._Price
            holder.jumCart.text=items._Count

            val pricee=CartSharePreft(context).getPrice()
            var totalPrice=0
            for (price in pricee){
                totalPrice+=price.toInt()
            }
            val addTransaksi:Button=view.findViewById(R.id.btn_Transaksi)
            Log.e("price",pricee.toString())
            if (items._Count!="0"){
            addTransaksi.text="IDR "+totalPrice.toString()
            }
            addTransaksi.isEnabled = items._Count != "0"
        }

        holder.btnMinus.setOnClickListener {
            minusCount(position)
            readresuld.observe(lifecycleOwner){
                items._Price=(priceSave.toInt()*it).toString()
            }
            CartSharePreft(context).countUpdate(position,items._Count)
            Log.e("item",priceSave)

            CartSharePreft(context).updatePrice(position,items._Price)

            holder.priceBarang.text="IDR "+items._Price
            holder.jumCart.text=items._Count

            val pricee=CartSharePreft(context).getPrice()
            var totalPrice=0
            for (price in pricee){
                totalPrice+=price.toInt()
            }
            val addTransaksi:Button=view.findViewById(R.id.btn_Transaksi)
            Log.e("price",pricee.toString())
            if (items._Count!="0"){
                addTransaksi.text="IDR "+totalPrice.toString()
            }
            addTransaksi.isEnabled = items._Count != "0"
        }
        val pricee=CartSharePreft(context).getPrice()

        val addTransaksi:Button=view.findViewById(R.id.btn_Transaksi)
        Log.e("price",pricee.toString())
        var totalPrice=0
        for (price in pricee){
            totalPrice+=price.toInt()
        }
        addTransaksi.text="IDR "+totalPrice.toString()
        addTransaksi.isEnabled = items._Count != "0"

        val ids=CartSharePreft(context).getId()
        if (ids.isEmpty()){
            addTransaksi.isVisible=false
        }
    }

    fun addCount(potition:Int){
        val oldValue=CartSharePreft(context).getCount()[potition].toInt()
        result.value=(oldValue)?.plus(1)
    }
    fun  minusCount(potition: Int){
        val oldValue=CartSharePreft(context).getCount()[potition].toInt()
        result.value=(oldValue)?.minus(1)
    }
}