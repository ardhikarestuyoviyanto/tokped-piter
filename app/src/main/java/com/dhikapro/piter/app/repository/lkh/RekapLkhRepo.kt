package com.dhikapro.piter.app.repository.lkh

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lkh.RekapLkhInterface
import com.dhikapro.piter.app.models.lkh.DeleteLkhModel
import com.dhikapro.piter.app.models.lkh.RekapLkhModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class RekapLkhRepo(private val rekapLkhInterface: RekapLkhInterface) {

    fun getRekapLkh(bulan: String, token: String){
        NetworkConfig.getService()
            .getRekapLkh(bulan, token)
            .enqueue(object : retrofit2.Callback<RekapLkhModel>{
                override fun onResponse(
                    call: Call<RekapLkhModel>,
                    response: Response<RekapLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        rekapLkhInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapLkhInterface.onSuccessGetRekapLkh(body)
                    }
                    if(body?.success == false){
                        rekapLkhInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<RekapLkhModel>, t: Throwable) {
                    rekapLkhInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun deleteLkh(lkhID: String, token: String){
        NetworkConfig.getService()
            .deleteLkh(lkhID, token)
            .enqueue(object : retrofit2.Callback<DeleteLkhModel>{
                override fun onResponse(
                    call: Call<DeleteLkhModel>,
                    response: Response<DeleteLkhModel>
                ) {
                    val body = response.body()
                    if(response.code()==401){
                        rekapLkhInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        rekapLkhInterface.onSuccessDeleteLkh(body)
                    }
                }

                override fun onFailure(call: Call<DeleteLkhModel>, t: Throwable) {
                    rekapLkhInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}