package com.dhikapro.piter.ui.lembur

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.lembur.DetailLemburInterface
import com.dhikapro.piter.app.models.lembur.DetailLemburModel
import com.dhikapro.piter.app.models.lembur.UpdateLemburModel
import com.dhikapro.piter.app.repository.lembur.LemburDetailRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.getFileName
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityDetailLemburBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class DetailLemburActivity : AppCompatActivity(), DetailLemburInterface {

    private lateinit var binding: ActivityDetailLemburBinding
    private lateinit var lemburDetailRepo: LemburDetailRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private var buktiLemburPicked: Uri?=Uri.EMPTY

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailLemburBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        lemburDetailRepo = LemburDetailRepo(this, this)
        sharedPreference =SharedPreference(this)

        if(intent.getStringExtra("lembur_id") == null){
            startActivity(Intent(this, RekapLemburActivity::class.java))
            overridePendingTransition(0, 0)
        }

        lemburDetailRepo.getDetailLembur(
            intent.getStringExtra("lembur_id")!!,
            sharedPreference.getTokenJWT()!!
        )

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
                else->{
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSubmit.text = getString(R.string.loading)
                    lemburDetailRepo.updateLembur(
                        binding.tanggal.text.toString(),
                        binding.jamMulai.text.toString(),
                        binding.jamSelesai.text.toString(),
                        binding.kegiatan.text.toString(),
                        buktiLemburPicked!!,
                        intent.getStringExtra("lembur_id")!!,
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }

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

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessUpdateLembur(updateLemburModel: UpdateLemburModel) {
        Alerter.create(this)
            .setTitle("Berhasil Update")
            .setText(updateLemburModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        lemburDetailRepo.getDetailLembur(
            intent.getStringExtra("lembur_id")!!,
            sharedPreference.getTokenJWT()!!
        )
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Lembur"
    }

    override fun onSuccessGetDetailLembur(detailLemburModel: DetailLemburModel) {
        val detailLembur = detailLemburModel.data
        binding.tanggal.setText(detailLembur?.tanggal)
        binding.jamMulai.setText(detailLembur?.jam_mulai)
        binding.jamSelesai.setText(detailLembur?.jam_selesai)
        binding.kegiatan.setText(detailLembur?.kegiatan)
        binding.buktiPrev.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detailLembur?.bukti)))
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Lembur"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}
