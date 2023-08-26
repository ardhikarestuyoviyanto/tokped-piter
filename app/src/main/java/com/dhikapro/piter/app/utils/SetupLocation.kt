package com.dhikapro.piter.app.utils

import android.annotation.SuppressLint
import android.content.Context
import android.location.Geocoder
import android.location.Location
import android.widget.Toast
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import kotlin.math.roundToInt

class SetupLocation(val context: Context) {
    private var distanceKM = 0.0
    private var distanceMeters = 0.0
    private val officeLocation = Location("")
    private val currentLocation = Location("")

    companion object {
        private const val CONST_MINIMUM_METERS = 200
    }

    fun placeMarkerOnMap(currentLatLong: LatLng, latUnitKerja:Double, lonUnitKerja:Double, nameUnitKerja:String, mMap: GoogleMap){
        val markerOptions = MarkerOptions().position(currentLatLong)
        val lokasiKantor = LatLng(latUnitKerja, lonUnitKerja)
        markerOptions.title("Lokasi Anda")
        mMap.addMarker(markerOptions)
        mMap.addMarker(MarkerOptions().position(lokasiKantor).title(nameUnitKerja))
        mMap.moveCamera(CameraUpdateFactory.newLatLng(lokasiKantor))
    }

    @SuppressLint("SetTextI18n")
    fun setUpLocationName(officeLat:Double, officeLon:Double, currentLat:Double, currentLon:Double, context: Context): Map<String, String> {
        val geocoder = Geocoder(context)
        return try{
            val currentLocation = geocoder.getFromLocation(currentLat, currentLon, 1)
            mapOf(
                "currentLocation" to currentLocation?.get(0)?.getAddressLine(0).toString(),
                "distanceLocation" to getLocationDistance(officeLat, officeLon, currentLat, currentLon)
            )
        }catch (e:Exception){
            Toast.makeText(context, "Please check your connection", Toast.LENGTH_SHORT).show()
            mapOf(
                "currentLocation" to "Location name not detected, please check your connection",
                "distanceLocation" to getLocationDistance(officeLat, officeLon, currentLat, currentLon)
            )
        }
    }

    private fun getLocationDistance(officeLat:Double, officeLon:Double, currentLat:Double, currentLon:Double):String{

        this.officeLocation.latitude = officeLat
        this.officeLocation.longitude = officeLon
        this.currentLocation.latitude = currentLat
        this.currentLocation.longitude = currentLon

        this.distanceKM = (((currentLocation.distanceTo(officeLocation).toDouble() / 1000).roundToInt() * 100)/100).toDouble()
        this.distanceMeters = ((currentLocation.distanceTo(officeLocation).toDouble() * 100).roundToInt() / 100).toDouble()

        return if(this.distanceKM >= 1){
            "$distanceKM km dari kantor anda"
        }else{
            "$distanceMeters meter dari kantor anda"
        }
    }

    fun chekLocationIsAllowed(officeLat:Double, officeLon:Double, currentLat:Double, currentLon:Double):Boolean{
        this.officeLocation.latitude = officeLat
        this.officeLocation.longitude = officeLon
        this.currentLocation.latitude = currentLat
        this.currentLocation.longitude = currentLon
        this.distanceMeters = ((currentLocation.distanceTo(officeLocation).toDouble() * 100).roundToInt() / 100).toDouble()

        return this.distanceMeters <= CONST_MINIMUM_METERS
    }
}