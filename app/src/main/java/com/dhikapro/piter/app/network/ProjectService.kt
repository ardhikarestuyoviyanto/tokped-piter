package com.dhikapro.piter.app.network

import com.dhikapro.piter.app.constant.Company
import com.dhikapro.piter.app.models.auth.LoginModel
import com.dhikapro.piter.app.models.dashboard.InfoLimitModel
import com.dhikapro.piter.app.models.dashboard.LogoutModel
import com.dhikapro.piter.app.models.dashboard.ProfileModel
import com.dhikapro.piter.app.models.info.InfoDetailModel
import com.dhikapro.piter.app.models.info.InfoModel
import com.dhikapro.piter.app.models.info.KategoriModel
import com.dhikapro.piter.app.models.lembur.*
import com.dhikapro.piter.app.models.lkh.*
import com.dhikapro.piter.app.models.perizinan.*
import com.dhikapro.piter.app.models.presensi.AbsenModel
import com.dhikapro.piter.app.models.presensi.LocationModel
import com.dhikapro.piter.app.models.presensi.RekapAbsenModel
import com.dhikapro.piter.app.models.profile.UbahPasswordModel
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ProjectService {
    // login
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("username_nidn") usernameNIDN:String,
        @Field("password") password: String,
        @Field("device_id") deviceID: String,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<LoginModel>

    //logout
    @FormUrlEncoded
    @POST("logout")
    fun logout(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
        @Field("status") status: Boolean?=true,
    ): Call<LogoutModel>

    // get profile
    @GET("user/profile")
    fun getProfile(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<ProfileModel>

    // info limit
    @FormUrlEncoded
    @POST("info/limit")
    fun getInfoLimit(
        @Field("limit_total") limitTotal: Int?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<InfoLimitModel>

    // kategori info
    @GET("info/kategori")
    fun getKategori(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<KategoriModel>

    // get info all
    @FormUrlEncoded
    @POST("info/all")
    fun getInfoAll(
        @Field("kategori_id") kategoriID: String?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<InfoModel>

    // get info detail
    @FormUrlEncoded
    @POST("info/detail")
    fun getInfoDetail(
        @Field("informasi_id") informasiID: String?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<InfoDetailModel>

    // ubah password
    @FormUrlEncoded
    @POST("user/ubahpassword")
    fun ubahPassword(
        @Field("password_lama") passwordLama: String,
        @Field("password_baru") passwordBaru: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<UbahPasswordModel>

    // Get Status Lembur
    @GET("lembur/status")
    fun getStatusLembur(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<StatusLemburModel>

    // Create Lembur
    @Multipart
    @POST("lembur/create")
    fun createLembur(
        @Part("tanggal") tanggal: RequestBody,
        @Part("jam_mulai") jamMulai: RequestBody,
        @Part("jam_selesai") jamSelesai: RequestBody,
        @Part("kegiatan") kegiatan: RequestBody,
        @Part bukti: MultipartBody.Part?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<CreateLemburModel>

    // Rekap Lembur
    @FormUrlEncoded
    @POST("lembur/rekap")
    fun rekapLembur(
        @Field("bulan") bulan: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<RekapLemburModel>

    // Get Detail Lembur
    @FormUrlEncoded
    @POST("lembur/detail")
    fun detailLembur(
        @Field("lembur_id") lemburID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<DetailLemburModel>

    // Update Lembur
    @Multipart
    @POST("lembur/update")
    fun updateLembur(
        @Part("tanggal") tanggal: RequestBody,
        @Part("jam_mulai") jamMulai: RequestBody,
        @Part("jam_selesai") jamSelesai: RequestBody,
        @Part("kegiatan") kegiatan: RequestBody,
        @Part bukti: MultipartBody.Part?=null,
        @Part("lembur_id") lemburID: RequestBody,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<UpdateLemburModel>

    // Delete Lembur
    @FormUrlEncoded
    @POST("lembur/delete")
    fun deleteLembur(
        @Field("lembur_id") lemburID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<DeleteLemburModel>

    // get kategori lkh
    @GET("lkh/kategori")
    fun getKategoriLkh(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<KategoriLkhModel>

    // get status lkh
    @GET("lkh/status")
    fun getStatusLkh(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<StatusLkhModel>

    // create lkh
    @FormUrlEncoded
    @POST("lkh/create")
    fun createLkh(
        @Field("tanggal") tanggal: String,
        @Field("kegiatan") kegiatan: String,
        @Field("kategori_lkh_id") kategoriLkhID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<CreateLkhModel>

    // rekap lkh
    @FormUrlEncoded
    @POST("lkh/rekap")
    fun getRekapLkh(
        @Field("bulan") bulan: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<RekapLkhModel>

    // delete lkh
    @FormUrlEncoded
    @POST("lkh/delete")
    fun deleteLkh(
        @Field("lkh_id") lkhID: String?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<DeleteLkhModel>

    // get detail lkh
    @FormUrlEncoded
    @POST("lkh/detail")
    fun getDetailLkh(
        @Field("lkh_id") lkhID: String?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<DetailLkhModel>

    // updateLkh
    @FormUrlEncoded
    @PUT("lkh/update")
    fun updateLkh(
        @Field("tanggal") tanggal: String,
        @Field("kegiatan") kegiatan: String,
        @Field("kategori_lkh_id") kategoriLkhID: String,
        @Field("lkh_id") lkhID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<UpdateLkhModel>

    // get kategori izin
    @GET("izin/kategori")
    fun getKategoriIzin(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<KategoriPerizinanModel>

    // create izin
    @Multipart
    @POST("izin/create")
    fun createIzin(
        @Part("tanggal_mulai") tanggalMulai: RequestBody,
        @Part("tanggal_selesai") tanggalSelesai: RequestBody,
        @Part file_pendukung: MultipartBody.Part?=null,
        @Part("kategori_perizinan_id") kategoriPerizinanID: RequestBody,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<CreatePerizinanModel>

    // Rekap Izin
    @FormUrlEncoded
    @POST("izin/rekap")
    fun rekapIzin(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
        @Field("status") status: Boolean?=true,
    ): Call<RekapPerizinanModel>

    // Get Izin Detail
    @FormUrlEncoded
    @POST("izin/detail")
    fun izinDetail(
        @Field("perizinan_id") perizinanID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<DetailPerizinanModel>

    // update izin
    @Multipart
    @POST("izin/update")
    fun updateIzin(
        @Part("tanggal_mulai") tanggalMulai: RequestBody,
        @Part("tanggal_selesai") tanggalSelesai: RequestBody,
        @Part file_pendukung: MultipartBody.Part?=null,
        @Part("kategori_perizinan_id") kategoriPerizinanID: RequestBody,
        @Part("perizinan_id") perizinanID: RequestBody,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<UpdatePerizinanModel>

    // get kategori cuti
    @GET("cuti/kategori")
    fun getKategoriCuti(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<KategoriPerizinanModel>

    // create cuti
    @Multipart
    @POST("cuti/create")
    fun createCuti(
        @Part("tanggal_mulai") tanggalMulai: RequestBody,
        @Part("tanggal_selesai") tanggalSelesai: RequestBody,
        @Part file_pendukung: MultipartBody.Part?=null,
        @Part("kategori_perizinan_id") kategoriPerizinanID: RequestBody,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<CreatePerizinanModel>

    // Rekap Cuti
    @FormUrlEncoded
    @POST("cuti/rekap")
    fun rekapCuti(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
        @Field("status") status: Boolean?=true,
    ): Call<RekapPerizinanModel>

    // Get cuti Detail
    @FormUrlEncoded
    @POST("cuti/detail")
    fun cutiDetail(
        @Field("perizinan_id") perizinanID: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<DetailPerizinanModel>

    // update cuti
    @Multipart
    @POST("cuti/update")
    fun updateCuti(
        @Part("tanggal_mulai") tanggalMulai: RequestBody,
        @Part("tanggal_selesai") tanggalSelesai: RequestBody,
        @Part file_pendukung: MultipartBody.Part?=null,
        @Part("kategori_perizinan_id") kategoriPerizinanID: RequestBody,
        @Part("perizinan_id") perizinanID: RequestBody,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY
    ): Call<UpdatePerizinanModel>

    // rekap presensi
    @FormUrlEncoded
    @POST("absen/rekap")
    fun rekapAbsen(
        @Field("month") bulan: String,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<RekapAbsenModel>

    // absen masuk
    @Multipart
    @POST("absen/masuk")
    fun absenMasuk(
        @Part fotoMasuk: MultipartBody.Part?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<AbsenModel>

    // absen pulang
    @Multipart
    @POST("absen/pulang")
    fun absenPulang(
        @Part fotoPulang: MultipartBody.Part?=null,
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<AbsenModel>

    // get location
    @GET("unitkerja")
    fun getUnitKerja(
        @Header("Authorization") token: String?=null,
        @Query("api_key") api_key: String?=Company.API_KEY,
    ): Call<LocationModel>

}