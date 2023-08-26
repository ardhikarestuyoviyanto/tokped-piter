package com.dhikapro.piter.ui.lkh

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.lkh.LkhPageInterface
import com.dhikapro.piter.app.models.lkh.CreateLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhItem
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.StatusLkhModel
import com.dhikapro.piter.app.repository.lkh.LkhPageRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityLkhPageBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.datepicker.MaterialDatePicker
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class LkhPageActivity : AppCompatActivity(), LkhPageInterface {

    private lateinit var binding: ActivityLkhPageBinding
    private lateinit var lkhPageRepo: LkhPageRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var navigationView: BottomNavigationView
    private var kategoriLkhItemData = emptyList<KategoriLkhItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLkhPageBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        lkhPageRepo = LkhPageRepo(this)
        navigationView = BottomNavigationView(this)
        navigationMenu()

        lkhPageRepo.getKategoriLkh(sharedPreference.getTokenJWT()!!)
        lkhPageRepo.getStatusLkh(sharedPreference.getTokenJWT()!!)
        binding.cardStatusLkh.visibility = View.GONE
        binding.lkhForm.visibility = View.GONE

        binding.tanggal.setOnClickListener {
            showDatePicker()
        }
        binding.buttonSubmit.setOnClickListener {
            when{
                binding.tanggal.text.isEmpty()->{
                    binding.tanggal.error = getString(R.string.required)
                }
                binding.kategoriLkh.selectedItem.equals("PILIH KATEGORI LKH")->{
                    binding.root.snackbar("Pilih Kategori Lkh ...")
                }
                binding.kegiatan.text.isEmpty()->{
                    binding.kegiatan.error = getString(R.string.required)
                }
                else->{
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSubmit.text = getString(R.string.loading)
                    var kategoriLkhID = ""
                    kategoriLkhItemData.forEach{item->
                        if(item.nama_kategori == binding.kategoriLkh.selectedItem.toString()){
                            kategoriLkhID = item.id!!
                        }
                    }
                    lkhPageRepo.createLkh(
                        binding.tanggal.text.toString(),
                        binding.kegiatan.text.toString(),
                        kategoriLkhID,
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

    private fun navigationMenu(){
        navigationView =binding.navLkh
        navigationView.selectedItemId = R.id.tambah_lkh_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_lkh_menu->{
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_lkh_menu->{
                    startActivity(Intent(this, RekapLkhActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                else->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    override fun onSuccessGetKategoriLkh(kategoriLkhModel: KategoriLkhModel) {
        binding.lkhForm.visibility = View.VISIBLE
        kategoriLkhItemData = kategoriLkhModel.data!!

        val kategoriName = ArrayList<String>()
        kategoriName.add(getString(R.string.kategori_lkh_text))
        kategoriLkhModel.data.forEach{ value->
            kategoriName.add(value.nama_kategori!!)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kategoriName
        )
        binding.kategoriLkh.adapter = adapter
    }

    override fun onSuccessGetStatusLkh(statusLkhModel: StatusLkhModel) {
        binding.cardStatusLkh.visibility = View.VISIBLE
        binding.statusLkhText.text = statusLkhModel.data.message
        if(statusLkhModel.data.success == true){
            binding.cardStatusLkh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_success)
        }else{
            binding.cardStatusLkh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_danger)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessCreateLkh(createLkhModel: CreateLkhModel) {
        Alerter.create(this)
            .setTitle("Lkh Berhasil Disimpan")
            .setText(createLkhModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Simpan Lkh"
        lkhPageRepo.getStatusLkh(sharedPreference.getTokenJWT()!!)

        binding.tanggal.text = null
        binding.kategoriLkh.setSelection(0)
        binding.kegiatan.text = null
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Simpan Lkh"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}