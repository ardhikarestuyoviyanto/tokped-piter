package com.dhikapro.piter.app.repository.lembur

import android.content.Context
import android.net.Uri
import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.interfaces.lembur.DetailLemburInterface
import com.dhikapro.piter.app.models.lembur.DetailLemburModel
import com.dhikapro.piter.app.models.lembur.UpdateLemburModel
import com.dhikapro.piter.app.network.NetworkConfig
import com.dhikapro.piter.app.utils.UriToFileUpload
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Response

class LemburDetailRepo(private val detailLemburInterface: DetailLemburInterface, private val context: Context) {

    fun updateLembur(
        tanggal: String,
        jamMulai: String,
        jamSelesai:String,
        kegiatan: String,
        bukti: Uri,
        lemburID: String,
        token: String
    ){
        val tanggalRB = tanggal.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jamMulaiRB = jamMulai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val jamSelesaiRB = jamSelesai.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val kegiatanRB = kegiatan.toRequestBody("multipart/form-data".toMediaTypeOrNull())
        val lemburIDRB = lemburID.toRequestBody("multipart/form-data".toMediaTypeOrNull())

        var buktiMultiPart: MultipartBody.Part?= null
        if(bukti != Uri.EMPTY){
            val pickedBukti = UriToFileUpload(bukti, context)
            buktiMultiPart = MultipartBody.Part.createFormData("bukti", pickedBukti.getFile().name, pickedBukti.getRequestBodyFile())
        }

        NetworkConfig
            .getService()
            .updateLembur(
                tanggalRB, jamMulaiRB, jamSelesaiRB, kegiatanRB,
                buktiMultiPart, lemburIDRB, token
            )
            .enqueue(object : retrofit2.Callback<UpdateLemburModel>{
                override fun onResponse(
                    call: Call<UpdateLemburModel>,
                    response: Response<UpdateLemburModel>
                ) {
                    val body = response.body()
                    if(response.code() == 401){
                        detailLemburInterface.onUnAuthorized(Company.unauthorizedFailure)
                    }
                    if(body?.success==true){
                        detailLemburInterface.onSuccessUpdateLembur(body)
                    }else if(body?.success == false){
                        detailLemburInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<UpdateLemburModel>, t: Throwable) {
                    detailLemburInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

    fun getDetailLembur(
        lemburID: String, token: String
    ){
        NetworkConfig
            .getService()
            .detailLembur(lemburID, token)
            .enqueue(object : retrofit2.Callback<DetailLemburModel>{
                override fun onResponse(
                    call: Call<DetailLemburModel>,
                    response: Response<DetailLemburModel>
                ) {
                    val body = response.body()
                    if(body?.success == true){
                        detailLemburInterface.onSuccessGetDetailLembur(body)
                    }else if(body?.success == false){
                        detailLemburInterface.onBadRequest(body.message!!)
                    }
                }

                override fun onFailure(call: Call<DetailLemburModel>, t: Throwable) {
                    detailLemburInterface.onBadRequest(Company.connectionFailure)
                }

            })
    }

}