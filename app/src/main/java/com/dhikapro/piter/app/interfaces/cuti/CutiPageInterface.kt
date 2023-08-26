package com.dhikapro.piter.app.interfaces.cuti

import com.dhikapro.piter.app.models.perizinan.CreatePerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel

interface CutiPageInterface {
    fun onSuccessGetKategoriCuti(kategoriPerizinanModel: KategoriPerizinanModel)
    fun onSuccessCreateCuti(createPerizinanModel: CreatePerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}