package com.dhikapro.piter.app.repository.lkh

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lkh.LkhPageInterface
import com.dhikapro.piter.app.models.lkh.CreateLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.StatusLkhModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class LkhPageRepo(private val lkhPageInterface: LkhPageInterface) {

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
                        lkhPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        lkhPageInterface.onSuccessGetKategoriLkh(body)
                    }
                }

                override fun onFailure(call: Call<KategoriLkhModel>, t: Throwable) {
                    lkhPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getStatusLkh(token: String){
        NetworkConfig.getService()
            .getStatusLkh(token)
            .enqueue(object : retrofit2.Callback<StatusLkhModel>{
                override fun onResponse(
                    call: Call<StatusLkhModel>,
                    response: Response<StatusLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        lkhPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        lkhPageInterface.onSuccessGetStatusLkh(body)
                    }
                }

                override fun onFailure(call: Call<StatusLkhModel>, t: Throwable) {
                    lkhPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun createLkh(
        tanggal: String,
        kegiatan:String,
        kategoriLkhID: String,
        token: String
    ){
        NetworkConfig.getService()
            .createLkh(tanggal, kegiatan, kategoriLkhID, token)
            .enqueue(object : retrofit2.Callback<CreateLkhModel>{
                override fun onResponse(
                    call: Call<CreateLkhModel>,
                    response: Response<CreateLkhModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        lkhPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        lkhPageInterface.onSuccessCreateLkh(body)
                    }
                    if(body?.success == false){
                        lkhPageInterface.onBadRequest(body.message!!)
                    }

                }

                override fun onFailure(call: Call<CreateLkhModel>, t: Throwable) {
                    lkhPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}