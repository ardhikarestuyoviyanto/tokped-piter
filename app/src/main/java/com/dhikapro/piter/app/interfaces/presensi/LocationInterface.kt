package com.dhikapro.piter.app.interfaces.presensi

import com.dhikapro.piter.app.models.presensi.LocationModel

interface LocationInterface {
    fun onSuccessGetLocation(locationModel: LocationModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}