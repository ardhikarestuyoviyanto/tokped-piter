package com.dhikapro.piter.ui.izin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.izin.RekapIzinInterface
import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel
import com.dhikapro.piter.app.repository.izin.RekapIzinRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityRekapIzinBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.dhikapro.piter.ui.izin.adapter.RekapIzinAdapter
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.tapadoo.alerter.Alerter

class RekapIzinActivity : AppCompatActivity(), RekapIzinInterface {

    private lateinit var binding: ActivityRekapIzinBinding
    private lateinit var rekapIzinRepo: RekapIzinRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var rekapIzinAdapter: RekapIzinAdapter
    private lateinit var navigationView: BottomNavigationView

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityRekapIzinBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        rekapIzinRepo = RekapIzinRepo(this)
        rekapIzinRepo.getRekapIzin(sharedPreference.getTokenJWT()!!)
        navigationMenu()

    }

    private fun navigationMenu(){
        navigationView =binding.navIzin
        navigationView.selectedItemId = R.id.rekap_izin_menu
        navigationView.setOnItemSelectedListener { item->
            when (item.itemId){
                R.id.tambah_izin_menu->{
                    startActivity(Intent(this, IzinPageActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.dashboard_menu->{
                    startActivity(Intent(this, DashboardActivity::class.java))
                    overridePendingTransition(0, 0)
                    return@setOnItemSelectedListener true
                }
                R.id.rekap_izin_menu->{
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

    override fun onSuccessGetRekapIzin(rekapPerizinanModel: RekapPerizinanModel) {
        rekapIzinAdapter = RekapIzinAdapter(this)
        val rv: RecyclerView =binding.recycleView
        rv.layoutManager = LinearLayoutManager(this)
        rv.adapter = rekapIzinAdapter
        rekapIzinAdapter.setDataList(rekapPerizinanModel.data)
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