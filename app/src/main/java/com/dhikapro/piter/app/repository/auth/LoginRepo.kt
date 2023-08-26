package com.dhikapro.piter.app.repository.auth

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.auth.LoginInterface
import com.dhikapro.piter.app.models.auth.LoginModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class LoginRepo(private val loginInterface: LoginInterface) {

    fun login(
        usernameNIDN: String,
        password: String,
        deviceID: String
    ){
        NetworkConfig.getService()
            .login(
                usernameNIDN, password, deviceID
            )
            .enqueue(object : retrofit2.Callback<LoginModel>{
                override fun onResponse(call: Call<LoginModel>, response: Response<LoginModel>) {
                    val body = response.body()
                    if(!body?.success!!){
                        loginInterface.onBadLogin(body.message!!.toString())
                    }else{
                        loginInterface.onSuccessLogin(body)
                    }
                }

                override fun onFailure(call: Call<LoginModel>, t: Throwable) {
                    loginInterface.onBadLogin(Company.connectionFailure)
                }

            })
    }

}