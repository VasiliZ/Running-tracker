package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.Manifest
import android.animation.AnimatorInflater
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningService.Companion.ACTION_RUNNING_SERVICE_STOP
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class RunningActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 1101
        private const val DEFAULT_ZOOM = 15
        private val TAG = RunningActivity::class.java.simpleName.toString()
        private const val TIMER_INTERVAL = 10L
        const val EXTRA_RUN_DISTANCE = "RUN_DISTANCE"
        const val BROADCAST_RUN_DISTANCE = "local:BROADCAST_RUN_DISTANCE"
    }

    private var locationPermissionGranted: Boolean = false
    private var googleMap: GoogleMap? = null
    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var startRunningButton: MaterialButton
    private lateinit var exitLayout: CardView
    private lateinit var startLayout: CardView
    private lateinit var stopRunningButton: MaterialButton
    private lateinit var resultLayout: CardView
    private lateinit var timerTextView: MaterialTextView
    private lateinit var resultRunningTimeTextView: MaterialTextView
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var runningDistanceReceiver: BroadcastReceiver
    private lateinit var runDistanceTextView: MaterialTextView

    private var handler: Handler? = null
    private var timeInHundredthOfASecond = 0L
    private var statusChecker = object : Runnable {
        override fun run() {
            try {
                timeInHundredthOfASecond += 10
                updateWatch(timeInHundredthOfASecond)
            } finally {
                handler?.postDelayed(this, TIMER_INTERVAL)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        findViews()
        localBroadcastManager = LocalBroadcastManager.getInstance(this)
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
            exitLayout.isVisible = true
            startRunningButton.isClickable = false

            startTimer()
            val intentRunningService = Intent(this, RunningService::class.java)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                startForegroundService(intentRunningService)
            } else {
                startService(intentRunningService)
            }
        }

        stopRunningButton.setOnClickListener {
            startAnimation(exitLayout, R.animator.flip_out)
            startAnimation(resultLayout, R.animator.flip_in)
            stopRunningButton.isClickable = false
            startRunningButton.isClickable = false
            stopTimer()

            val intentRunningService = Intent(this, RunningService::class.java).apply {
                action = ACTION_RUNNING_SERVICE_STOP
            }
            startService(intentRunningService)
            resultRunningTimeTextView.text = formattedStopWatch(timeInHundredthOfASecond)
        }
        initRunningDistanceReceiver()
    }

    override fun onResume() {
        super.onResume()

        registerRunDistanceReceiver()
    }

    private fun registerRunDistanceReceiver() {
        localBroadcastManager.registerReceiver(
            runningDistanceReceiver, IntentFilter(BROADCAST_RUN_DISTANCE)
        )
    }

    private fun findViews() {
        startRunningButton = findViewById(R.id.startRunningButton)
        exitLayout = findViewById(R.id.exitLayout)
        startLayout = findViewById(R.id.startLayout)
        stopRunningButton = findViewById(R.id.stopTimerButton)
        resultLayout = findViewById(R.id.resultLayout)
        timerTextView = findViewById(R.id.timerTextView)
        resultRunningTimeTextView = findViewById(R.id.stoppedTimerTextView)
        runDistanceTextView = findViewById(R.id.runDistanceTextView)
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
        getDeviceLocation()
        updateLocationUi()
    }

    private fun initRunningDistanceReceiver() {
        runningDistanceReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                runDistanceTextView.text = String.format(
                    resources.getString(R.string.running_activity_run_distance_pattern),
                    intent?.getDoubleExtra(EXTRA_RUN_DISTANCE, 0.0)
                )
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        getDeviceLocation()
        updateLocationUi()
    }

    private fun updateLocationUi() {
        if (googleMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
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
                        googleMap?.moveCamera(
                            CameraUpdateFactory.newLatLngZoom(
                                LatLng(
                                    it.result.latitude,
                                    it.result.longitude
                                ), DEFAULT_ZOOM.toFloat()
                            )
                        )
                    } else {
                        Log.e(TAG, "Exception: %s", it.exception)
                        googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun updateWatch(timeInHundredthOfASecond: Long) {
        timerTextView.text = formattedStopWatch(timeInHundredthOfASecond)
    }

    private fun formattedStopWatch(milliseconds: Long): String {
        return SimpleDateFormat("mm:ss:SS", Locale.getDefault()).format(milliseconds)
    }

    private fun startTimer() {
        handler = Handler(Looper.getMainLooper())
        statusChecker.run()
    }

    private fun stopTimer() {
        handler?.removeCallbacks(statusChecker)
    }

    override fun onPause() {
        localBroadcastManager.unregisterReceiver(runningDistanceReceiver)

        super.onPause()
    }

    override fun onDestroy() {
        stopTimer()

        super.onDestroy()
    }
}