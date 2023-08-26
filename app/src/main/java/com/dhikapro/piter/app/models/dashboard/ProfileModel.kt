package com.dhikapro.piter.app.models.dashboard

data class ProfileModel(
    val success: Boolean,
    val payload_token: PayloadTokenModel?=null,
    val jam_kerja: JamKerjaModel?=null
)

data class PayloadTokenModel(
    val id: String?=null, // pegawai_id
    val nama_pegawai: String?=null,
    val status_pegawai: String?=null,
    val nidn: String?=null,
    val username: String?=null,
    val foto: String?=null,
    val status_wfh: Boolean?=false,
    val unit_kerja: UnitKerjaModel?=null,
)

data class UnitKerjaModel(
    val id: String?=null, // unit_kerja_id
    val nama_unit_kerja: String?=null,
    val alamat: String?=null
)

data class JamKerjaModel(
    val masuk: String?=null,
    val pulang: String?=null
)