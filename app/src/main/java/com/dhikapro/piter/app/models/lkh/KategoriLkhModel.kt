package com.dhikapro.piter.app.models.lkh

data class KategoriLkhModel(
    val success: Boolean,
    val data: List<KategoriLkhItem>?=null
)

data class KategoriLkhItem(
    val id: String?=null,
    val nama_kategori: String?=null
)
