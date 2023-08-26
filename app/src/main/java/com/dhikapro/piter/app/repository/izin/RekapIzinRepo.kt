package com.dhikapro.piter.app.repository.izin

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.izin.RekapIzinInterface
import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class RekapIzinRepo(private val rekapIzinInterface: RekapIzinInterface) {

    fun getRekapIzin(token: String){
        NetworkConfig.getService()
            .rekapIzin(token)
            .enqueue(object : retrofit2.Callback<RekapPerizinanModel>{
                override fun onResponse(
                    call: Call<RekapPerizinanModel>,
                    response: Response<RekapPerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapIzinInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapIzinInterface.onSuccessGetRekapIzin(body)
                    }
                    if(body?.success == false){
                        rekapIzinInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<RekapPerizinanModel>, t: Throwable) {
                    rekapIzinInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}