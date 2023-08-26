package com.dhikapro.piter.ui.cuti

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.cuti.CutiPageInterface
import com.dhikapro.piter.app.models.perizinan.CreatePerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanItemModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.repository.cuti.CutiPageRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.getFileName
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityCutiPageBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class CutiPageActivity : AppCompatActivity(), CutiPageInterface {

    private lateinit var binding: ActivityCutiPageBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var cutiPageRepo: CutiPageRepo
    private var filePendukung: Uri?=Uri.EMPTY
    private var kategoriPerizinanItemModel = emptyList<KategoriPerizinanItemModel>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityCutiPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        navigationMenu()

        sharedPreference = SharedPreference(this)
        cutiPageRepo = CutiPageRepo(this, this)
        cutiPageRepo.getKategoriCuti(sharedPreference.getTokenJWT()!!)
        binding.cutiForm.visibility = View.GONE

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
                filePendukung == Uri.EMPTY->{
                    binding.root.snackbar("Silahkan upload file pengajuan cuti anda ...")
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
                    cutiPageRepo.createCuti(
                        binding.tanggalMulai.text.toString(),
                        binding.tanggalSelesai.text.toString(),
                        filePendukung!!,
                        kategoriPerizinanID,
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }
    }

    private fun navigationMenu(){
        navigationView =binding.navCuti
        navigationView.selectedItemId = R.id.tambah_cuti_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_cuti_menu->{
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_cuti_menu->{
                    startActivity(Intent(this, RekapCutiActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                else->{
                    return@setOnItemSelectedListener true
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

    override fun onSuccessGetKategoriCuti(kategoriPerizinanModel: KategoriPerizinanModel) {
        binding.cutiForm.visibility = View.VISIBLE
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
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessCreateCuti(createPerizinanModel: CreatePerizinanModel) {
        Alerter.create(this)
            .setTitle("Cuti berhasil diajukan")
            .setText(createPerizinanModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        binding.buttonSubmit.text = "Ajukan Cuti"
        binding.buttonSubmit.isEnabled = true

        binding.kategoriCuti.setSelection(0)
        binding.tanggalMulai.text = null
        binding.tanggalSelesai.text = null
        binding.filePendukung.text = "Upload File Pengajuan Cuti"
        filePendukung = Uri.EMPTY
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.text = "Ajukan Cuti"
        binding.buttonSubmit.isEnabled = true
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}