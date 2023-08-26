package com.dhikapro.piter.app.models.info

data class KategoriModel(
    val success: Boolean,
    val data: List<KategoriItemModel>?=null
)

data class KategoriItemModel(
    val id: String?=null,
    val nama_kategori:String?=null
)
