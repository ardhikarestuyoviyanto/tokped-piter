package com.dhikapro.piter.ui.lembur

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.lembur.LemburPageInterface
import com.dhikapro.piter.app.models.lembur.CreateLemburModel
import com.dhikapro.piter.app.models.lembur.StatusLemburModel
import com.dhikapro.piter.app.repository.lembur.LemburPageRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.getFileName
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityLemburPageBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.TimeZone

class LemburPageActivity : AppCompatActivity(), LemburPageInterface {

    private lateinit var binding: ActivityLemburPageBinding
    private lateinit var lemburPageRepo: LemburPageRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var navigationView: BottomNavigationView
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var buktiLemburPicked: Uri?=Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLemburPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        lemburPageRepo = LemburPageRepo(this, this)
        binding.lemburForm.visibility = View.GONE
        binding.cardStatusLembur.visibility = View.GONE

        lemburPageRepo.getStatusLembur(sharedPreference.getTokenJWT()!!)
        navigationMenu()

        binding.tanggal.setOnClickListener {
            showDatePicker()
        }
        binding.jamMulai.setOnClickListener {
            showStartTime()
        }
        binding.jamSelesai.setOnClickListener {
            showFinishTime()
        }
        binding.bukti.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "*/*"
            }
            startForResult.launch(intent)
        }
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                buktiLemburPicked = result.data?.data!!
                val fileName:String =contentResolver!!.getFileName(buktiLemburPicked!!)
                binding.bukti.text = fileName
            }
        }

        binding.buttonSubmit.setOnClickListener {
            when{
                binding.tanggal.text.isEmpty()->{
                    binding.tanggal.error = getString(R.string.required)
                }
                binding.jamMulai.text.isEmpty()->{
                    binding.jamMulai.error = getString(R.string.required)
                }
                binding.jamSelesai.text.isEmpty()->{
                    binding.jamSelesai.error = getString(R.string.required)
                }
                binding.kegiatan.text.isEmpty()->{
                    binding.kegiatan.error = getString(R.string.required)
                }
                buktiLemburPicked == Uri.EMPTY->{
                    binding.root.snackbar("Silahkan upload bukti lembur anda ...")
                }
                else->{
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSubmit.text = getString(R.string.loading)
                    lemburPageRepo.createLembur(
                        binding.tanggal.text.toString(),
                        binding.jamMulai.text.toString(),
                        binding.jamSelesai.text.toString(),
                        binding.kegiatan.text.toString(),
                        buktiLemburPicked!!,
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }
    }


    @SuppressLint("SetTextI18n")
    private fun clearForm(){
        binding.tanggal.text = null
        binding.jamMulai.text = null
        binding.jamSelesai.text = null
        binding.kegiatan.text = null
        binding.bukti.text = "Upload Bukti Lembur"
        buktiLemburPicked = Uri.EMPTY
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePicker(){
        val fromDate = MaterialDatePicker.Builder.datePicker().setTitleText("dd-mm-YYYY").build()
        fromDate.show(supportFragmentManager, fromDate.toString())
        fromDate.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatted: String = format.format(utc.time)
            binding.tanggal.setText(formatted)
        }
    }

    private fun showStartTime(){
        val hourFromView = if(binding.jamMulai.text.toString().isEmpty()){ 0 }else{ binding.jamMulai.text.toString().substring(0, 2).toInt() }
        val minuteFromView = if(binding.jamMulai.text.toString().isEmpty()){ 0 }else{ binding.jamMulai.text.toString().substring(3,5).toInt() }

        val picker = MaterialTimePicker.Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hourFromView)
            .setMinute(minuteFromView)
            .setTitleText("Jam Mulai Lembur")
            .build()
        picker.show(supportFragmentManager, picker.toString())
        picker.addOnPositiveButtonClickListener {
            var hourSelected = picker.hour.toString()
            var minuteSelected = picker.minute.toString()
            if(hourSelected.length == 1){
                hourSelected = "0$hourSelected"
            }
            if(minuteSelected.length == 1){
                minuteSelected = "0$minuteSelected"
            }
            val timeSelected = "$hourSelected:$minuteSelected"
            binding.jamMulai.setText(timeSelected)

        }
    }

    private fun showFinishTime(){

        if(binding.jamMulai.text.toString().isEmpty()){
            binding.root.snackbar("Pilih waktu mulai terlebih dahulu ...")
        }else{
            val hourFromView = if(binding.jamSelesai.text.toString().isEmpty()){0}else{binding.jamSelesai.text.toString().substring(0, 2).toInt()}
            val minuteFromView = if(binding.jamSelesai.text.toString().isEmpty()){0}else{binding.jamSelesai.text.toString().substring(3, 5).toInt()}

            val picker = MaterialTimePicker.Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setHour(hourFromView)
                .setMinute(minuteFromView)
                .setTitleText("Jam Selesai Lembur")
                .build()
            picker.show(supportFragmentManager, picker.toString())
            picker.addOnPositiveButtonClickListener {
                var hourSelected = picker.hour.toString()
                var minuteSelected = picker.minute.toString()

                if(hourSelected.length == 1){
                    hourSelected = "0$hourSelected"
                }
                if(minuteSelected.length == 1){
                    minuteSelected = "0$minuteSelected"
                }
                val timeSelected = "$hourSelected:$minuteSelected"
                binding.jamSelesai.setText(timeSelected)
            }
        }

    }

    private fun navigationMenu(){
        navigationView =binding.navLembur
        navigationView.selectedItemId = R.id.tambah_lembur_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_lembur_menu->{
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_lembur_menu->{
                    startActivity(Intent(this, RekapLemburActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                else->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    override fun onSuccessGetStatusLembur(statusLemburModel: StatusLemburModel) {
        binding.cardStatusLembur.visibility = View.VISIBLE
        if(statusLemburModel.status_lembur == true){
            binding.cardStatusLembur.background = ContextCompat.getDrawable(this, R.drawable.bg_card_success)
            binding.statusLemburText.text = getString(R.string.status_lembur_aktif)
            binding.lemburForm.visibility = View.VISIBLE
        }else{
            binding.cardStatusLembur.background = ContextCompat.getDrawable(this, R.drawable.bg_card_danger)
            binding.statusLemburText.text = getString(R.string.status_lembur_tidak_aktif)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessCreateLembur(createLemburModel: CreateLemburModel) {
        Alerter.create(this)
            .setTitle("Berhasil")
            .setText(createLemburModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        clearForm()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Simpan Lembur"
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Simpan Lembur"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}