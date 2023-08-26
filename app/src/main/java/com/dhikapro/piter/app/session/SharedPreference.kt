package com.dhikapro.piter.app.session

import android.content.Context
import android.content.SharedPreferences

class SharedPreference(private val context: Context) {

    private val privateMode = 0
    private val prefName = "SESSION_TOKEN_USER"

    var pref:SharedPreferences?= context.getSharedPreferences(prefName, privateMode)
    private var editor: SharedPreferences.Editor?=pref?.edit()

    fun removeToken(){
        editor?.clear()
        editor?.commit()
    }

    //-----------------------------------------------

    fun setTokenJWT(token:String){
        editor?.putString("token", "Bearer $token")
        editor?.commit()
    }

    fun setStatusWfh(statusWfh:Boolean){
        editor?.putBoolean("status_wfh", statusWfh)
        editor?.commit()
    }

    fun setPegawaiID(pegawaiID: String){
        editor?.putString("pegawai_id", pegawaiID)
        editor?.commit()
    }

    fun setUnitKerjaID(unitKerjaID: String){
        editor?.putString("unit_kerja_id", unitKerjaID)
        editor?.commit()
    }

    //-----------------------------------------------

    fun getTokenJWT(): String?{
        return pref?.getString("token", "")
    }

    fun getStatusWfh(): Boolean?{
        return pref?.getBoolean("status_wfh", false)
    }

    fun getPegawaiID():String?{
        return pref?.getString("pegawai_id", "")
    }

    fun getUnitKerjaID(): String?{
        return pref?.getString("unit_kerja_id", "")
    }

}