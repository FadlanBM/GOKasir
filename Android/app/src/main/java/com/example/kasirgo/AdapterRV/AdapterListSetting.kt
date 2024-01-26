package com.example.kasirgo.AdapterRV

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.example.kasirgo.LoginActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePref.Companion.setAuth
import com.example.kasirgo.item.Setting
import com.example.kasirgo.ui.setting.DataPribadiActivity
import com.example.kasirgo.ui.setting.ResetPasswordActivity
import com.example.kasirgo.ui.setting.UbahDataActivity

class AdapterListSetting(private val settingsList: List<Setting>,val context: Context):RecyclerView.Adapter<AdapterListSetting.ViewHolder>() {
    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.textViewSettingTitle)
        val descriptionTextView: TextView = itemView.findViewById(R.id.textViewSettingDescription)
        val aksi: CardView =itemView.findViewById(R.id.btn_aksi_settings)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_list, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val setting = settingsList[position]
        holder.titleTextView.text = setting.title
        holder.descriptionTextView.text = setting.description
        holder.aksi.setOnClickListener {
            if (setting.title=="Logout"){
                showAlertDialogLogout("Peringatan","Apakah anda akan Logout ?")
            }
            if (setting.title=="Data Pribadi"){
                context.startActivity(Intent(context,DataPribadiActivity::class.java))
            }
            if (setting.title=="Ubah Data Pribadi"){
                context.startActivity(Intent(context,UbahDataActivity::class.java))
            }
            if (setting.title=="Reset Password"){
                context.startActivity(Intent(context,ResetPasswordActivity::class.java))
            }
        }
    }

    override fun getItemCount(): Int {
        return settingsList.size
    }

    fun showAlertDialogLogout(ttl:String,msg:String) {
        val alertDialogBuilder = AlertDialog.Builder(context)

        alertDialogBuilder.setTitle(ttl)

        alertDialogBuilder.setMessage(msg)

        alertDialogBuilder.setPositiveButton("Yes", DialogInterface.OnClickListener { dialog, which ->
            context.startActivity(Intent(context,LoginActivity::class.java))
        })

        alertDialogBuilder.setNegativeButton("Batal", DialogInterface.OnClickListener { dialog, which ->
            dialog.dismiss()
        })

        val alertDialog = alertDialogBuilder.create()
        alertDialog.show()
    }
}