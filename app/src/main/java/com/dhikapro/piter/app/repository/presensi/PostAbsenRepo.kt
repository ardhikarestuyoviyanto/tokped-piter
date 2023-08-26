package com.dhikapro.piter.app.repository.presensi

import android.content.Context
import android.net.Uri
import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.presensi.AbsenInterface
import com.dhikapro.piter.app.models.presensi.AbsenModel
import com.dhikapro.piter.app.network.NetworkConfig
import com.dhikapro.piter.app.utils.UriToFileUpload
import okhttp3.MultipartBody
import retrofit2.Call
import retrofit2.Response

class PostAbsenRepo(private val absenInterface: AbsenInterface, private val context: Context) {

    fun postAbsenMasuk(
        fotoAbsen: MultipartBody.Part,
        token: String,
    ){
        NetworkConfig.getService()
            .absenMasuk(
                fotoAbsen,
                token
            )
            .enqueue(object : retrofit2.Callback<AbsenModel>{
                override fun onResponse(call: Call<AbsenModel>, response: Response<AbsenModel>) {
                    val body = response.body()
                    if(body?.success == false){
                        absenInterface.onBadRequest(body.message!!)
                    }
                    if(body?.success == true){
                        absenInterface.onSuccessAbsen(body)
                    }
                }

                override fun onFailure(call: Call<AbsenModel>, t: Throwable) {
                    absenInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun postAbsenPulang(
        fotoAbsen: MultipartBody.Part,
        token: String,
    ){
        NetworkConfig.getService()
            .absenPulang(
                fotoAbsen,
                token
            )
            .enqueue(object : retrofit2.Callback<AbsenModel>{
                override fun onResponse(call: Call<AbsenModel>, response: Response<AbsenModel>) {
                    val body = response.body()
                    if(body?.success == false){
                        absenInterface.onBadRequest(body.message!!)
                    }
                    if(body?.success == true){
                        absenInterface.onSuccessAbsen(body)
                    }
                }

                override fun onFailure(call: Call<AbsenModel>, t: Throwable) {
                    absenInterface.onBadRequest(Company.connectionFailure)
                }

            })

    }

}