package com.dhikapro.piter.app.repository.lembur

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lembur.RekapLemburInterface
import com.dhikapro.piter.app.models.lembur.DeleteLemburModel
import com.dhikapro.piter.app.models.lembur.RekapLemburModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class RekapLemburRepo(private val rekapLemburInterface: RekapLemburInterface) {

    fun getRekapLembur(bulan: String, token: String){
        NetworkConfig
            .getService()
            .rekapLembur(bulan, token)
            .enqueue(object : retrofit2.Callback<RekapLemburModel>{
                override fun onResponse(
                    call: Call<RekapLemburModel>,
                    response: Response<RekapLemburModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapLemburInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapLemburInterface.onSuccessGetRekapLembur(body)
                    }else if(body?.success == false){
                        rekapLemburInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<RekapLemburModel>, t: Throwable) {
                    rekapLemburInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun deleteLembur(lemburID: String, token: String){
        NetworkConfig
            .getService()
            .deleteLembur(lemburID, token)
            .enqueue(object : retrofit2.Callback<DeleteLemburModel>{
                override fun onResponse(
                    call: Call<DeleteLemburModel>,
                    response: Response<DeleteLemburModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapLemburInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }

                    if(body?.success == true){
                        rekapLemburInterface.onSuccessDeleteLembur(body)
                    }
                }

                override fun onFailure(call: Call<DeleteLemburModel>, t: Throwable) {
                    rekapLemburInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}