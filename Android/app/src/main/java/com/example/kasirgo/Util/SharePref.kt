package com.example.kasirgo.Util

import android.content.Context
import android.content.SharedPreferences
import org.json.JSONObject

class SharePref {
    companion object{
       fun Context.getAuth():JSONObject?{
           return getSharedPreferences("TOKEN",Context.MODE_PRIVATE).getString("auth",null)?.let {
               SharePref
               return@let JSONObject(it)
           }
       }

        fun Context.setAuth(json: JSONObject){
            getSharedPreferences("TOKEN",Context.MODE_PRIVATE).edit().putString("auth",json.toString()).apply()
        }

        fun Context.clearAuth(){
            getSharedPreferences("TOKEN",Context.MODE_PRIVATE).edit().remove("auth").apply()
        }

    }
}