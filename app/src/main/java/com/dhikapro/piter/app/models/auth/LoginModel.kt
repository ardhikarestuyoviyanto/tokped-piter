package com.dhikapro.piter.app.models.auth

data class LoginModel(
    val success: Boolean?=null,
    val message: String?=null,
    val access_token: String?=null
)