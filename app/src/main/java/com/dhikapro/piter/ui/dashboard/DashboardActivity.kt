@file:Suppress("DEPRECATION")

package com.dhikapro.piter.ui.dashboard

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.view.LayoutInflater
import android.widget.LinearLayout
import com.bumptech.glide.Glide
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.dashboard.DashboardInterface
import com.dhikapro.piter.app.models.dashboard.InfoLimitModel
import com.dhikapro.piter.app.models.dashboard.LogoutModel
import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.repository.dashboard.DashboardRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityDashboardBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.cuti.CutiPageActivity
import com.dhikapro.piter.ui.info.DetailInfoActivity
import com.dhikapro.piter.ui.info.ListInfoActivity
import com.dhikapro.piter.ui.izin.IzinPageActivity
import com.dhikapro.piter.ui.lembur.LemburPageActivity
import com.dhikapro.piter.ui.lkh.LkhPageActivity
import com.dhikapro.piter.ui.presensi.LocationActivity
import com.dhikapro.piter.ui.presensi.RekapAbsenActivity
import com.dhikapro.piter.ui.profile.ProfileActivity
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.tapadoo.alerter.Alerter
import java.text.SimpleDateFormat
import java.util.Calendar

class DashboardActivity : AppCompatActivity(), DashboardInterface {

    private lateinit var binding:ActivityDashboardBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var dashboardRepo: DashboardRepo
    private lateinit var bottomSheetDialog: BottomSheetDialog
    private val handler = Handler()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDashboardBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        dashboardRepo = DashboardRepo(this)

        val dateTimeNow = object : Runnable {
            override fun run(){
                binding.dateTimeNow.text = getDateTimeNow()
                handler.postDelayed(this, 1000)
            }
        }
        handler.postDelayed(dateTimeNow, 1000)
        dashboardRepo.getProfile(sharedPreference.getTokenJWT()!!)
        dashboardRepo.getInfoLimit(3, sharedPreference.getTokenJWT()!!)

        // menu
        binding.presensiMenu.setOnClickListener {
            showMenuPresensi()
        }
        binding.perizinanMenu.setOnClickListener {
            showMenuPerizinan()
        }
        binding.lkhMenu.setOnClickListener {
            startActivity(Intent(this, LkhPageActivity::class.java))
            overridePendingTransition(0, 0)
        }
        binding.lemburMenu.setOnClickListener {
            startActivity(Intent(this, LemburPageActivity::class.java))
            overridePendingTransition(0, 0)
        }
        binding.profileMenu.setOnClickListener {
            startActivity(Intent(this, ProfileActivity::class.java))
            overridePendingTransition(0, 0)
        }
        binding.logoutMenu.setOnClickListener {
            dashboardRepo.logout(sharedPreference.getTokenJWT()!!)
        }
        binding.moreInformasi.setOnClickListener {
            startActivity(Intent(this, ListInfoActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    private fun showMenuPresensi(){
        bottomSheetDialog = BottomSheetDialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.bs_presensi_menu, findViewById(R.id.design_bottom_sheet), false)
        view.findViewById<LinearLayout>(R.id.presensi_masuk_menu).setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("type_absen", "masuk")
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.presensi_pulang_menu).setOnClickListener {
            val intent = Intent(this, LocationActivity::class.java)
            intent.putExtra("type_absen", "pulang")
            startActivity(intent)
        }
        view.findViewById<LinearLayout>(R.id.rekap_absen_menu).setOnClickListener {
            startActivity(Intent(this, RekapAbsenActivity::class.java))
            overridePendingTransition(0, 0)
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    private fun showMenuPerizinan(){
        bottomSheetDialog = BottomSheetDialog(this)
        val layoutInflater = LayoutInflater.from(this)
        val view = layoutInflater.inflate(R.layout.bs_perizinan_menu, findViewById(R.id.design_bottom_sheet), false)
        view.findViewById<LinearLayout>(R.id.ajukan_cuti_menu).setOnClickListener {
            startActivity(Intent(this,CutiPageActivity::class.java))
            overridePendingTransition(0, 0)
        }
        view.findViewById<LinearLayout>(R.id.ajukan_izin_menu).setOnClickListener {
            startActivity(Intent(this, IzinPageActivity::class.java))
            overridePendingTransition(0, 0)
        }
        bottomSheetDialog.setContentView(view)
        bottomSheetDialog.show()
    }

    @SuppressLint("SimpleDateFormat")
    private fun getDateTimeNow():String{
        val calendar = Calendar.getInstance()
        val date = SimpleDateFormat("dd - M - yyyy")
        val time = SimpleDateFormat("HH:mm:ss")
        return "Tgl : "+date.format(calendar.time)+" | Jam : "+time.format(calendar.time)
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessGetProfile(profileModel: ProfileModel) {
        val payloadToken =  profileModel.payload_token
        var wfhStatus = ""
        wfhStatus = if(payloadToken?.status_wfh == true){
            "WFH (Work From Home)"
        }else{
            "WFO (Work From Office)"
        }
        binding.namaPegawai.text = payloadToken?.nama_pegawai
        binding.namaUnitKerja.text = payloadToken?.unit_kerja?.nama_unit_kerja
        binding.usernameNidn.text = "@"+payloadToken?.username
        binding.statusPegawaiWfh.text = payloadToken?.status_pegawai+" / "+wfhStatus
        binding.jamMasuk.text = profileModel.jam_kerja?.masuk
        binding.jamPulang.text = profileModel.jam_kerja?.pulang
        Glide.with(this).load(payloadToken?.foto).into(binding.profileImage)

        sharedPreference.setStatusWfh(payloadToken?.status_wfh!!)
        sharedPreference.setPegawaiID(payloadToken.id!!)
        sharedPreference.setUnitKerjaID(payloadToken.unit_kerja?.id!!)
    }

    override fun onSuccessGetInfoLimit(infoLimitModel: InfoLimitModel) {
        if(infoLimitModel.data?.getOrNull(0)?.judul!= null){
            val res = infoLimitModel.data.getOrNull(0)!!
            binding.judul1.text = infoLimitModel.data[0].judul
            binding.subJudul1.text = infoLimitModel.data[0].tanggal

            binding.judul1.setOnClickListener {
                val intent = Intent(this, DetailInfoActivity::class.java)
                intent.putExtra("info_id", res.id)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        if(infoLimitModel.data?.getOrNull(1)?.judul!=null){
            val res = infoLimitModel.data.getOrNull(1)!!
            binding.judul2.text = infoLimitModel.data[1].judul
            binding.subJudul2.text = infoLimitModel.data[1].tanggal

            binding.judul2.setOnClickListener {
                val intent = Intent(this, DetailInfoActivity::class.java)
                intent.putExtra("info_id", res.id)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
        if(infoLimitModel.data?.getOrNull(2)?.judul!=null){
            val res = infoLimitModel.data.getOrNull(2)!!
            binding.judul3.text = infoLimitModel.data[2].judul
            binding.subJudul3.text = infoLimitModel.data[2].tanggal

            binding.judul3.setOnClickListener {
                val intent = Intent(this, DetailInfoActivity::class.java)
                intent.putExtra("info_id", res.id)
                startActivity(intent)
                overridePendingTransition(0, 0)
            }
        }
    }

    override fun onSuccessLogout(logoutModel: LogoutModel) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", logoutModel.message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Gagal Memuat")
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