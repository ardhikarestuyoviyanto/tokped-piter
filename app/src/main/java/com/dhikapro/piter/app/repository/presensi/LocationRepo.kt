package com.dhikapro.piter.app.repository.presensi

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.presensi.LocationInterface
import com.dhikapro.piter.app.models.presensi.LocationModel
import com.dhikapro.piter.app.network.NetworkConfig
import retrofit2.Call
import retrofit2.Response

class LocationRepo(private val locationInterface: LocationInterface) {

    fun getLocation(token: String){
        NetworkConfig
            .getService()
            .getUnitKerja(token)
            .enqueue(object : retrofit2.Callback<LocationModel>{
                override fun onResponse(
                    call: Call<LocationModel>,
                    response: Response<LocationModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        locationInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        locationInterface.onSuccessGetLocation(body)
                    }
                }

                override fun onFailure(call: Call<LocationModel>, t: Throwable) {
                    locationInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}