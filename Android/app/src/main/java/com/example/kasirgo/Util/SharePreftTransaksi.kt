package com.example.kasirgo.Util

import com.chibatching.kotpref.KotprefModel

class SharePreftTransaksi: KotprefModel() {
    var member_id by intPref(0)
    var point by intPref(0)
    var ppn by intPref(0)
    var totalPrice by intPref(0)
    var codeVoucer by stringPref("")
}
