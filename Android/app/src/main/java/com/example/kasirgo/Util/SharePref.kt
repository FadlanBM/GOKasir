package com.example.kasirgo.Util

import android.content.Context
import android.content.SharedPreferences
import com.chibatching.kotpref.KotprefModel
import org.json.JSONObject

object SharePref: KotprefModel()  {
    var token by stringPref("")
}
