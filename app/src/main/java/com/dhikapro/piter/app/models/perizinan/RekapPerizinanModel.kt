package com.dhikapro.piter.app.models.perizinan

data class RekapPerizinanModel(
    val success: Boolean,
    val message: String?=null,
    val data: List<RekapPerizinanItemModel>
)

data class RekapPerizinanItemModel(
    val id: String?=null,
    val tanggal_diajukan: String?=null,
    val nama_pegawai: String?=null,
    val tanggal_mulai: String?=null,
    val tanggal_selesai: String?=null,
    val status_perizinan: String?=null,
    val kategori_perizinan: String?=null,
    val file_pendukung: String?=null
)
