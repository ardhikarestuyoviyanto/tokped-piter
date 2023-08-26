package com.dhikapro.piter.ui.info

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import com.bumptech.glide.Glide
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.info.InfoDetailInterface
import com.dhikapro.piter.app.models.info.InfoDetailModel
import com.dhikapro.piter.app.repository.info.InfoRepoDetail
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityDetailInfoBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.tapadoo.alerter.Alerter

class DetailInfoActivity : AppCompatActivity(), InfoDetailInterface {

    private lateinit var binding: ActivityDetailInfoBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var infoRepoDetail: InfoRepoDetail

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailInfoBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        infoRepoDetail = InfoRepoDetail(this)

        if(intent.getStringExtra("info_id") == null){
            startActivity(Intent(this, ListInfoActivity::class.java))
            overridePendingTransition(0, 0)
        }

        infoRepoDetail.getInfoDetail(
            intent.getStringExtra("info_id"),
            sharedPreference.getTokenJWT()!!
        )

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    override fun onSuccessGetInfoDetail(infoDetailModel: InfoDetailModel) {
        binding.judul.text = infoDetailModel.data?.judul
        binding.tanggal.text = infoDetailModel.data?.tanggal
        binding.namaKategori.text = infoDetailModel.data?.nama_kategori

        if(infoDetailModel.data?.banner_foto != null){
            binding.bannerFoto.visibility = View.VISIBLE
            Glide.with(this).load(infoDetailModel.data?.banner_foto).into(binding.bannerFoto)
        }else{
            binding.bannerFoto.visibility = View.GONE
        }

        if(infoDetailModel.data?.lampiran_file != null){
            binding.layerButtonLampiran.visibility = View.VISIBLE
            binding.buttonLampiran.setOnClickListener {
                val intent = Intent(Intent.ACTION_VIEW, Uri.parse(infoDetailModel.data.lampiran_file))
                startActivity(intent)
            }
        }else{
            binding.layerButtonLampiran.visibility = View.GONE
        }

        binding.isi.text = HtmlCompat.fromHtml(
            infoDetailModel.data?.isi!!,
            HtmlCompat.FROM_HTML_MODE_COMPACT
        )
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