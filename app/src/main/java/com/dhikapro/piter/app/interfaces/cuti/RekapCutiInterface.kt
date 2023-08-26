package com.dhikapro.piter.app.interfaces.cuti

import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel

interface RekapCutiInterface{
    fun onSuccessGetRekapCuti(rekapPerizinanModel: RekapPerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}