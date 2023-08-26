package com.dhikapro.piter.app.interfaces.lkh

import com.dhikapro.piter.app.models.lkh.DeleteLkhModel
import com.dhikapro.piter.app.models.lkh.RekapLkhModel

interface RekapLkhInterface {
    fun onSuccessGetRekapLkh(rekapLkhModel: RekapLkhModel)
    fun onSuccessDeleteLkh(deleteLkhModel: DeleteLkhModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}