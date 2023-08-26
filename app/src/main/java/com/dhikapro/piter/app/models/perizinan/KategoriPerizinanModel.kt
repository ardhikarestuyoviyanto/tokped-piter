package com.dhikapro.piter.app.models.perizinan

data class KategoriPerizinanModel(
    val success: Boolean,
    val data: List<KategoriPerizinanItemModel>
)

data class KategoriPerizinanItemModel(
    val id: String?=null,
    val nama_kategori: String?=null,
)
