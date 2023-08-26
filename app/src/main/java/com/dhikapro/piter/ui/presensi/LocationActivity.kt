package com.dhikapro.piter.ui.presensi

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.content.ContextCompat
import com.dhikapro.piter.R
import com.dhikapro.piter.app.interfaces.presensi.LocationInterface
import com.dhikapro.piter.app.models.presensi.LocationModel
import com.dhikapro.piter.app.repository.presensi.LocationRepo
import com.dhikapro.piter.app.session.SharedPreference
import com.dhikapro.piter.app.utils.DisableDarkMode
import com.dhikapro.piter.app.utils.PermissionCheck
import com.dhikapro.piter.app.utils.SetupLocation
import com.dhikapro.piter.app.utils.snackbar
import com.dhikapro.piter.databinding.ActivityLocationBinding
import com.dhikapro.piter.ui.auth.LoginActivity
import com.dhikapro.piter.ui.dashboard.DashboardActivity
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.tapadoo.alerter.Alerter

class LocationActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener, LocationInterface{

    private lateinit var binding: ActivityLocationBinding
    private lateinit var mMap: GoogleMap
    private lateinit var lastLocation: Location
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient
    private lateinit var sharedPreference: SharedPreference
    private lateinit var locationRepo: LocationRepo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLocationBinding.inflate(layoutInflater)
        setContentView(binding.root)
        DisableDarkMode(this).disable()
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        sharedPreference = SharedPreference(this)
        locationRepo = LocationRepo(this)

        if(intent.getStringExtra("type_absen") == null){
            startActivity(Intent(this, DashboardActivity::class.java))
            overridePendingTransition(0, 0)
        }
        if(intent.getStringExtra("type_absen") == "masuk"){
            supportActionBar?.setTitle(getString(R.string.app_bar_absen_masuk))
        }else{
            supportActionBar?.setTitle(getString(R.string.app_bar_absen_pulang))
        }
        binding.refreshLocation.setOnClickListener {
            init()
        }
        binding.submitButton.setOnClickListener {
            val intentPostAbsen = Intent(this, PostAbsenActivity::class.java)
            intentPostAbsen.putExtra("type_absen", intent.getStringExtra("type_absen"))
            startActivity(intentPostAbsen)
            overridePendingTransition(0, 0)
        }

        // display map
        val mapFragment = supportFragmentManager.findFragmentById(R.id.mapFragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
        init()
    }

    private fun init(){
        if(!checkGPS()){
            binding.root.snackbar("Aktifkan GPS Anda Dahulu ...")
            binding.formLokasiAbsen.visibility = View.GONE
        }else{
            binding.formLokasiAbsen.visibility = View.VISIBLE
            locationRepo.getLocation(sharedPreference.getTokenJWT()!!)
            disabledForm(getString(R.string.tunggu))
        }
    }


    override fun onResume() {
        super.onResume()
        init()
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressedDispatcher.onBackPressed()
        return super.onSupportNavigateUp()
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.isZoomControlsEnabled = true
        mMap.uiSettings.setAllGesturesEnabled(true)
        mMap.setOnMarkerClickListener(this)

        mMap.isMyLocationEnabled = true
        init()
    }

    override fun onMarkerClick(p0: Marker): Boolean = false

    @SuppressLint("MissingPermission", "SetTextI18n")
    override fun onSuccessGetLocation(locationModel: LocationModel) {
        mMap.clear()
        val unitKerja = locationModel.data
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)
        if(PermissionCheck().checkPermissions(this)){
            fusedLocationProviderClient.lastLocation.addOnSuccessListener(this){location->
                if(location!=null){
                    lastLocation = location
                    // lat long hp user
                    val currentLatLong = LatLng(location.latitude, location.longitude)
                    // marker lat long unit kerja
                    SetupLocation(this).placeMarkerOnMap(
                        currentLatLong,
                        unitKerja?.latitude!!.toDouble(), unitKerja.longitude!!.toDouble(),
                        unitKerja.nama_unit_kerja!!,
                        mMap
                    )
                    // setup location hp user
                    val setupLocationUser: Map<String, String> = SetupLocation(this).setUpLocationName(
                        unitKerja.latitude.toDouble(),
                        unitKerja.longitude.toDouble(),
                        location.latitude,
                        location.longitude,
                        this
                    )
                    // display in view
                    binding.locationMe.text = setupLocationUser["currentLocation"]
                    binding.locationOffice.text = "${unitKerja.nama_unit_kerja} ( ${unitKerja.alamat} )"
                    binding.distanceLocation.text = setupLocationUser["distanceLocation"]
                    // set view di map
                    mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLong, 12f))
                    // cek mock app
                    if(location.isFromMockProvider){
                        disabledForm("Fake GPS Detected ...")
                    }else{
                        if(
                            SetupLocation(this).chekLocationIsAllowed(
                                unitKerja.latitude.toDouble(),
                                unitKerja.longitude.toDouble(),
                                location.latitude, location.longitude
                            ) || locationModel.status_wfh!!
                        ){
                            enabledForm()
                            setCardLkh(locationModel)
                        }else{
                            disabledForm("Lokasi Terlalu Jauh")
                        }
                    }
                }
            }
        }else{
            binding.root.snackbar("Permission Access Location Required")
            disabledForm("Permission Location Required")
        }

    }

    override fun onBadRequest(message: String) {
        Alerter.create(this)
            .setTitle("Terjadi Kesalahan")
            .setText(message)
            .setBackgroundColorRes(R.color.danger)
            .show()
    }

    override fun onUnAuthorized(message: String) {
        sharedPreference.removeToken()
        val intent = Intent(this, LoginActivity::class.java)
        intent.putExtra("onUnAuthorized", message)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    private fun checkGPS():Boolean{
        val mLocationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager
        val gps = mLocationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
        if(gps){
            return true
        }
        return false
    }

    private fun disabledForm(textDisabled: String){
        binding.submitButton.isEnabled = false
        binding.submitButton.background = ContextCompat.getDrawable(this, R.drawable.border_button_warning)
        binding.submitButton.text = textDisabled
    }

    private fun enabledForm(){
        binding.submitButton.isEnabled = true
        binding.submitButton.background = ContextCompat.getDrawable(this, R.drawable.border_button_normal)
        binding.submitButton.text = getString(R.string.absen_sekarang)
    }

    @SuppressLint("SetTextI18n")
    private fun setCardLkh(locationModel: LocationModel){
        if(intent.getStringExtra("type_absen") == "masuk"){
            if (locationModel.status_absen_masuk == true) {
                disabledForm("Sudah Absen Masuk")
            }
            else if (locationModel.status_absen_masuk == false) {
                enabledForm()
            }
            else {}
        }else{
            if (locationModel.status_absen_pulang == true) {
                disabledForm("Sudah Absen Pulang")
            }
            else if (locationModel.status_absen_pulang == false) {
                enabledForm()
            }
            else {}
        }

        if(locationModel.status_wfh == true){
            binding.cardStatusWfh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_success)
            binding.statusWfhText.text = "Status Pegawai WFH (Work From Home)"
        }else{
            binding.cardStatusWfh.background = ContextCompat.getDrawable(this, R.drawable.bg_card_primary)
            binding.statusWfhText.text = "Status Pegawai WFO (Work From Office)"
        }
    }
}