package com.dhikapro.piter.app.models.dashboard

data class InfoLimitModel(
    val success: Boolean,
    val data: List<InfoLimitItemModel>?=null
)

data class InfoLimitItemModel(
    val id: String?=null,
    val judul: String?=null,
    val tanggal: String?=null
)
