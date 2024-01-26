package com.example.kasirgo.AdaptorTL

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.example.kasirgo.MenuFragment.KasirFragment
import com.example.kasirgo.MenuFragment.RiwayatFragment

class AdapterMenuTL(fm:FragmentManager, val tabitem:Int):FragmentPagerAdapter(fm) {
    override fun getCount(): Int {
        return tabitem
    }

    override fun getItem(position: Int): Fragment {
        return when(position){
            0->KasirFragment()
            1->RiwayatFragment()
            else->getItem(position)
        }
    }

}