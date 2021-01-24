package org.mousehole.a2ndkotlinapprestaurantfinder

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat

class MainActivity : AppCompatActivity(), LocationListener{

    private lateinit var latLngTextView : TextView

    private val LOCATION_REQUEST_CODE : Int = 707
    private lateinit var overlay: ConstraintLayout
    private lateinit var openSettingsButton : Button

    private lateinit var locationManager: LocationManager

    /*
    1. check if you have permission
    2. if you do not have the oermission - Request the persion
    3. override onPermissionRequestResult- Handle user input accordingly
     */

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        latLngTextView = findViewById(R.id.latlong_tv)
        overlay = findViewById(R.id.permission_overlay)
        openSettingsButton = findViewById(R.id.open_settings_button)

        locationManager = getSystemService(Context.LOCATION_SERVICE) as LocationManager


        openSettingsButton.setOnClickListener {
            // Implicit intent to open settings.... this specific apps permissions to be precise
            val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
            val uri = Uri.fromParts("package", packageName, "Permission")
            // uri = Settings -> org.mousehole.a2ndkotlinapprestaurantfinder
            Log.d("TAG_X","")
            intent.data = uri
            startActivity(intent)
        }

        // 1. Check if you have the permission
        checkLocationPermission()

    }

    override fun onStart() {
        super.onStart()
        checkLocationPermission()
    }


    private fun checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {

            registerLocationManager()
        } else {
            // 2. Request the permission
            requestLocationPermission()
        }
    }

    private fun requestLocationPermission() {

        ActivityCompat.requestPermissions(this, arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION),LOCATION_REQUEST_CODE)
    }

    @SuppressLint("MissingPermission")
    private fun registerLocationManager() {

        locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000L, 10f, this)

    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        // The app has received the user's permission
        // I must check to see if it;s the same permission I requested for
        if ( requestCode == LOCATION_REQUEST_CODE){
            if(permissions[0] == android.Manifest.permission.ACCESS_FINE_LOCATION){
                if (grantResults[0]== PackageManager.PERMISSION_GRANTED){
                    Log.d("TAG_X", "Location Permission Granted!")
                    registerLocationManager()
                } else {

                    if(ActivityCompat.shouldShowRequestPermissionRationale(this,android.Manifest.permission.ACCESS_FINE_LOCATION)){

                        requestLocationPermission()
                            }else {
                        // At this point let the user know that they have
                        //to enable permission manually to use this application
                        Log.d("TAG_X", "Rationale Blocked..")
                        overlay.visibility = View.VISIBLE


                    }
                }
            }
        }
    }

    override fun onLocationChanged(location: Location) {
        Log.d("TAG_X","My Location is: ${location.latitude},${location.longitude}")

    }
}