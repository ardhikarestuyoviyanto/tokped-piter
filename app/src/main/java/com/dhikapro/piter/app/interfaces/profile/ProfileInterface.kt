package com.dhikapro.piter.app.interfaces.profile

import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.models.profile.UbahPasswordModel

interface ProfileInterface {
    fun onSuccessGetProfile(profileModel: ProfileModel)
    fun onUpdatePasswordResponse(ubahPasswordModel: UbahPasswordModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}