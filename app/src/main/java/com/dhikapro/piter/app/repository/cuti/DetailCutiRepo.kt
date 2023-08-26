package com.dhikapro.piter.app.repository.cuti

import android.content.Context
import android.net.Uri
import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.cuti.DetailCutiInterface
import com.dhikapro.piter.app.models.perizinan.DetailPerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.models.perizinan.UpdatePerizinanModel
import com.dhikapro.piter.app.network.NetworkConfig
import com.dhikapro.piter.app.utils.UriToFileUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class DetailCutiRepo(private val detailCutiInterface: DetailCutiInterface, private val context: Context){

    fun getKategoriCuti(token: String){
        NetworkConfig.getService()
            .getKategoriCuti(token)
            .enqueue(object : retrofit2.Callback<KategoriPerizinanModel>{
                override fun onResponse(
                    call: Call<KategoriPerizinanModel>,
                    response: Response<KategoriPerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailCutiInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        detailCutiInterface.onSuccessGetKategoriCuti(body)
                    }
                }

                override fun onFailure(call: Call<KategoriPerizinanModel>, t: Throwable) {
                    detailCutiInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getDetailCuti(perizinanID: String, token: String){
        NetworkConfig.getService()
            .cutiDetail(perizinanID, token)
            .enqueue(object : retrofit2.Callback<DetailPerizinanModel>{
                override fun onResponse(
                    call: Call<DetailPerizinanModel>,
                    response: Response<DetailPerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailCutiInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        detailCutiInterface.onSuccessGetDetailCuti(body)
                    }
                }

                override fun onFailure(call: Call<DetailPerizinanModel>, t: Throwable) {
                    detailCutiInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun updateCuti(
        tanggalMulai: String,
        tanggalSelesai: String,
        filePendukung: Uri,
        kategoriPerizinanID: String,
        perizinanID: String,
        token: String
    ){
        val tanggalMulaiRB = tanggalMulai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val tanggalSelesaiRB = tanggalSelesai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val kategoriPerizinanIDRB = kategoriPerizinanID.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val perizinanIDRB = perizinanID.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var filePendukungMultipart: MultipartBody.Part?= null
        if(filePendukung != Uri.EMPTY){
            val pickedFilePendukung = UriToFileUpload(filePendukung, context)
            filePendukungMultipart = MultipartBody.Part.createFormData("file_pendukung", pickedFilePendukung.getFile().name, pickedFilePendukung.getRequestBodyFile())
        }

        NetworkConfig.getService()
            .updateCuti(
                tanggalMulaiRB,
                tanggalSelesaiRB,
                filePendukungMultipart,
                kategoriPerizinanIDRB,
                perizinanIDRB,
                token
            )
            .enqueue(object : retrofit2.Callback<UpdatePerizinanModel>{
                override fun onResponse(
                    call: Call<UpdatePerizinanModel>,
                    response: Response<UpdatePerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailCutiInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success==true){
                        detailCutiInterface.onSuccessUpdateCuti(body)
                    }
                    if(body?.success ==false){
                        detailCutiInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<UpdatePerizinanModel>, t: Throwable) {
                    detailCutiInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}