package com.dhikapro.piter.app.repository.profile

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.profile.ProfileInterface
import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.models.profile.UbahPasswordModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class ProfileRepo(private val profileInterface: ProfileInterface) {

    fun getProfile(token: String){
        NetworkConfig
            .getService()
            .getProfile(token)
            .enqueue(object : retrofit2.Callback<ProfileModel>{
                override fun onResponse(
                    call: Call<ProfileModel>,
                    response: Response<ProfileModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        profileInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success!!){
                        profileInterface.onSuccessGetProfile(body)
                    }
                }

                override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                    profileInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun updatePassword(
        passwordLama: String,
        passwordBaru: String,
        token: String
    ){
        NetworkConfig.getService()
            .ubahPassword(passwordLama, passwordBaru, token)
            .enqueue(object : retrofit2.Callback<UbahPasswordModel>{
                override fun onResponse(
                    call: Call<UbahPasswordModel>,
                    response: Response<UbahPasswordModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        profileInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    profileInterface.onUpdatePasswordResponse(body!!)
                }

                override fun onFailure(call: Call<UbahPasswordModel>, t: Throwable) {
                    profileInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}