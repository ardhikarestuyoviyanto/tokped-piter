package com.dhikapro.piter.app.interfaces.presensi

import com.dhikapro.piter.app.models.presensi.RekapAbsenModel

interface RekapAbsenInterface {
    fun onSuccessGetRekapAbsen(rekapAbsenModel: RekapAbsenModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}