package com.example.kasirgo.ui.barang

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.CodeScannerView
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ErrorCallback
import com.budiyev.android.codescanner.ScanMode
import com.example.kasirgo.AdapterRV.AdapterCart
import com.example.kasirgo.MenuKasirActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.CartSharePreft
import com.example.kasirgo.Util.SharePref
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityScanBcbarangBinding
import com.example.kasirgo.item.itemCart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL
import kotlin.system.exitProcess

class ScanBCBarangActivity : AppCompatActivity() {
    private  lateinit var codeScanner:CodeScanner
    private lateinit var binding:ActivityScanBcbarangBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityScanBcbarangBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if(ContextCompat.checkSelfPermission(this,android.Manifest.permission.CAMERA)==
            PackageManager.PERMISSION_DENIED){
            ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.CAMERA),123)
        }else{
            startScan()
        }
    }

    private  fun startScan(){
        val scannerview: CodeScannerView =binding.scannerView
        codeScanner= CodeScanner(this,scannerview)
        codeScanner.camera= CodeScanner.CAMERA_BACK
        codeScanner.formats= CodeScanner.ALL_FORMATS

        codeScanner.autoFocusMode= AutoFocusMode.SAFE
        codeScanner.scanMode= ScanMode.SINGLE
        codeScanner.isAutoFocusEnabled=false
        codeScanner.isFlashEnabled=false

        codeScanner.decodeCallback= DecodeCallback {
            runOnUiThread{
                if (intent.getStringExtra("status")=="updateBarang"){
                    val intent=Intent(this, UpdateBarangActivity::class.java)
                    intent.putExtra("CodeBarang",it.toString())
                    startActivity(intent)
                }else if (intent.getStringExtra("status")=="addCart"){
                    getData(it.toString())
/*
                    CartSharePreft(this).saveCount("1")
*/
                }else{
                    val intent=Intent(this, CreateBarangActivity::class.java)
                    intent.putExtra("CodeBarang",it.toString())
                    startActivity(intent)
                }
            }
        }
        codeScanner.errorCallback= ErrorCallback {
            runOnUiThread{
                Toast.makeText(this,"Error Scann ${it.message}", Toast.LENGTH_SHORT).show()
            }
        }
        scannerview.setOnClickListener{
            codeScanner.startPreview()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode==123){
            if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                Toast.makeText(this,"Camera Permition Grade", Toast.LENGTH_SHORT).show()
                startScan()
            }else{
                Toast.makeText(this,"Camera Permition Denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (:: codeScanner.isInitialized){
            codeScanner?.startPreview()
        }
    }

    override fun onPause() {
        if (:: codeScanner.isInitialized){
            codeScanner?.releaseResources()
        }
        super.onPause()
    }

    private fun getData(id:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val ids=CartSharePreft(this@ScanBCBarangActivity).getId()
                    Log.e("idCart",ids.toString())
                        val conn =
                            URL("${BaseAPI.BaseAPI}/api/barang/WithQr/${id}").openConnection() as HttpURLConnection
                        conn.requestMethod = "GET"

                        getAuth()?.let {
                            conn.setRequestProperty(
                                "Authorization",
                                "Bearer ${it.getString("token")}"
                            )
                        }
                        conn.setRequestProperty("Content-Type", "application/json")
                        val code = conn.responseCode
                        Log.e("data", code.toString())

                        val body = if (code in 200 until 300) {
                            conn.inputStream?.bufferedReader()?.use { it.readLine() }
                        } else {
                            conn.errorStream?.bufferedReader()?.use { it.readLine() }
                        }
                        withContext(Dispatchers.Main) {
                            var IsStatus=false
                             if (code in 200 until 300) {
                                 val jsonKaryawan = JSONObject(body!!)
                                 val dataKaryawan=jsonKaryawan.getJSONArray("Data")
                                 for(i in 0 until dataKaryawan.length()) {
                                     val jsonObject = dataKaryawan.getJSONObject(i)
                                     val id = jsonObject.getString("code_barang")
                                     val price = jsonObject.getString("price")

                                     val idExis=CartSharePreft(this@ScanBCBarangActivity).getId()
                                     val countExis=CartSharePreft(this@ScanBCBarangActivity).getCount()
                                     Log.e("countExis",countExis.toString())
                                     for (i in idExis.indices){
                                         val ids=idExis[i]
                                         val countlist=countExis.getOrNull(i)
                                         if (ids==id){
                                             val pCount = idExis.indexOf(id)
                                             CartSharePreft(this@ScanBCBarangActivity).countUpdate(pCount,(countlist!!.toInt()+1).toString())
                                             val intent=Intent(this@ScanBCBarangActivity, MenuKasirActivity::class.java)
                                             intent.putExtra("status","kasir")
                                             startActivity(intent)
                                             IsStatus=true
                                         }
                                     }
                                     if (IsStatus!=true){
                                         CartSharePreft(this@ScanBCBarangActivity).saveId(id)
                                         CartSharePreft(this@ScanBCBarangActivity).savePrice(price)
                                         CartSharePreft(this@ScanBCBarangActivity).saveCount("1")
                                     }
                                 }
                                 val intent=Intent(this@ScanBCBarangActivity, MenuKasirActivity::class.java)
                                 intent.putExtra("status","kasir")
                                 startActivity(intent)
                             }else{
                                 Toast.makeText(this@ScanBCBarangActivity,"QR Barang tidak terdaftar",Toast.LENGTH_SHORT).show()
                             }
                        }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }
}