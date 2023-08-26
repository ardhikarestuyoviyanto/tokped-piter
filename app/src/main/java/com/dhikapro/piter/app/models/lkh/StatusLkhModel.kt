package com.dhikapro.piter.app.models.lkh

data class StatusLkhModel(
    val success: Boolean,
    val data: StatusLkhItemModel
)

data class StatusLkhItemModel(
    val success: Boolean?=null,
    val message: String?=null
)
