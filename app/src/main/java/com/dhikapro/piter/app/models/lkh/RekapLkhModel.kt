package com.dhikapro.piter.app.models.lkh

data class RekapLkhModel(
    val success: Boolean,
    val message: String?=null,
    val data: List<RekapLkhItemModel>?=null
)

data class RekapLkhItemModel(
    val id: String?=null,
    val tanggal: String?=null,
    val kegiatan: String?=null,
    val status: String?=null,
    val nama_kategori: String?=null,
    val kategori_lkh_id: String?=null
)