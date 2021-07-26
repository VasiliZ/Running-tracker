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
import android.view.Gravity
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.RunningService.Companion.ACTION_RUNNING_SERVICE_STOP
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.text.SimpleDateFormat
import java.util.*

class RunningActivity : AppCompatActivity(), OnMapReadyCallback {

    companion object {
        private const val FINE_LOCATION_REQUEST_CODE = 1101
        private const val DEFAULT_ZOOM = 15
        private const val TIMER_INTERVAL = 10L
        private val TAG = RunningActivity::class.java.simpleName.toString()
        const val EXTRA_RUN_DISTANCE = "RUN_DISTANCE"
        const val BROADCAST_RUN_DISTANCE = "local:BROADCAST_RUN_DISTANCE"
        const val BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE =
            "local:BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE"
        const val BROADCAST_WRONG_USER_TOKEN =
            "local:BROADCAST_WRONG_USER_TOKEN"
        const val BROADCAST_NETWORK_ERROR =
            "local:BROADCAST_NETWORK_ERROR"
        private const val STOP_WATCH_PATTERN = "mm:ss,SS"
        private const val DEFAULT_INT_VALUE = 0
        private const val FIRST_ARRAY_INDEX = 0
        private const val UTC_TIME_ZONE = "UTC"
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
    private lateinit var wrongUserTokenReceiver: BroadcastReceiver
    private lateinit var runDistanceTextView: MaterialTextView
    private lateinit var errorSavingTrackIntoDbReceiver: BroadcastReceiver
    private lateinit var networkErrorReceiver: BroadcastReceiver
    private lateinit var toolbar: MaterialToolbar
    private val timeFormatter = SimpleDateFormat(STOP_WATCH_PATTERN, Locale.getDefault())

    private var handler: Handler? = null
    private var timeInHundredthOfASecond = 0L
    private var timeTicker = object : Runnable {
        override fun run() {
            timeInHundredthOfASecond += TIMER_INTERVAL
            updateWatch(timeInHundredthOfASecond)
            handler?.postDelayed(this, TIMER_INTERVAL)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        findViews()
        setSupportActionBar(toolbar)

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
            val intentRunningService = Intent(this, RunningService::class.java).apply {
                putExtra(RunningService.EXTRA_CURRENT_TIME, System.currentTimeMillis())
            }

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
            val stopActionRunningServiceIntent = Intent(this, RunningService::class.java)
                .apply {
                    action = ACTION_RUNNING_SERVICE_STOP
                    putExtra(RunningService.EXTRA_FINISH_RUNNING_TIME, timeInHundredthOfASecond)
                }

            stopTimer()
            startService(stopActionRunningServiceIntent)

            resultRunningTimeTextView.text = timeFormatter.format(timeInHundredthOfASecond)
        }

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initRunningDistanceReceiver()
        initErrorSavingTrackIntoDbReceiver()
        initWrongUserTokenReceiver()
        initNetworkErrorReceiver()
    }

    override fun onResume() {
        super.onResume()

        registerRunDistanceReceiver()
        registerErrorSavingTrackIntoDbReceiver()
        registerWrongUserTokenReceiver()
        registerNetworkErrorReceiver()
    }

    private fun registerWrongUserTokenReceiver() {
        localBroadcastManager.registerReceiver(
            wrongUserTokenReceiver, IntentFilter(BROADCAST_WRONG_USER_TOKEN)
        )
    }

    private fun registerRunDistanceReceiver() {
        localBroadcastManager.registerReceiver(
            runningDistanceReceiver, IntentFilter(BROADCAST_RUN_DISTANCE)
        )
    }

    private fun registerErrorSavingTrackIntoDbReceiver() {
        localBroadcastManager.registerReceiver(
            errorSavingTrackIntoDbReceiver,
            IntentFilter(BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE)
        )
    }

    private fun registerNetworkErrorReceiver() {
        localBroadcastManager.registerReceiver(
            networkErrorReceiver, IntentFilter(BROADCAST_NETWORK_ERROR)
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
        toolbar = findViewById(R.id.toolBar)
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
                if (grantResults.isEmpty() || grantResults[FIRST_ARRAY_INDEX] != PackageManager.PERMISSION_GRANTED) {
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
                    intent?.getIntExtra(EXTRA_RUN_DISTANCE, DEFAULT_INT_VALUE)
                )
            }
        }
    }

    private fun initErrorSavingTrackIntoDbReceiver() {
        errorSavingTrackIntoDbReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                Toast.makeText(
                    this@RunningActivity,
                    R.string.running_activity_error_saving_data_into_db,
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }

    private fun initWrongUserTokenReceiver() {
        wrongUserTokenReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                startActivity(Intent(this@RunningActivity, LoginActivity::class.java))
                finish()
            }
        }
    }

    private fun initNetworkErrorReceiver() {
        networkErrorReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                val toast = Toast.makeText(
                    this@RunningActivity,
                    R.string.running_activity_network_error, Toast.LENGTH_LONG
                )
                toast.setGravity(Gravity.TOP, 0, 0)
                toast.setText(R.string.running_activity_network_error)
                toast.show()
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
        timerTextView.text = timeFormatter.format(timeInHundredthOfASecond)
    }

    private fun startTimer() {
        handler = Handler(Looper.getMainLooper())
        timeTicker.run()
    }

    private fun stopTimer() {
        handler?.removeCallbacks(timeTicker)
    }

    override fun onPause() {
        localBroadcastManager.unregisterReceiver(runningDistanceReceiver)
        localBroadcastManager.unregisterReceiver(errorSavingTrackIntoDbReceiver)
        localBroadcastManager.unregisterReceiver(wrongUserTokenReceiver)
        localBroadcastManager.unregisterReceiver(networkErrorReceiver)

        super.onPause()
    }

    override fun onDestroy() {
        stopTimer()

        super.onDestroy()
    }
}