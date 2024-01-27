package com.example.kasirgo.ui.Transaksi

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.kasirgo.R
import com.example.kasirgo.databinding.ActivityTransaksiBinding

class TransaksiActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransaksiBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityTransaksiBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

}