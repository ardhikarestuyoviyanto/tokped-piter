package com.dhikapro.piter.ui.info

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.info.InfoInterface
import com.dhikapro.piter.app.models.info.InfoModel
import com.dhikapro.piter.app.models.info.KategoriModel
import com.dhikapro.piter.app.repository.info.InfoRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityListInfoBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.info.adapter.KategoriAdapter
import com.dhikapro.piter.ui.info.adapter.ListInfoAdapter
import com.tapadoo.alerter.Alerter

class ListInfoActivity : AppCompatActivity(), InfoInterface {

    private lateinit var binding: ActivityListInfoBinding
    private lateinit var infoRepo: InfoRepo
    private lateinit var sharedPreference: SharedPreference
    private lateinit var kategoriAdapter: KategoriAdapter
    private lateinit var listInfoAdapter: ListInfoAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityListInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        infoRepo = InfoRepo(this)

        infoRepo.getInfoAll(null, sharedPreference.getTokenJWT()!!)
        infoRepo.getKategori(sharedPreference.getTokenJWT()!!)

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccessGetKategori(kategoriModel: KategoriModel) {
        kategoriAdapter = KategoriAdapter(this, infoRepo, sharedPreference.getTokenJWT()!!)
        val rv = binding.kategoriRv
        rv.adapter = kategoriAdapter
        kategoriAdapter.setDataList(kategoriModel.data!!)
    }

    override fun onSuccessGetInfo(infoModel: InfoModel) {
        listInfoAdapter = ListInfoAdapter(this)
        val rv = binding.listInfoRv
        rv.adapter = listInfoAdapter
        listInfoAdapter.setDataList(infoModel.data!!)

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