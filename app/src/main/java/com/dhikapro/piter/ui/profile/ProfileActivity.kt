package com.dhikapro.piter.ui.profile

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.bumptech.glide.Glide
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.profile.ProfileInterface
import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.models.profile.UbahPasswordModel
import com.dhikapro.piter.app.repository.profile.ProfileRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.databinding.ActivityProfileBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.tapadoo.alerter.Alerter

class ProfileActivity : AppCompatActivity(), ProfileInterface {

    private lateinit var binding: ActivityProfileBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var profileRepo: ProfileRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()

        sharedPreference = SharedPreference(this)
        profileRepo = ProfileRepo(this)

        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        profileRepo.getProfile(sharedPreference.getTokenJWT()!!)

        binding.buttonSubmit.setOnClickListener {
            when{
                binding.passwordLama.text.isEmpty()->{
                    binding.passwordLama.error = getString(R.string.required)
                }
                binding.passwordBaru.text.isEmpty()->{
                    binding.passwordBaru.error = getString(R.string.required)
                }
                else->{
                    binding.buttonSubmit.isEnabled = false
                    binding.buttonSubmit.text = getString(R.string.loading)
                    profileRepo.updatePassword(
                        binding.passwordLama.text.toString(),
                        binding.passwordBaru.text.toString(),
                        sharedPreference.getTokenJWT()!!
                    )
                }
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessGetProfile(profileModel: ProfileModel) {
        val payloadToken = profileModel.payload_token
        binding.nidn.isEnabled = false
        binding.username.isEnabled = false
        binding.namaPegawai.isEnabled = false
        binding.namaUnitKerja.isEnabled = false
        binding.statusPegawaiWfh.isEnabled = false
        binding.statusPegawai.isEnabled = false
        Glide.with(this).load(payloadToken?.foto!!).into(binding.foto)

        var wfhStatus = ""
        wfhStatus = if(payloadToken?.status_wfh == true){
            "WFH (Work From Home)"
        }else{
            "WFO (Work From Office)"
        }

        binding.nidn.setText(payloadToken?.nidn)
        binding.username.setText("@"+payloadToken?.username)
        binding.namaPegawai.setText(payloadToken?.nama_pegawai)
        binding.namaUnitKerja.setText(payloadToken?.unit_kerja?.nama_unit_kerja)
        binding.statusPegawaiWfh.setText(wfhStatus)
        binding.statusPegawai.setText(payloadToken?.status_pegawai)
    }

    @SuppressLint("SetTextI18n")
    override fun onUpdatePasswordResponse(ubahPasswordModel: UbahPasswordModel) {
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Ubah Password"

        if(ubahPasswordModel.success){
            Alerter.create(this)
                .setTitle("Berhasil Mengubah Password")
                .setText(ubahPasswordModel.message.toString())
                .setBackgroundColorRes(R.color.success)
                .show()
        }else{
            Alerter.create(this)
                .setTitle("Gagal Mengubah Password")
                .setText(ubahPasswordModel.message.toString())
                .setBackgroundColorRes(R.color.danger)
                .show()
        }
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
    }
}