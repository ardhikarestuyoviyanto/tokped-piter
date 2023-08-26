package com.dhikapro.piter.ui.izin

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.izin.IzinPageInterface
import com.dhikapro.piter.app.models.perizinan.CreatePerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanItemModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.repository.izin.IzinPageRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.getFileName
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityIzinPageBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*

class IzinPageActivity : AppCompatActivity(), IzinPageInterface {

    private lateinit var binding: ActivityIzinPageBinding
    private lateinit var izinPageRepo: IzinPageRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var navigationView: BottomNavigationView
    private var filePendukung: Uri?= Uri.EMPTY
    private var kategoriPerizinanItemModel = emptyList<KategoriPerizinanItemModel>()
    private lateinit var startForResult: ActivityResultLauncher<Intent>

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityIzinPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        navigationMenu()

        izinPageRepo = IzinPageRepo(this, this)
        sharedPreference = SharedPreference(this)
        izinPageRepo.getKategoriIzin(sharedPreference.getTokenJWT()!!)
        binding.izinForm.visibility = View.GONE

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
                binding.kategoriIzin.selectedItem.equals(getString(R.string.kategori_izin_text))->{
                    binding.root.snackbar("Pilih kategori izin dahulu ...")
                }
                filePendukung == Uri.EMPTY->{
                    binding.root.snackbar("Silahkan upload file pengajuan izin anda ...")
                }
                else->{
                    binding.buttonSubmit.text = getString(R.string.loading)
                    binding.buttonSubmit.isEnabled = false

                    var kategoriPerizinanID = ""
                    kategoriPerizinanItemModel.forEach{item->
                        if(item.nama_kategori == binding.kategoriIzin.selectedItem.toString()){
                            kategoriPerizinanID = item.id!!
                        }
                    }
                    izinPageRepo.createIzin(
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
        navigationView =binding.navIzin
        navigationView.selectedItemId = R.id.tambah_izin_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_izin_menu->{
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_izin_menu->{
                    binding.root.snackbar("Test")
                    startActivity(Intent(this, RekapIzinActivity::class.java))
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

    override fun onSuccessGetKategoriIzin(kategoriPerizinanModel: KategoriPerizinanModel) {
        binding.izinForm.visibility = View.VISIBLE
        kategoriPerizinanItemModel = kategoriPerizinanModel.data

        val kategoriName = ArrayList<String>()
        kategoriName.add(getString(R.string.kategori_izin_text))
        kategoriPerizinanItemModel.forEach{ value->
            kategoriName.add(value.nama_kategori!!)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kategoriName
        )
        binding.kategoriIzin.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessCreateIzin(createPerizinanModel: CreatePerizinanModel) {
        Alerter.create(this)
            .setTitle("Izin berhasil diajukan")
            .setText(createPerizinanModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        binding.buttonSubmit.text = "Ajukan Izin"
        binding.buttonSubmit.isEnabled = true

        binding.kategoriIzin.setSelection(0)
        binding.tanggalMulai.text = null
        binding.tanggalSelesai.text = null
        binding.filePendukung.text = "Upload File Pengajuan Izin"
        filePendukung = Uri.EMPTY
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.text = "Ajukan Izin"
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