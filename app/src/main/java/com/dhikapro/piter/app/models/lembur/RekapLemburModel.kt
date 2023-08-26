package com.dhikapro.piter.app.models.lembur

data class RekapLemburModel(
    val success: Boolean,
    val message: String?=null,
    val data: List<RekapLemburItemModel>?=null
)

data class RekapLemburItemModel(
    val id: String?=null,
    val tanggal: String?=null,
    val jam_mulai: String?=null,
    val jam_selesai: String?=null,
    val kegiatan: String?=null,
    val bukti:String?=null,
    val total_jam: Int?=null
)