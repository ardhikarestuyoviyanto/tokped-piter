package com.dhikapro.piter.ui.presensi

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.presensi.RekapAbsenInterface
import com.dhikapro.piter.app.models.presensi.RekapAbsenModel
import com.dhikapro.piter.app.repository.presensi.RekapAbsenRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.*
import com.dhikapro.piter.databinding.ActivityRekapAbsenBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.presensi.adapter.RekapAbsenAdapter
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapadoo.alerter.Alerter

class RekapAbsenActivity : AppCompatActivity(), RekapAbsenInterface {

    private lateinit var binding: ActivityRekapAbsenBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var rekapAbsenRepo: RekapAbsenRepo
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var rekapAbsenAdapter: RekapAbsenAdapter
    private var monthFormat:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRekapAbsenBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreference = SharedPreference(this)
        rekapAbsenRepo = RekapAbsenRepo(this)
        rekapAbsenAdapter = RekapAbsenAdapter(this)

        binding.bulan.setOnClickListener {
            showMonthPicker()
        }
        binding.buttonSubmit.setOnClickListener {
            when{
                binding.bulan.text.isEmpty()->{
                    binding.bulan.error = getString(R.string.required)
                }
                else->{
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSubmit.text = getString(R.string.loading)
                    rekapAbsenRepo.rekapAbsen(
                        this.monthFormat,
                        sharedPreference.getTokenJWT()!!
                    )

                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun showMonthPicker(){
        MonthYearPickerDialog().apply {
            setListener { _, year, month, _ ->
                monthFormat = convertToMonthFormat(year, month)
                binding.bulan.setText(
                    convertToHumanMonth(year, month)
                )
            }
            show(supportFragmentManager, "Pilih Bulan")
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessGetRekapAbsen(rekapAbsenModel: RekapAbsenModel) {
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Lihat Rekap Presensi"

        // bottom sheet
        bottomSheetDialog = BottomSheetDialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.bs_rekap_absen, findViewById(R.id.design_bottom_sheet), false)
        bottomSheetDialog.setContentView(view)
        view.findViewById<TextView>(R.id.bulan_bs).text = binding.bulan.text.toString()
        view.findViewById<Button>(R.id.button_unduh).setOnClickListener {
            Toast.makeText(this, "Mengunduh Laporan ...", Toast.LENGTH_SHORT).show()
            val url = ExportUrl(
                sharedPreference.getUnitKerjaID()!!,
                sharedPreference.getPegawaiID()!!,
                this.monthFormat
            ).rekapAbsen()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        // rv
        val rv = view.findViewById<RecyclerView>(R.id.recycleView)
        rv.adapter = rekapAbsenAdapter
        rv.layoutManager = LinearLayoutManager(this)
        rekapAbsenAdapter.setDataList(rekapAbsenModel.data!!)
        bottomSheetDialog.show()

        // alert faq
        val lf: LayoutInflater = LayoutInflater.from(this)
        val alertDialog = AlertDialog.Builder(this)
        view.findViewById<TextView>(R.id.faq).setOnClickListener {
            val alertView: View = lf.inflate(R.layout.show_faq_rekap_absen, null)
            alertDialog.setTitle("Petunjuk Rekap Absen")
            alertDialog.setPositiveButton("Oke"){_,_->}
            alertDialog.setView(alertView)
            alertDialog.show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Lihat Rekap Presensi"

        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}