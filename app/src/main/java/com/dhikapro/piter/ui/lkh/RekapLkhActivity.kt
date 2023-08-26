package com.dhikapro.piter.ui.lkh

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
import com.dhikapro.piter.app.interfaces.lkh.RekapLkhInterface
import com.dhikapro.piter.app.models.lkh.DeleteLkhModel
import com.dhikapro.piter.app.models.lkh.RekapLkhModel
import com.dhikapro.piter.app.repository.lkh.RekapLkhRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.*
import com.dhikapro.piter.databinding.ActivityRekapLkhBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.dhikapro.piter.ui.lkh.adapter.RekapLkhAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapadoo.alerter.Alerter

class RekapLkhActivity : AppCompatActivity(), RekapLkhInterface {

    private lateinit var binding: ActivityRekapLkhBinding
    private lateinit var rekapLkhRepo: RekapLkhRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var navigationView: BottomNavigationView
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private lateinit var rekapLkhAdapter: RekapLkhAdapter
    private var monthFormat:String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRekapLkhBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        rekapLkhRepo = RekapLkhRepo(this)
        sharedPreference = SharedPreference(this)
        rekapLkhAdapter = RekapLkhAdapter(rekapLkhRepo, this, sharedPreference.getTokenJWT()!!)

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
                    rekapLkhRepo.getRekapLkh(
                        this.monthFormat,
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }
        navigationMenu()
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

    private fun navigationMenu(){
        navigationView =binding.navLkh
        navigationView.selectedItemId = R.id.rekap_lkh_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_lkh_menu->{
                    startActivity(Intent(this, LkhPageActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_lkh_menu->{
                    return@setOnItemSelectedListener true
                }
                else->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    @SuppressLint("SetTextI18n", "MissingInflatedId")
    override fun onSuccessGetRekapLkh(rekapLkhModel: RekapLkhModel) {
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Lihat Rekap Lkh"

        // bottom sheet
        bottomSheetDialog = BottomSheetDialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.bs_rekap_lkh, findViewById(R.id.design_bottom_sheet), false)
        bottomSheetDialog.setContentView(view)
        view.findViewById<TextView>(R.id.bulan_bs).text = binding.bulan.text.toString()
        view.findViewById<Button>(R.id.button_unduh).setOnClickListener {
            Toast.makeText(this, "Mengunduh Laporan ...", Toast.LENGTH_SHORT).show()
            val url = ExportUrl(
                sharedPreference.getUnitKerjaID()!!,
                sharedPreference.getPegawaiID()!!,
                this.monthFormat
            ).rekapLkh()
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(url)))
        }
        // rv
        val rv = view.findViewById<RecyclerView>(R.id.recycleView)
        rv.adapter = rekapLkhAdapter
        rv.layoutManager = LinearLayoutManager(this)
        rekapLkhAdapter.setDataList(rekapLkhModel.data!!)
        bottomSheetDialog.show()

        // alert faq
        val lf: LayoutInflater = LayoutInflater.from(this)
        val alertDialog = AlertDialog.Builder(this)
        view.findViewById<TextView>(R.id.faq).setOnClickListener {
            val alertView: View = lf.inflate(R.layout.show_faq_rekap_lkh, null)
            alertDialog.setTitle("Petunjuk Rekap Lkh")
            alertDialog.setPositiveButton("Oke"){_,_->}
            alertDialog.setView(alertView)
            alertDialog.show()
        }
    }

    override fun onSuccessDeleteLkh(deleteLkhModel: DeleteLkhModel) {
        Alerter.create(this)
            .setTitle("Data Lkh Berhasil Dihapus")
            .setText(deleteLkhModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Lihat Rekap Lkh"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }
}