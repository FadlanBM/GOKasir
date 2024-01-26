package com.example.kasirgo.ui.Vouchers

import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.lifecycleScope
import com.example.kasirgo.MenuAdminActivity
import com.example.kasirgo.R
import com.example.kasirgo.Util.BaseAPI
import com.example.kasirgo.Util.IdKoneksi
import com.example.kasirgo.Util.SharePref.Companion.getAuth
import com.example.kasirgo.databinding.ActivityCreateVoucerBinding
import com.example.kasirgo.databinding.ActivityUpdateKaryawanBinding
import com.example.kasirgo.library.ExceptionMessage
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.io.OutputStreamWriter
import java.lang.RuntimeException
import java.net.HttpURLConnection
import java.net.URL
import java.util.Calendar

class UpdateVoucerActivity : AppCompatActivity() {
    private lateinit var binding:ActivityCreateVoucerBinding
    private lateinit var tiStartDate: EditText
    private lateinit var tiEndDate: EditText
    private lateinit var selectedDateTextView1: ImageView
    private lateinit var selectedDateTextView2: ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding=ActivityCreateVoucerBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnRowBack.setOnClickListener {
            val intent= Intent(this, MenuAdminActivity::class.java)
            intent.putExtra("back","voucer")
            startActivity(intent)
        }

        tiStartDate=binding.tiStartDate
        tiEndDate=binding.tiEndDate
        selectedDateTextView1=binding.toggleDateStart
        selectedDateTextView2=binding.toggleDateEnd
        val idvoucer=intent.getStringExtra("idvoucer")


        selectedDateTextView1.setOnClickListener {
            showDatePickerDialog1()
        }
        selectedDateTextView2.setOnClickListener {
            showDatePickerDialog2()
        }
        binding.btnSubmit.setOnClickListener {
            UpdateKaryawan(idvoucer.toString())
        }

