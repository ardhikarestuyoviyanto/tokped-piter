package com.dhikapro.piter.app.interfaces.auth

import com.dhikapro.piter.app.models.auth.LoginModel

interface LoginInterface {
    fun onSuccessLogin(loginModel: LoginModel)
    fun onBadLogin(message: String)
}