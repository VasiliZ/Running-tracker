package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.Manifest
import android.animation.AnimatorInflater
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.github.rtyvz.senla.tr.runningtracker.R
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton

class RunningActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 1101
        private const val DEFAULT_ZOOM = 15
        private val TAG = RunningActivity::class.java.simpleName.toString()
    }

    private var locationPermissionGranted: Boolean = false
    private lateinit var googleMap: GoogleMap
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var startRunningButton: MaterialButton
    private lateinit var exitLayout: CardView
    private lateinit var startLayout: CardView
    private lateinit var stopRunningButton: MaterialButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        findViews()
        locationProvider = LocationServices.getFusedLocationProviderClient(this)
        val permission =
            ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)

        if (permission != PackageManager.PERMISSION_GRANTED) {
            requestPermission()
        } else {
            locationPermissionGranted = true
        }

        val mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        startRunningButton.setOnClickListener {
            startAnimation(startLayout, R.animator.flip_out)
            startAnimation(exitLayout, R.animator.flip_in)
            stopRunningButton.isClickable = true
            startRunningButton.isClickable = false
        }

        stopRunningButton.setOnClickListener {
            startAnimation(exitLayout, R.animator.flip_out)
            startAnimation(startLayout, R.animator.flip_in)
            stopRunningButton.isClickable = false
            startRunningButton.isClickable = true
        }
    }

    private fun findViews() {
        startRunningButton = findViewById(R.id.startRunningButton)
        exitLayout = findViewById(R.id.exitLayout)
        startLayout = findViewById(R.id.startLayout)
        stopRunningButton = findViewById(R.id.stopTimerButton)
    }

    private fun startAnimation(view: View, idAnimatorRes: Int) {
        val flipLestOutAnimator = AnimatorInflater.loadAnimator(this, idAnimatorRes)
        flipLestOutAnimator.setTarget(view)
        flipLestOutAnimator.start()
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(
            this,
            arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
            FINE_LOCATION_REQUEST_CODE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {

        when (requestCode) {
            FINE_LOCATION_REQUEST_CODE -> {
                if (grantResults.isEmpty() || grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    finish()
                } else {
                    locationPermissionGranted = true
                }
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
        updateLocationUi()
    }

    override fun onMapReady(map: GoogleMap) {
        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }
        googleMap = map
        getDeviceLocation()
        updateLocationUi()
    }

    private fun updateLocationUi() {
        try {
            if (locationPermissionGranted) {
                googleMap.isMyLocationEnabled = true
                googleMap.uiSettings.isMyLocationButtonEnabled = true
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                val location = locationProvider.lastLocation
                location.addOnCompleteListener {

                    if (it.isSuccessful) {
                        googleMap.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.result.latitude,
                                    it.result.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                    } else {
                        Log.e(TAG, "Exception: %s", it.exception)
                        googleMap.uiSettings.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }
}