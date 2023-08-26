package com.dhikapro.piter.app.interfaces.lkh

import com.dhikapro.piter.app.models.lkh.CreateLkhModel
import com.dhikapro.piter.app.models.lkh.KategoriLkhModel
import com.dhikapro.piter.app.models.lkh.StatusLkhModel

interface LkhPageInterface {
    fun onSuccessGetKategoriLkh(kategoriLkhModel: KategoriLkhModel)
    fun onSuccessGetStatusLkh(statusLkhModel: StatusLkhModel)
    fun onSuccessCreateLkh(createLkhModel: CreateLkhModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}