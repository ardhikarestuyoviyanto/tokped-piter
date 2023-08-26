package com.dhikapro.piter.app.models.presensi

data class RekapAbsenModel(
    val success: Boolean,
    val message: String?=null,
    val data: List<RekapAbsenItemModel>?=null
)

data class RekapAbsenItemModel(
    val tanggal: String?=null,
    val absen_masuk: String?=null,
    val absen_pulang: String?=null,
    val foto_masuk: String?=null,
    val foto_pulang: String?=null,
    val kode_tl: String?=null,
    val kode_psw: String?=null,
    val potongan_tl: Int?=null,
    val potongan_psw: Int?=null,
    val keterangan: String?=null
)