package com.example.kasirgo.Util

import com.chibatching.kotpref.KotprefModel

class SharePreftTransaksi: KotprefModel() {
    var member_id by stringPref("")
    var point by stringPref("")
    var ppn by stringPref("")
    var totalPrice by stringPref("")
}