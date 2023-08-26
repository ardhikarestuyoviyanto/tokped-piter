package com.dhikapro.piter.app.interfaces.izin

import com.dhikapro.piter.app.models.perizinan.DetailPerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.models.perizinan.UpdatePerizinanModel

interface DetailIzinInterface {
    fun onSuccessGetKategoriIzin(kategoriPerizinanModel: KategoriPerizinanModel)
    fun onSuccessGetDetailIzin(detailPerizinanModel: DetailPerizinanModel)
    fun onSuccessUpdateIzin(updatePerizinanModel: UpdatePerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}