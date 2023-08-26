package com.dhikapro.piter.app.interfaces.lembur

import com.dhikapro.piter.app.models.lembur.CreateLemburModel
import com.dhikapro.piter.app.models.lembur.StatusLemburModel

interface LemburPageInterface {
    fun onSuccessGetStatusLembur(statusLemburModel: StatusLemburModel)
    fun onSuccessCreateLembur(createLemburModel: CreateLemburModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}