package com.dhikapro.piter.app.interfaces.lkh

import com.dhikapro.piter.app.models.lkh.DetailLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.UpdateLkhModel

interface DetailLkhInterface {
    fun onSuccessUpdateLkh(updateLkhModel: UpdateLkhModel)
    fun onSuccessGetDetailLkh(detailLkhModel: DetailLkhModel)
    fun onSuccessGetKategoriLkh(kategoriLkhModel: KategoriLkhModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}