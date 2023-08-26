package com.dhikapro.piter.app.repository.izin

import android.content.Context
import android.net.Uri
import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.izin.IzinPageInterface
import com.dhikapro.piter.app.models.perizinan.CreatePerizinanModel
import com.dhikapro.piter.app.models.perizinan.KategoriPerizinanModel
import com.dhikapro.piter.app.network.NetworkConfig
import com.dhikapro.piter.app.utils.UriToFileUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class IzinPageRepo(private val izinPageInterface: IzinPageInterface, private val context: Context) {

    fun getKategoriIzin(token: String){
        NetworkConfig.getService()
            .getKategoriIzin(token)
            .enqueue(object : retrofit2.Callback<KategoriPerizinanModel>{
                override fun onResponse(
                    call: Call<KategoriPerizinanModel>,
                    response: Response<KategoriPerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        izinPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true){
                        izinPageInterface.onSuccessGetKategoriIzin(body)
                    }
                }

                override fun onFailure(call: Call<KategoriPerizinanModel>, t: Throwable) {
                    izinPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun createIzin(
        tanggalMulai: String,
        tanggalSelesai: String,
        filePendukung: Uri,
        kategoriPerizinanID: String,
        token: String
    ){
        val tanggalMulaiRB = tanggalMulai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val tanggalSelesaiRB = tanggalSelesai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val kategoriPerizinanIDRB = kategoriPerizinanID.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val pickedFilePendukung = UriToFileUpload(filePendukung, context)
        val filePendukungMultipart = MultipartBody.Part.createFormData("file_pendukung", pickedFilePendukung.getFile().name, pickedFilePendukung.getRequestBodyFile())

        NetworkConfig.getService()
            .createIzin(
                tanggalMulaiRB,
                tanggalSelesaiRB,
                filePendukungMultipart,
                kategoriPerizinanIDRB,
                token
            ).enqueue(object : retrofit2.Callback<CreatePerizinanModel>{
                override fun onResponse(
                    call: Call<CreatePerizinanModel>,
                    response: Response<CreatePerizinanModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        izinPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success==true){
                        izinPageInterface.onSuccessCreateIzin(body)
                    }
                    if(body?.success==false){
                        izinPageInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<CreatePerizinanModel>, t: Throwable) {
                    izinPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}