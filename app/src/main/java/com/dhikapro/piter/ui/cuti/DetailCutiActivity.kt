package com.dhikapro.piter.ui.cuti

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.cuti.DetailCutiInterface
import com.dhikapro.piter.app.models.perizinan.DetailPerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanItemModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.models.perizinan.UpdatePerizinanModel
import com.dhikapro.piter.app.repository.cuti.DetailCutiRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.getFileName
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityDetailCutiBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.google.android.material.datepicker.MaterialDatePicker
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class DetailCutiActivity : AppCompatActivity(), DetailCutiInterface {

    private lateinit var binding: ActivityDetailCutiBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var detailCutiRepo: DetailCutiRepo
    private var filePendukung: Uri?=Uri.EMPTY
    private var kategoriPerizinanItemModel = emptyList<KategoriPerizinanItemModel>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailCutiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        DisableDarkMode(this).disable()

        if(intent.getStringExtra("perizinan_id") == null){
            startActivity(Intent(this, RekapCutiActivity::class.java))
            overridePendingTransition(0, 0)
        }
        binding.tanggalMulai.setOnClickListener {
            showDatePickerStart()
        }
        binding.tanggalSelesai.setOnClickListener {
            showDatePickerFinish()
        }
        binding.filePendukung.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT).apply {
                type = "application/pdf"
            }
            startForResult.launch(intent)
        }
        startForResult = registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ){result: ActivityResult ->
            if(result.resultCode == Activity.RESULT_OK){
                filePendukung = result.data?.data!!
                val fileName:String =contentResolver!!.getFileName(filePendukung!!)
                binding.filePendukung.text = fileName
            }
        }

        sharedPreference = SharedPreference(this)
        detailCutiRepo = DetailCutiRepo(this, this)
        detailCutiRepo.getKategoriCuti(sharedPreference.getTokenJWT()!!)
        binding.cutiForm.visibility = View.GONE
        binding.buttonSubmit.setOnClickListener {
            when{
                binding.tanggalMulai.text.isEmpty()->{
                    binding.tanggalMulai.error = getString(R.string.required)
                }
                binding.tanggalSelesai.text.isEmpty()->{
                    binding.tanggalSelesai.error = getString(R.string.required)
                }
                binding.kategoriCuti.selectedItem.equals(getString(R.string.kategori_cuti_text))->{
                    binding.root.snackbar("Pilih kategori cuti dahulu ...")
                }
                else->{
                    binding.buttonSubmit.text = getString(R.string.loading)
                    binding.buttonSubmit.isEnabled = false

                    var kategoriPerizinanID = ""
                    kategoriPerizinanItemModel.forEach{item->
                        if(item.nama_kategori == binding.kategoriCuti.selectedItem.toString()){
                            kategoriPerizinanID = item.id!!
                        }
                    }
                    detailCutiRepo.updateCuti(
                        binding.tanggalMulai.text.toString(),
                        binding.tanggalSelesai.text.toString(),
                        filePendukung!!,
                        kategoriPerizinanID,
                        intent.getStringExtra("perizinan_id")!!,
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerStart(){
        val fromDate = MaterialDatePicker.Builder.datePicker().setTitleText("dd-mm-YYYY").build()
        fromDate.show(supportFragmentManager, fromDate.toString())
        fromDate.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatted: String = format.format(utc.time)
            binding.tanggalMulai.setText(formatted)
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun showDatePickerFinish(){
        val fromDate = MaterialDatePicker.Builder.datePicker().setTitleText("dd-mm-YYYY").build()
        fromDate.show(supportFragmentManager, fromDate.toString())
        fromDate.addOnPositiveButtonClickListener {
            val utc = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
            utc.timeInMillis = it
            val format = SimpleDateFormat("yyyy-MM-dd")
            val formatted: String = format.format(utc.time)
            binding.tanggalSelesai.setText(formatted)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccessGetKategoriCuti(kategoriPerizinanModel: KategoriPerizinanModel) {
        kategoriPerizinanItemModel = kategoriPerizinanModel.data

        val kategoriName = ArrayList<String>()
        kategoriName.add(getString(R.string.kategori_cuti_text))
        kategoriPerizinanItemModel.forEach{ value->
            kategoriName.add(value.nama_kategori!!)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kategoriName
        )
        binding.kategoriCuti.adapter = adapter
        detailCutiRepo.getDetailCuti(
            intent.getStringExtra("perizinan_id")!!,
            sharedPreference.getTokenJWT()!!
        )
    }

    override fun onSuccessGetDetailCuti(detailPerizinanModel: DetailPerizinanModel) {
        binding.cutiForm.visibility = View.VISIBLE

        when (detailPerizinanModel.data?.status_perizinan) {
            "DIVERIFIKASI" -> {
                binding.cardStatusAccCuti.background = ContextCompat.getDrawable(this, R.drawable.bg_card_primary)
            }
            "TIDAK DISETUJUI" -> {
                binding.cardStatusAccCuti.background = ContextCompat.getDrawable(this, R.drawable.bg_card_danger)
            }
            else -> {
                binding.cardStatusAccCuti.background = ContextCompat.getDrawable(this, R.drawable.bg_card_success)
            }
        }

        binding.statusAccCutiText.text = detailPerizinanModel.data?.status_perizinan
        binding.tanggalMulai.setText(detailPerizinanModel.data?.tanggal_mulai)
        binding.tanggalSelesai.setText(detailPerizinanModel.data?.tanggal_selesai)
        binding.suratCuti.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(detailPerizinanModel.data?.file_pendukung)))
        }
        kategoriPerizinanItemModel.forEachIndexed{ i, value->
            if(value.nama_kategori == detailPerizinanModel.data?.kategori_perizinan){
                val selectionIndex = i + 1
                if (selectionIndex >= 0 && selectionIndex < binding.kategoriCuti.count) {
                    binding.kategoriCuti.setSelection(selectionIndex)
                } else {
                    Log.e("ERROR", "Index out bound $selectionIndex")
                }

            }
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessUpdateCuti(updatePerizinanModel: UpdatePerizinanModel) {
        Alerter.create(this)
            .setTitle("Berhasil Update Cuti")
            .setText(updatePerizinanModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Data Cuti"
        detailCutiRepo.getDetailCuti(
            intent.getStringExtra("perizinan_id")!!,
            sharedPreference.getTokenJWT()!!
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Data Cuti"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}