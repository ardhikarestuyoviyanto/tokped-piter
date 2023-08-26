package com.dhikapro.piter.app.interfaces.info

import com.dhikapro.piter.app.models.info.InfoDetailModel
import com.dhikapro.piter.app.models.info.InfoModel

interface InfoDetailInterface {
    fun onSuccessGetInfoDetail(infoDetailModel: InfoDetailModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}