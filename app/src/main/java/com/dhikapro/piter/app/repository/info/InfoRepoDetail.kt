package com.dhikapro.piter.app.repository.info

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.info.InfoDetailInterface
import com.dhikapro.piter.app.models.info.InfoDetailModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class InfoRepoDetail(private val infoDetailInterface: InfoDetailInterface
) {

    fun getInfoDetail(infoID: String?=null, token: String){
        NetworkConfig.getService()
            .getInfoDetail(
                infoID, token
            )
            .enqueue(object : retrofit2.Callback<InfoDetailModel>{
                override fun onResponse(
                    call: Call<InfoDetailModel>,
                    response: Response<InfoDetailModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        infoDetailInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        infoDetailInterface.onSuccessGetInfoDetail(body)
                    }
                }

                override fun onFailure(call: Call<InfoDetailModel>, t: Throwable) {
                    infoDetailInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}