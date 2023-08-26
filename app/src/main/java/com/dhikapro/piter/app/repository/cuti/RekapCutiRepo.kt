package com.dhikapro.piter.app.repository.cuti

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.cuti.RekapCutiInterface
import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class RekapCutiRepo(private val rekapCutiInterface: RekapCutiInterface) {

    fun rekapCuti(token: String){
        NetworkConfig.getService()
            .rekapCuti(token)
            .enqueue(object : retrofit2.Callback<RekapPerizinanModel>{
                override fun onResponse(
                    call: Call<RekapPerizinanModel>,
                    response: Response<RekapPerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapCutiInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapCutiInterface.onSuccessGetRekapCuti(body)
                    }
                    if(body?.success == false){
                        rekapCutiInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<RekapPerizinanModel>, t: Throwable) {
                    rekapCutiInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}