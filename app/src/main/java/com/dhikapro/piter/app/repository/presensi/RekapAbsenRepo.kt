package com.dhikapro.piter.app.repository.presensi

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.presensi.RekapAbsenInterface
import com.dhikapro.piter.app.models.presensi.RekapAbsenModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class RekapAbsenRepo(private val rekapAbsenInterface: RekapAbsenInterface) {

    fun rekapAbsen(bulan: String, token: String){
        NetworkConfig.getService()
            .rekapAbsen(bulan, token)
            .enqueue(object : retrofit2.Callback<RekapAbsenModel>{
                override fun onResponse(
                    call: Call<RekapAbsenModel>,
                    response: Response<RekapAbsenModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapAbsenInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapAbsenInterface.onSuccessGetRekapAbsen(body)
                    }
                    if(body?.success == false){
                        rekapAbsenInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<RekapAbsenModel>, t: Throwable) {
                    rekapAbsenInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}