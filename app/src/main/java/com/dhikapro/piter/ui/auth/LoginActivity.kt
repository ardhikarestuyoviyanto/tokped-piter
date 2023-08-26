package com.dhikapro.piter.ui.auth

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.ApplicationInfo
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.auth.LoginInterface
import com.dhikapro.piter.app.models.auth.LoginModel
import com.dhikapro.piter.app.repository.auth.LoginRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.PermissionCheck
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityLoginBinding
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.tapadoo.alerter.Alerter

class LoginActivity : AppCompatActivity(), LoginInterface {

    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginRepo: LoginRepo
    private lateinit var sharedPreference: SharedPreference

    @SuppressLint("HardwareIds")
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityLoginBinding.inflate(layoutInflater)
        DisableDarkMode(this).disable()
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        loginRepo = LoginRepo(this)
        sharedPreference = SharedPreference(this)

        // check permission
        if(!PermissionCheck().checkPermissions(this)){
            disabledForm("Access Denied")
            sharedPreference.removeToken()
        }

        binding.buttonLogin.setOnClickListener {
            when{
                binding.usernameNidn.text.isEmpty()->{
                    binding.usernameNidn.error = getString(R.string.required)
                }
                binding.password.text.isEmpty()->{
                    binding.password.error = getString(R.string.required)
                }
                else->{
                    disabledForm(getString(R.string.loading))
                    val deviceID = Settings.Secure.getString(contentResolver, Settings.Secure.ANDROID_ID)
                    loginRepo.login(
                        binding.usernameNidn.text.toString(),
                        binding.password.text.toString(),
                        deviceID
                    )
                }
            }
        }
        binding.lupaPassword.setOnClickListener {
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://api.whatsapp.com/send?phone=${getString(R.string.no_admin_wa)}")))
        }

        if(intent.getStringExtra("onUnAuthorized") != null){
            binding.root.snackbar(intent.getStringExtra("onUnAuthorized").toString())
        }

        autoLogin()
    }

    private fun enableForm(){
        binding.buttonLogin.text = getString(R.string.login)
        binding.buttonLogin.isEnabled =true
    }

    private fun disabledForm(buttonLabel: String){
        binding.buttonLogin.text = buttonLabel
        binding.buttonLogin.isEnabled = false
    }

    private fun autoLogin(){
        if(sharedPreference.getTokenJWT().toString().isNotEmpty()){
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(0, 0)
        }
    }

    override fun onSuccessLogin(loginModel: LoginModel) {
        enableForm()
        sharedPreference.setTokenJWT(loginModel.access_token!!)
        startActivity(Intent(this, DashboardActivity::class.java))
        overridePendingTransition(0, 0)
    }

    override fun onBadLogin(message: String) {
        enableForm()
        Alerter.create(this)
            .setTitle("Login Akun Gagal")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if (requestCode == 123) {
            var allPermissionsGranted = true
            for (result in grantResults) {
                if (result != PackageManager.PERMISSION_GRANTED) {
                    allPermissionsGranted = false
                    break
                }
            }

            if (allPermissionsGranted) {
                enableForm()
            } else {
                Alerter.create(this)
                    .setTitle("Izinkan Semua Hak Akses Yang Diperlukan")
                    .setText(PermissionCheck().showPermissionsToRequest(this))
                    .setBackgroundColorRes(R.color.danger)
                    .show()

            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }
}