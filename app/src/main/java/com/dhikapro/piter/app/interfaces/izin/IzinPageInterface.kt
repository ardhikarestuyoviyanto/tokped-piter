package com.dhikapro.piter.app.interfaces.izin

import com.dhikapro.piter.app.models.perizinan.CreatePerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel

interface IzinPageInterface {
    fun onSuccessGetKategoriIzin(kategoriPerizinanModel: KategoriPerizinanModel)
    fun onSuccessCreateIzin(createPerizinanModel: CreatePerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}