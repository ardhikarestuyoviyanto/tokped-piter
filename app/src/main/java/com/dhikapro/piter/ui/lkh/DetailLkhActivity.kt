package com.dhikapro.piter.ui.lkh

import android.annotation.SuppressLint
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ArrayAdapter
import androidx.core.content.ContextCompat
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.lkh.DetailLkhInterface
import com.dhikapro.piter.app.models.lkh.DetailLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhItem
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.UpdateLkhModel
import com.dhikapro.piter.app.repository.lkh.DetailLkhRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityDetailLkhBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.tapadoo.alerter.Alerter

class DetailLkhActivity : AppCompatActivity(), DetailLkhInterface {

    private lateinit var binding: ActivityDetailLkhBinding
    private lateinit var sharedPreference: SharedPreference
    private lateinit var detailLkhRepo: DetailLkhRepo
    private var kategoriLkhItemData = emptyList<KategoriLkhItem>()

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityDetailLkhBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        sharedPreference = SharedPreference(this)
        detailLkhRepo = DetailLkhRepo(this)

        if(intent.getStringExtra("lkh_id") == null){
            startActivity(Intent(this, RekapLkhActivity::class.java))
            overridePendingTransition(0, 0)
        }

        binding.cardStatusAccLkh.visibility = View.GONE
        binding.lkhForm.visibility = View.GONE

        detailLkhRepo.getKategoriLkh(sharedPreference.getTokenJWT()!!)

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
                    detailLkhRepo.updateLkh(
                        binding.tanggal.text.toString(),
                        binding.kegiatan.text.toString(),
                        kategoriLkhID,
                        intent.getStringExtra("lkh_id")!!,
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
    override fun onSuccessUpdateLkh(updateLkhModel: UpdateLkhModel) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(updateLkhModel.message!!)
            .setBackgroundColorRes(R.color.success)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Lkh"

        detailLkhRepo.getDetailLkh(
            intent.getStringExtra("lkh_id")!!,
            sharedPreference.getTokenJWT()!!
        )
    }

    @SuppressLint("SetTextI18n")
    override fun onSuccessGetDetailLkh(detailLkhModel: DetailLkhModel) {
        binding.cardStatusAccLkh.visibility = View.VISIBLE
        binding.lkhForm.visibility = View.VISIBLE

        if(detailLkhModel.data == null){
            startActivity(Intent(this, RekapLkhActivity::class.java))
            overridePendingTransition(0, 0)
        }

        if(detailLkhModel.data?.status == "1"){
            binding.cardStatusAccLkh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_success)
            binding.statusAccLkhText.text = "Lkh Ini Sudah Approval"
        }else{
            binding.cardStatusAccLkh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_danger)
            binding.statusAccLkhText.text = "Lkh Ini Belum Approval"
        }

        binding.tanggal.setText(detailLkhModel.data?.tanggal)
        binding.kegiatan.setText(detailLkhModel.data?.kegiatan)
        kategoriLkhItemData.forEachIndexed{ i, value->
            if(value.id == detailLkhModel.data?.kategori_lkh_id){
                val selectionIndex = i + 1
                if (selectionIndex >= 0 && selectionIndex < binding.kategoriLkh.count) {
                    binding.kategoriLkh.setSelection(selectionIndex)
                } else {
                    Log.e("ERROR", "Index out bound $selectionIndex")
                }

            }
        }
    }

    override fun onSuccessGetKategoriLkh(kategoriLkhModel: KategoriLkhModel) {
        detailLkhRepo.getDetailLkh(intent.getStringExtra("lkh_id")!!, sharedPreference.getTokenJWT()!!)
        kategoriLkhItemData = kategoriLkhModel.data!!

        val kategoriName = ArrayList<String>()
        kategoriName.add(getString(R.string.kategori_lkh_text))
        kategoriLkhModel.data?.forEach{ value->
            kategoriName.add(value.nama_kategori!!)
        }

        val adapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            kategoriName
        )
        binding.kategoriLkh.adapter = adapter
    }

    @SuppressLint("SetTextI18n")
    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
        binding.buttonSubmit.isEnabled = true
        binding.buttonSubmit.text = "Update Lkh"
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}