package com.dhikapro.piter.app.interfaces.dashboard

import com.dhikapro.piter.app.models.dashboard.InfoLimitModel
import com.dhikapro.piter.app.models.dashboard.LogoutModel
import com.dhikapro.piter.app.models.dashboard.ProfileModel

interface DashboardInterface {
    fun onSuccessGetProfile(profileModel: ProfileModel)
    fun onSuccessGetInfoLimit(infoLimitModel: InfoLimitModel)
    fun onSuccessLogout(logoutModel: LogoutModel)
    fun onBadRequest(message: String)
    fun onUnAuthorized(message: String)
}