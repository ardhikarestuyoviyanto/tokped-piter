package com.dhikapro.piter.app.models.info

data class InfoModel(
    val success: Boolean,
    val data: List<InfoItemModel>?=null
)

data class InfoItemModel(
    val id: String?=null,
    val judul: String?=null,
    val tanggal: String?=null,
    val banner_foto: String?=null,
    val lampiran_file: String?=null,
    val isi: String?=null,
    val nama_kategori: String?=null
)