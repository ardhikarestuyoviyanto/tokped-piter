package com.dhikapro.piter.app.interfaces.izin

import com.dhikapro.piter.app.models.perizinan.RekapPerizinanModel

interface RekapIzinInterface {
    fun onSuccessGetRekapIzin(rekapPerizinanModel: RekapPerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}