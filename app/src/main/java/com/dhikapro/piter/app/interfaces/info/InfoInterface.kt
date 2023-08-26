package com.dhikapro.piter.app.interfaces.info

import com.dhikapro.piter.app.models.info.InfoModel
import com.dhikapro.piter.app.models.info.KategoriModel

interface InfoInterface {
    fun onSuccessGetKategori(kategoriModel: KategoriModel)
    fun onSuccessGetInfo(infoModel: InfoModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}