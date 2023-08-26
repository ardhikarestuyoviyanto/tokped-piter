package com.dhikapro.piter.app.utils

import com.dhikapro.piter.app.constant.Company

class ExportUrl(
    private val unitKerjaID: String,
    private val pegawaiID: String,
    private val bulan: String
) {

    fun rekapAbsen(): String {
        return "${Company.BASE_URL}api/lap/pegawai/export/pdf?month=$bulan&q=$unitKerjaID&p=$pegawaiID";
    }

    fun rekapLembur(): String{
        return "${Company.BASE_URL}api/lap/lembur/export/pegawai/pdf?month=$bulan&q=$unitKerjaID&p=$pegawaiID";
    }

    fun rekapLkh(): String{
        return "${Company.BASE_URL}api/lap/lkh/export/pdf?bulan=$bulan&unit_kerja_id=$unitKerjaID&pegawai_id=$pegawaiID";
    }

}