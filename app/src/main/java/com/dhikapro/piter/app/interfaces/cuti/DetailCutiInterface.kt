package com.dhikapro.piter.app.interfaces.cuti

import com.dhikapro.piter.app.models.perizinan.DetailPerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.models.perizinan.UpdatePerizinanModel

interface DetailCutiInterface {
    fun onSuccessGetKategoriCuti(kategoriPerizinanModel: KategoriPerizinanModel)
    fun onSuccessGetDetailCuti(detailPerizinanModel: DetailPerizinanModel)
    fun onSuccessUpdateCuti(updatePerizinanModel: UpdatePerizinanModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}