package com.dhikapro.piter.app.interfaces.lembur

import com.dhikapro.piter.app.models.lembur.DeleteLemburModel
import com.dhikapro.piter.app.models.lembur.RekapLemburModel

interface RekapLemburInterface {
    fun onSuccessGetRekapLembur(rekapLemburModel: RekapLemburModel)
    fun onSuccessDeleteLembur(deleteLemburModel: DeleteLemburModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}