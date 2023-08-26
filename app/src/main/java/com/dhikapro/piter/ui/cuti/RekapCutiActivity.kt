package com.dhikapro.piter.ui.cuti

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.cuti.RekapCutiInterface
import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel
import com.dhikapro.piter.app.repository.cuti.RekapCutiRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityRekapCutiBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.cuti.adapter.RekapCutiAdapter
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tapadoo.alerter.Alerter

class RekapCutiActivity : AppCompatActivity(), RekapCutiInterface {

    private lateinit var binding: ActivityRekapCutiBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var rekapCutiRepo: RekapCutiRepo
    private lateinit var navigationView: BottomNavigationView
    private lateinit var rekapCutiAdapter: RekapCutiAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRekapCutiBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        navigationMenu()

        sharedPreference = SharedPreference(this)
        rekapCutiRepo = RekapCutiRepo(this)

        rekapCutiRepo.rekapCuti(sharedPreference.getTokenJWT()!!)
    }

    private fun navigationMenu(){
        navigationView =binding.navCuti
        navigationView.selectedItemId = R.id.rekap_cuti_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_cuti_menu->{
                    startActivity(Intent(this, CutiPageActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_cuti_menu->{
                    return@setOnItemSelectedListener true
                }
                else->{
                    return@setOnItemSelectedListener true
                }
            }
        }
    }

    override fun onSuccessGetRekapCuti(rekapPerizinanModel: RekapPerizinanModel) {
        rekapCutiAdapter = RekapCutiAdapter(this)
        val rv: RecyclerView=binding.recycleView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = rekapCutiAdapter
        rekapCutiAdapter.setDataList(rekapPerizinanModel.data)
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