package com.dhikapro.piter.app.models.presensi

data class LocationModel(
    val success: Boolean,
    val status_absen_masuk: Boolean?=null,
    val status_absen_pulang: Boolean?=null,
    val status_wfh: Boolean?=null,
    val data: LocationItemModel?=null
)

data class LocationItemModel(
    val id: String?=null,
    val nama_unit_kerja: String?=null,
    val alamat: String?=null,
    val latitude: String?=null,
    val longitude: String?=null
)
