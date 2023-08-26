package com.dhikapro.piter.app.repository.dashboard

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.dashboard.DashboardInterface
import com.dhikapro.piter.app.models.dashboard.InfoLimitModel
import com.dhikapro.piter.app.models.dashboard.LogoutModel
import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class DashboardRepo(private val dashboardInterface: DashboardInterface) {

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
                        dashboardInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        dashboardInterface.onSuccessGetProfile(body)
                    }
                }

                override fun onFailure(call: Call<ProfileModel>, t: Throwable) {
                    dashboardInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getInfoLimit(limitTotal: Int, token: String){
        NetworkConfig
            .getService()
            .getInfoLimit(limitTotal, token)
            .enqueue(object : retrofit2.Callback<InfoLimitModel>{
                override fun onResponse(
                    call: Call<InfoLimitModel>,
                    response: Response<InfoLimitModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        dashboardInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }

                    if(body?.success == true){
                        dashboardInterface.onSuccessGetInfoLimit(body)
                    }
                }

                override fun onFailure(call: Call<InfoLimitModel>, t: Throwable) {
                    dashboardInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun logout(token: String){
        NetworkConfig.getService()
            .logout(token)
            .enqueue(object : retrofit2.Callback<LogoutModel>{
                override fun onResponse(call: Call<LogoutModel>, response: Response<LogoutModel>) {
                    val body = response.body()
                    if(response.code() == 401){
                        dashboardInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        dashboardInterface.onSuccessLogout(body!!)
                    }
                    dashboardInterface.onUnAuthorized(Company.unauthorizedFailure)
                }

                override fun onFailure(call: Call<LogoutModel>, t: Throwable) {
                    dashboardInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}