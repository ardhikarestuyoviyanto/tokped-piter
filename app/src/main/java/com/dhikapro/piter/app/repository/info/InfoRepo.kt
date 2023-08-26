package com.dhikapro.piter.app.repository.info

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.info.InfoInterface
import com.dhikapro.piter.app.models.info.InfoModel
import com.dhikapro.piter.app.models.info.KategoriModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class InfoRepo(private val infoInterface: InfoInterface) {

    fun getKategori(token: String){
        NetworkConfig
            .getService()
            .getKategori(token)
            .enqueue(object : retrofit2.Callback<KategoriModel>{
                override fun onResponse(
                    call: Call<KategoriModel>,
                    response: Response<KategoriModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        infoInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        infoInterface.onSuccessGetKategori(body)
                    }
                }

                override fun onFailure(call: Call<KategoriModel>, t: Throwable) {
                    infoInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getInfoAll(kategoriID: String?=null, token:String){
        NetworkConfig
            .getService()
            .getInfoAll(kategoriID, token)
            .enqueue(object : retrofit2.Callback<InfoModel>{
                override fun onResponse(call: Call<InfoModel>, response: Response<InfoModel>) {
                    val body = response.body()
                    if(response.code() == 401){
                        infoInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        infoInterface.onSuccessGetInfo(body)
                    }
                }

                override fun onFailure(call: Call<InfoModel>, t: Throwable) {
                    infoInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}