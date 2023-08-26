package com.dhikapro.piter.app.repository.lembur

import android.content.Context
import android.net.Uri
import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lembur.LemburPageInterface
import com.dhikapro.piter.app.models.lembur.CreateLemburModel
import com.dhikapro.piter.app.models.lembur.StatusLemburModel
import com.dhikapro.piter.app.network.NetworkConfig
import com.dhikapro.piter.app.utils.UriToFileUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class LemburPageRepo(private val lemburPageInterface: LemburPageInterface, private val context: Context){

    fun getStatusLembur(
        token:String
    ){
        NetworkConfig
            .getService()
            .getStatusLembur(token)
            .enqueue(object : retrofit2.Callback<StatusLemburModel>{
                override fun onResponse(
                    call: Call<StatusLemburModel>,
                    response: Response<StatusLemburModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        lemburPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success == true || body?.success == false){
                        lemburPageInterface.onSuccessGetStatusLembur(body)
                    }
                }

                override fun onFailure(call: Call<StatusLemburModel>, t: Throwable) {
                    lemburPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun createLembur(
        tanggal: String,
        jamMulai: String,
        jamSelesai:String,
        kegiatan: String,
        bukti: Uri,
        token: String
    ){
        val tanggalRB = tanggal.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jamMulaiRB = jamMulai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jamSelesaiRB = jamSelesai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val kegiatanRB = kegiatan.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        val pickedBukti = UriToFileUpload(bukti, context)
        val buktiMultiPart = MultipartBody.Part.createFormData("bukti", pickedBukti.getFile().name, pickedBukti.getRequestBodyFile())

        NetworkConfig.getService()
            .createLembur(
                tanggalRB, jamMulaiRB, jamSelesaiRB, kegiatanRB, buktiMultiPart, token
            )
            .enqueue(object : retrofit2.Callback<CreateLemburModel>{
                override fun onResponse(
                    call: Call<CreateLemburModel>,
                    response: Response<CreateLemburModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        lemburPageInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success!!){
                        lemburPageInterface.onSuccessCreateLembur(body)
                    }else {
                        lemburPageInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<CreateLemburModel>, t: Throwable) {
                    lemburPageInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}