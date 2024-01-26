package com.example.kasirgo.Util

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import org.json.JSONArray

class CartSharePreft constructor(context: Context) {
    companion object{
        private const val shareName="MyAppCart"
        private const val shareId="id"
        private const val shareCount="count"
        private const val sharePrice="price"
    }

    val sharePreft: SharedPreferences =context.getSharedPreferences(shareName,Context.MODE_PRIVATE)
    val editorid: SharedPreferences.Editor=sharePreft.edit()
    val editorCount: SharedPreferences.Editor=sharePreft.edit()
    val editorrice: SharedPreferences.Editor=sharePreft.edit()

    fun saveId(id:String){
        Log.e("idCart",id)
        val jsonArray= JSONArray()
        val exisId=sharePreft.getString(shareId,null)
        if (exisId!=null){
            val idArray= JSONArray(exisId)
            for (i in 0 until idArray.length()){
                jsonArray.put(idArray.getString(i))
            }
        }
        jsonArray.put(id)
        editorid.putString(shareId,jsonArray.toString())
        editorid.apply()
    }

    fun savePrice(data: String){
        val jsonArray= JSONArray()
        val priceExis= sharePreft.getString(sharePrice,null)
        if (priceExis!=null){
            val priceArray= JSONArray(priceExis)
            for (i in 0 until priceArray.length() ){
                jsonArray.put(priceArray.getString(i))
            }
        }
        jsonArray.put(data)

        editorrice.putString(sharePrice,jsonArray.toString()).apply()
    }

    fun updatePrice(potition: Int,data: String){
        val price=sharePreft.getString(sharePrice,null)
        if (price!=null){
            val priceArray= JSONArray(price)
            for (i in 0 until priceArray.length()){
                if (i==potition) {
                    priceArray.put(i, data)
                }
            }
            editorrice.putString(sharePrice,priceArray.toString()).apply()
        }
    }

    fun getPrice():List<String>{
        val priceExis=sharePreft.getString(sharePrice,null)
        val data= mutableListOf<String>()
        if (priceExis!=null){
            val priceJson= JSONArray(priceExis)
            for (i in 0 until priceJson.length()){
                val item=priceJson.getString(i)
                data.add(item)
            }
        }
        return data
    }


    fun saveCount(count:String){
        Log.e("totalCount",count)
        val jsonArray= JSONArray()
        val countExis=sharePreft.getString(shareCount,null)
        if (countExis!=null){
            val exisArray= JSONArray(countExis)
            for (i in 0 until exisArray.length()){
                jsonArray.put(exisArray.getString(i))
                break
            }
        }
        jsonArray.put(count)
        editorCount.putString(shareCount,jsonArray.toString())
        editorCount.apply()
    }

    fun getId():List<String>{
        val idExis=sharePreft.getString(shareId,null)
        val dataList= mutableListOf<String>()
        if (idExis!=null){
            val jsonArray= JSONArray(idExis)
            for (i in 0 until jsonArray.length()){
                val list=jsonArray.getString(i)
                dataList.add(list)
            }
        }
        return dataList
    }

    fun  getCount():List<String>{
        val countExis=sharePreft.getString(shareCount,null)
        val datalist= mutableListOf<String>()
        if (countExis!=null){
            val countArray= JSONArray(countExis)
            for (i in 0 until countArray.length()){
                val list=countArray.getString(i)
                datalist.add(list)
            }
        }
        return datalist
    }

    fun countUpdate(potition:Int,data:String){
        val countExis=sharePreft.getString(shareCount,null)

        if (countExis!=null){
            val countJson= JSONArray(countExis)
            for (i in 0 until countJson.length()){
                if (i==potition){
                    countJson.put(i, data)
                    break
                }
            }
            editorCount.putString(shareCount,countJson.toString())
            editorCount.apply()
        }
    }


    fun deleteData(potition: Int){
        val dataExisCount=sharePreft.getString(shareCount,null)
        val dataExisid=sharePreft.getString(shareId,null)
        val priceExis=sharePreft.getString(sharePrice,null)
            if (dataExisid!=null) {
                val dataArrayid = JSONArray(dataExisid)
                val dataArrayCount = JSONArray(dataExisCount)
                val priceArray = JSONArray(priceExis)
                for (i in 0 until dataArrayid.length()) {
                        if (i == potition) {
                            dataArrayid.remove(i)
                            dataArrayCount.remove(i)
                            priceArray.remove(i)
                        }
                }
                editorrice.putString(sharePrice,priceArray.toString()).apply()
                editorCount.putString(shareCount,dataArrayCount.toString()).apply()
                editorid.putString(shareId,dataArrayid.toString()).apply()
            }
    }

    fun clearCart(){
        editorrice.clear().apply()
        editorid.clear().apply()
        editorCount.clear().apply()
    }
}