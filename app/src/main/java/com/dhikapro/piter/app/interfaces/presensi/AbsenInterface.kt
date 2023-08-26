package com.dhikapro.piter.app.interfaces.presensi

import com.dhikapro.piter.app.models.presensi.AbsenModel

interface AbsenInterface {
    fun onSuccessAbsen(absenModel: AbsenModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}