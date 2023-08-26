package com.dhikapro.piter.app.repository.lkh

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lkh.DetailLkhInterface
import com.dhikapro.piter.app.models.lkh.DetailLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.UpdateLkhModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class DetailLkhRepo(private val detailLkhInterface: DetailLkhInterface) {

    fun getDetailLkh(lkhID: String, token: String){
        NetworkConfig.getService()
            .getDetailLkh(
                lkhID, token
            )
            .enqueue(object : retrofit2.Callback<DetailLkhModel>{
                override fun onResponse(
                    call: Call<DetailLkhModel>,
                    response: Response<DetailLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailLkhInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        detailLkhInterface.onSuccessGetDetailLkh(body)
                    }
                }

                override fun onFailure(call: Call<DetailLkhModel>, t: Throwable) {
                    detailLkhInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun updateLkh(
        tanggal: String,
        kegiatan:String,
        kategoriLkhID: String,
        lkhID: String,
        token: String
    ){
        NetworkConfig.getService()
            .updateLkh(
                tanggal, kegiatan, kategoriLkhID, lkhID, token
            )
            .enqueue(object : retrofit2.Callback<UpdateLkhModel>{
                override fun onResponse(
                    call: Call<UpdateLkhModel>,
                    response: Response<UpdateLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailLkhInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        detailLkhInterface.onSuccessUpdateLkh(body)
                    }
                    if(body?.success == false){
                        detailLkhInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<UpdateLkhModel>, t: Throwable) {
                    detailLkhInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getKategoriLkh(token: String){
        NetworkConfig
            .getService()
            .getKategoriLkh(token)
            .enqueue(object : retrofit2.Callback<KategoriLkhModel>{
                override fun onResponse(
                    call: Call<KategoriLkhModel>,
                    response: Response<KategoriLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailLkhInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        detailLkhInterface.onSuccessGetKategoriLkh(body)
                    }
                }

                override fun onFailure(call: Call<KategoriLkhModel>, t: Throwable) {
                    detailLkhInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}