        _GetDataBarang(idvoucer.toString())
    }

    override fun onBackPressed() {
        val intent= Intent(this@UpdateVoucerActivity, MenuAdminActivity::class.java)
        intent.putExtra("back","voucer")
        startActivity(intent)
        super.onBackPressed()
    }

    private fun UpdateKaryawan(id:String) {
        val handler = CoroutineExceptionHandler { _, e ->
            if (e is Exception) {
                AlertDialog.Builder(this)
                    .setTitle("Error")
                    .setMessage(e.message)
                    .setNeutralButton("Yes") { _, _ -> }
                    .setOnDismissListener {
                        if (e !is ExceptionMessage.IgnorableException) {
                            throw RuntimeException(e)
                        }
                    }
                    .create()
                    .show()
            } else {
                throw RuntimeException(e)
            }
        }

        lifecycleScope.launch(handler) {
            withContext(Dispatchers.IO) {
                val discount=binding.tiDiscont
                val startDate=binding.tiStartDate
                val endDate=binding.tiEndDate
                val rbActive=binding.rbActive
                val rbNotActive=binding.rbNonActive


                if (discount.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nama Petugas masih kosong")
                if (startDate.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Nik Petugas masih kosong")
                if (endDate.text.toString().isBlank()) throw ExceptionMessage.IgnorableException("Form Username masih kosong")
                if(!rbActive.isChecked && !rbNotActive.isChecked){
                    throw ExceptionMessage.IgnorableException("Check Box status belum terisi")
                }
                try {

                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/voucer/${id}").openConnection() as HttpURLConnection
                    conn.requestMethod = "PUT"

                    getAuth()?.let {
                        conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
                    }
                    conn.doOutput = true
                    conn.setRequestProperty("Content-Type", "application/json")
                    OutputStreamWriter(conn.outputStream).use {
                        it.write(JSONObject().apply {
                            put("discount",discount.text.toString().toInt())
                            put("start_date",startDate.text.toString())
                            put("end_date",endDate.text.toString())
                            if (rbActive.isChecked){
                                put("is_active","true" )
                            }
                            if (rbNotActive.isChecked){
                                put("is_active","false" )
                            }
                        }.toString())
                    }

                    val code = conn.responseCode
                    Log.e("data", code.toString())

                    val body = if (code in 200 until 300) {
                        conn.inputStream?.bufferedReader()?.use { it.readLine() }
                    } else {
                        conn.errorStream?.bufferedReader()?.use { it.readLine() }
                    }


                    withContext(Dispatchers.Main) {
                        val json = JSONObject(body!!)
                        Log.e("Hasil",json.toString())

                        if (code in 200 until 300){
                            AlertDialog.Builder(this@UpdateVoucerActivity)
                                .setTitle("Information")
                                .setMessage("Berhasil input data")
                                .setNeutralButton("OK") {_,_->
                                    discount.setText("")
                                    startDate.setText("")
                                    endDate.setText("")
                                    rbActive.isChecked=false
                                    rbNotActive.isChecked=false
                                    val intent= Intent(this@UpdateVoucerActivity, MenuAdminActivity::class.java)
                                    intent.putExtra("back","voucer")
                                    startActivity(intent)
                                }
                                .create()
                                .show()
                        }
                    }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }

    private fun _GetDataBarang(id:String) {
        lifecycleScope.launch() {
            withContext(Dispatchers.IO) {
                try {
                    val tidiscount=binding.tiDiscont
                    val tistartDate=binding.tiStartDate
                    val tiendDate=binding.tiEndDate
                    val tirbActive=binding.rbActive
                    val tirbNotActive=binding.rbNonActive

                    val conn =
                        URL("${BaseAPI.BaseAPI}/api/voucer/${id}").openConnection() as HttpURLConnection
                    conn.requestMethod = "GET"

                    getAuth()?.let {
                        conn.setRequestProperty("Authorization", "Bearer ${it.getString("token")}")
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
                        val karyawanJson= JSONObject(body!!)
                        Log.e("json",karyawanJson.toString())
                        val listvoucer=karyawanJson.getJSONArray("Data")
                        for (i in 0 until listvoucer.length()){
                            val datavoucer=listvoucer.getJSONObject(i)
                            val startDate=datavoucer.getString("start_date")
                            val endDate=datavoucer.getString("end_date")
                            val discount=datavoucer.getString("discount")
                            val status=datavoucer.getString("is_active")
                            if (status=="false"){
                                tirbActive.isChecked=false
                                tirbNotActive.isChecked=true
                            }else{
                                tirbActive.isChecked=true
                                tirbNotActive.isChecked=false
                            }
                            tidiscount.setText(discount)
                            tistartDate.setText(startDate)
                            tiendDate.setText(endDate)
                        }
                    }
                }catch (e:Exception){
                    Log.e("Error ",e.toString())
                }
            }
        }
    }

    fun showDatePickerDialog1() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Menangani pemilihan tanggal dari dialog
                val formattedMonth = (month + 1).toString().padStart(2, '0')
                val formattedDay = dayOfMonth.toString().padStart(2, '0')
                val selectedDate = "01/$formattedMonth/$year"
                tiStartDate.setText(selectedDate)
            },
            year,
            month,
            day
        )

        datePickerDialog.show()
    }
    fun showDatePickerDialog2() {
        val calendar: Calendar = Calendar.getInstance()
        val year: Int = calendar.get(Calendar.YEAR)
        val month: Int = calendar.get(Calendar.MONTH)
        val day: Int = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(
            this,
            { _, year, month, dayOfMonth ->
                // Menangani pemilihan tanggal dari dialog
                val formattedMonth = (month + 1).toString().padStart(2, '0')
                val formattedDay = dayOfMonth.toString().padStart(2, '0')
                val selectedDate = "01/$formattedMonth/$year"
                tiEndDate.setText(selectedDate)
            },
            year,
            month,
            day
        )
        datePickerDialog.show()
    }

}