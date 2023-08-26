package com.dhikapro.piter.app.interfaces.lembur

import com.dhikapro.piter.app.models.lembur.DetailLemburModel
import com.dhikapro.piter.app.models.lembur.UpdateLemburModel

interface DetailLemburInterface {
    fun onSuccessUpdateLembur(updateLemburModel: UpdateLemburModel)
    fun onSuccessGetDetailLembur(detailLemburModel: DetailLemburModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}