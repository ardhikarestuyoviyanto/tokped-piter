package com.dhikapro.piter.app.models.lembur

data class DetailLemburModel(
    val success: Boolean,
    val message: String?=null,
    val data: RekapLemburItemModel?=null
)