package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.Manifest
import android.animation.AnimatorInflater
import android.content.*
import android.content.pm.PackageManager
import android.location.LocationManager
import android.os.*
import android.view.Gravity
import android.view.MenuItem
import android.view.View
import android.widget.Toast
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.isVisible
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.humanizeDistance
import com.github.rtyvz.senla.tr.runningtracker.extension.toDateTimeWithoutUTCOffset
import com.github.rtyvz.senla.tr.runningtracker.extension.toLatLng
import com.github.rtyvz.senla.tr.runningtracker.service.RunningService
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.running.dialogs.AreYouRunDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.running.dialogs.EnableGpsDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter.RunningActivityContract
import com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter.RunningActivityPresenter
import com.github.rtyvz.senla.tr.runningtracker.ui.running.presenter.RunningActivityPresenter.Companion.FINE_LOCATION_REQUEST_CODE
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView
import java.util.*

class RunningActivity :
    BaseActivity<RunningActivityContract.PresenterRunningActivity, RunningActivityContract.ViewRunningActivity>(),
    RunningActivityContract.ViewRunningActivity,
    OnMapReadyCallback,
    AreYouRunDialog.AreYouWantToRunningYetContract {

    companion object {

        private const val DEFAULT_ZOOM = 15
        private const val TIMER_INTERVAL = 10L
        private const val STOP_WATCH_PATTERN = "mm:ss,SS"
        private const val DEFAULT_INT_VALUE = 0
        private const val CAMERA_PADDING = 300
        private const val WIDTH_PATH_LINE = 10f
        private const val START_MARKER_TITLE = "Старт"
        private const val FINISH_MARKER_TITLE = "Финиш"

        private const val NANO_TIME_DIVIDER = 1000000
        const val EXTRA_RUN_DISTANCE = "RUN_DISTANCE"
        const val BROADCAST_RUN_DISTANCE = "local:BROADCAST_RUN_DISTANCE"
        const val BROADCAST_ARE_YOU_RUN = "local:BROADCAST_ARE_YOU_RUN"
        const val BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE =
            "local:BROADCAST_ERROR_SAVE_TRACK_TO_LOCAL_STORAGE"
        const val BROADCAST_WRONG_USER_TOKEN =
            "local:BROADCAST_WRONG_USER_TOKEN"
        const val BROADCAST_NETWORK_ERROR =
            "local:BROADCAST_NETWORK_ERROR"
        const val BROADCAST_GPS_ENABLED = "local:BROADCAST_GPS_ENABLED"
        const val BROADCAST_GPS_DISABLED = "local:BROADCAST_GPS_DISABLED"
        const val EXTRA_TRACK_POINTS = "TRACK_POINTS"
    }

    private lateinit var locationProvider: FusedLocationProviderClient
    private lateinit var localBroadcastManager: LocalBroadcastManager
    private lateinit var runningDistanceReceiver: BroadcastReceiver
    private lateinit var wrongUserTokenReceiver: BroadcastReceiver
    private lateinit var gpsProviderDisabledReceiver: BroadcastReceiver
    private lateinit var gpsProviderEnabledReceiver: BroadcastReceiver
    private lateinit var errorSavingTrackIntoDbReceiver: BroadcastReceiver
    private lateinit var networkErrorReceiver: BroadcastReceiver
    private lateinit var areYouRunReceiver: BroadcastReceiver
    private var locationPermissionGranted: Boolean = false
    private var googleMap: GoogleMap? = null
    private var startRunningButton: MaterialButton? = null
    private var exitLayout: CardView? = null
    private var startLayout: CardView? = null
    private var finishRunningButton: MaterialButton? = null
    private var resultLayout: CardView? = null
    private var timerTextView: MaterialTextView? = null
    private var resultRunningTimeTextView: MaterialTextView? = null
    private var gpsStatus: MaterialTextView? = null
    private var runDistanceTextView: MaterialTextView? = null
    private var toolbar: MaterialToolbar? = null
    private var startTimerRunningTime: Long = 0L
    private var startRunMillis: Long = 0L
    private var handler: Handler? = null
    private var isFinishButtonClicked = false
    private var isStartButtonClicked = false
    private var timeMillis = 0L
    private var timeTicker = object : Runnable {
        override fun run() {
            timeMillis = (System.nanoTime() - startTimerRunningTime) / NANO_TIME_DIVIDER
            updateWatch(timeMillis)
            handler?.postDelayed(this, TIMER_INTERVAL)
        }
    }
    private var isBoundToRunningService = false
    private var service: RunningService? = null
    private val runningServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(p0: ComponentName?, p1: IBinder?) {
            val binder = p1 as RunningService.RunningServiceBinder
            service = binder.getService()
            isBoundToRunningService = true
        }

        override fun onServiceDisconnected(p0: ComponentName?) {
            isBoundToRunningService = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_running)

        initViews()
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

        startRunningButton?.setOnClickListener {
            getPresenter().startRunning()
        }

        finishRunningButton?.setOnClickListener {
            getPresenter().stopRunning()
        }

        supportActionBar?.setDisplayShowHomeEnabled(true)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        initErrorSavingTrackIntoDbReceiver()
        initWrongUserTokenReceiver()
        initNetworkErrorReceiver()
        initAreYouRunReceiver()
        initGpsEnabledReceiver()
        initGpsDisabledReceiver()
    }


    override fun onResume() {
        super.onResume()

        registerRunDistanceReceiver()
        registerErrorSavingTrackIntoDbReceiver()
        registerWrongUserTokenReceiver()
        registerNetworkErrorReceiver()
        registerAreYouRunReceiver()
        registerGpsEnabledReceiver()
        registerGpsDisabledReceiver()
    }

    private fun registerGpsDisabledReceiver() {
        localBroadcastManager.registerReceiver(
            gpsProviderDisabledReceiver, IntentFilter(BROADCAST_GPS_DISABLED)
        )
    }

    private fun registerGpsEnabledReceiver() {
        localBroadcastManager.registerReceiver(
            gpsProviderEnabledReceiver, IntentFilter(BROADCAST_GPS_ENABLED)
        )
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

    private fun registerAreYouRunReceiver() {
        localBroadcastManager.registerReceiver(
            areYouRunReceiver,
            IntentFilter(BROADCAST_ARE_YOU_RUN)
        )
    }

    private fun initViews() {
        gpsStatus = findViewById(R.id.gpsStatusTextView)
        startRunningButton = findViewById(R.id.startRunningButton)
        exitLayout = findViewById(R.id.exitLayout)
        startLayout = findViewById(R.id.startLayout)
        finishRunningButton = findViewById(R.id.stopTimerButton)
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
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        getPresenter().checkRequstPermissionsResult(requestCode, permissions, grantResults)
    }

//    private fun initRunningDistanceReceiver() {
//        runningDistanceReceiver = object : BroadcastReceiver() {
//            override fun onReceive(context: Context?, intent: Intent?) {
//                val distance = intent?.getIntExtra(EXTRA_RUN_DISTANCE, DEFAULT_INT_VALUE)
//                intent?.getParcelableArrayListExtra<PointEntity>(EXTRA_TRACK_POINTS)?.toList()
//                    ?.let {
//
//                    }
//            }
//        }
//    }

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
                App.mainRunningRepository.clearCache()
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
                toast.show()
            }
        }
    }

    private fun initAreYouRunReceiver() {
        areYouRunReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {

            }
        }
    }

    private fun initGpsDisabledReceiver() {
        gpsProviderDisabledReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                gpsStatus?.isVisible = true
            }
        }
    }

    private fun initGpsEnabledReceiver() {
        gpsProviderEnabledReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context?, intent: Intent?) {
                gpsStatus?.isVisible = false
            }
        }
    }

    override fun onMapReady(map: GoogleMap) {
        googleMap = map
        getDeviceLocation()
        updateLocationUi()
    }

    override fun updateLocationUi() {
        if (googleMap == null) {
            return
        }
        try {
            if (locationPermissionGranted) {
                googleMap?.isMyLocationEnabled = true
                googleMap?.uiSettings?.isMyLocationButtonEnabled = true
            }
        } catch (e: SecurityException) {
            showEnableGpsToast()
        }
    }

    override fun startRunningService() {
        if (!isBoundToRunningService) {
            bindService(
                Intent(this, RunningService::class.java),
                runningServiceConnection,
                Context.BIND_AUTO_CREATE
            )
            getPresenter().saveTrack(startTimerRunningTime)
        }
//        val intentRunningService = Intent(this, RunningService::class.java).apply {
//            putExtra(
//                RunningService.EXTRA_CURRENT_TIME,
//                startRunMillis
//            )
//        }
//
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//            startForegroundService(intentRunningService)
//        } else {
//            startService(intentRunningService)
//        }
    }

    override fun showEnableGpsDialog() {
        EnableGpsDialog.newInstance().show(supportFragmentManager, EnableGpsDialog.TAG)
    }

    override fun getLocationManager() =
        getSystemService(Context.LOCATION_SERVICE) as LocationManager

    override fun finishActivity() {
        finish()
    }

    private fun drawRunningPath(points: List<PointEntity>) {
        if (points.isNotEmpty()) {
            googleMap?.let {
                with(PolylineOptions()) {
                    addAll(points.map { point ->
                        point.toLatLng()
                    })
                    width(WIDTH_PATH_LINE)
                    color(ContextCompat.getColor(this@RunningActivity, R.color.main_app_color))
                    it.addPolyline(this)
                }
            }
        }
    }

    private fun setupMapData(pointsList: List<PointEntity>) {
        if (pointsList.isNotEmpty()) {
            googleMap?.let { it ->
                val startPoint = pointsList.first()
                val finishPoint = pointsList.last()
                val startMarker = MarkerOptions().position(
                    LatLng(
                        startPoint.lat,
                        startPoint.lng
                    )
                ).icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                    .title(START_MARKER_TITLE)
                val finishMarker = MarkerOptions()
                    .position(LatLng(finishPoint.lat, finishPoint.lng))
                    .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    .title(FINISH_MARKER_TITLE)
                it.addMarker(startMarker)
                it.addMarker(finishMarker)
                val cameraUpdate =
                    CameraUpdateFactory.newLatLngBounds(
                        getMiddlePoint(pointsList),
                        CAMERA_PADDING
                    )
                it.animateCamera(cameraUpdate)
            }
        }
    }

    private fun getMiddlePoint(pointsList: List<PointEntity>): LatLngBounds {
        val bounds = LatLngBounds.builder()
        pointsList.forEach {
            bounds.include(LatLng(it.lat, it.lng))
        }
        return bounds.build()
    }

    override fun getDeviceLocation() {
        try {
            if (locationPermissionGranted) {
                locationProvider.lastLocation.addOnCompleteListener { location ->
                    if (location.isSuccessful) {
                        if (location.result != null) {
                            googleMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        location.result.latitude,
                                        location.result.longitude
                                    ), DEFAULT_ZOOM.toFloat()
                                )
                            )
                        } else {
                            googleMap?.uiSettings?.isMyLocationButtonEnabled = false
                        }
                    }
                }
            }
        } catch (e: SecurityException) {
            showEnableGpsToast()
        }
    }

    private fun showEnableGpsToast() {
        Toast.makeText(
            this,
            R.string.running_activity_havent_got_gps_permitions,
            Toast.LENGTH_LONG
        ).show()
    }

    private fun updateWatch(timeInHundredthOfASecond: Long) {
        timerTextView?.text =
            timeInHundredthOfASecond.toDateTimeWithoutUTCOffset(STOP_WATCH_PATTERN)
    }

    override fun startTimer() {
        handler = Handler(Looper.getMainLooper())
        timeTicker.run()
    }

    override fun stopTimer() {
        handler?.removeCallbacks(timeTicker)
    }

    override fun displayRunningTime() {
        resultRunningTimeTextView?.text =
            timeMillis.toDateTimeWithoutUTCOffset(STOP_WATCH_PATTERN)
    }

    override fun showAreYouRunningDialog() {
        AreYouRunDialog.newInstance().show(supportFragmentManager, AreYouRunDialog.TAG)
    }

    override fun onPause() {
        localBroadcastManager.unregisterReceiver(runningDistanceReceiver)
        localBroadcastManager.unregisterReceiver(errorSavingTrackIntoDbReceiver)
        localBroadcastManager.unregisterReceiver(wrongUserTokenReceiver)
        localBroadcastManager.unregisterReceiver(networkErrorReceiver)
        localBroadcastManager.unregisterReceiver(areYouRunReceiver)
        localBroadcastManager.unregisterReceiver(gpsProviderEnabledReceiver)
        localBroadcastManager.unregisterReceiver(gpsProviderDisabledReceiver)

        super.onPause()
    }

    override fun tryToRunningAgain() {
        startTimerRunningTime = 0L
        startRunMillis = 0L
        handler = null
        startRunningButton?.isClickable = true
        startAnimation(resultLayout as CardView, R.animator.flip_out)
        startAnimation(startLayout as CardView, R.animator.flip_in)
    }

    override fun onBackPressed() {
        if (isStartButtonClicked && !isFinishButtonClicked) {
            showNeedsClickFinishToast()
        } else {
            finish()
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return getPresenter().checkFinishButtonWasClicked(
            item.itemId,
            isStartButtonClicked,
            isFinishButtonClicked
        )
    }

    override fun showNeedsClickFinishToast() {
        Toast.makeText(
            this,
            getString(R.string.running_activity_dialog_finish_button_is_not_click_yet),
            Toast.LENGTH_LONG
        ).show()
    }

    override fun setGrandPermissionFlag(flag: Boolean) {
        locationPermissionGranted = true
    }

    override fun stopRunAnimation() {
        startAnimation(exitLayout as CardView, R.animator.flip_out)
        startAnimation(resultLayout as CardView, R.animator.flip_in)
    }

    override fun disableButtons() {
        finishRunningButton?.isClickable = false
        startRunningButton?.isClickable = false
    }

    override fun changeButtonClickable() {
        isFinishButtonClicked = true
        isStartButtonClicked = false
    }

    override fun stopRunningService() {
        if (isBoundToRunningService) {
            val distance = service?.getDistance()
            runDistanceTextView?.text = String.format(
                resources.getString(R.string.running_activity_run_distance_pattern),
                distance,
                distance?.humanizeDistance()
            )

            service?.getTrackPoints(startTimerRunningTime)?.let {
                drawRunningPath(it)
                setupMapData(it)
            }

            getPresenter().updateTrackAfterRun(
                service?.getTrackPoints(startTimerRunningTime),
                distance,
                startTimerRunningTime,
                timeMillis
            )
            unbindService(runningServiceConnection)
        }

//        val stopActionRunningServiceIntent = Intent(this, RunningService::class.java)
//            .apply {
//                action = RunningService.ACTION_RUNNING_SERVICE_STOP
//                putExtra(RunningService.EXTRA_FINISH_RUNNING_TIME, timeMillis)
//            }
//        startService(stopActionRunningServiceIntent)
    }

    override fun onDestroy() {
        stopTimer()
        googleMap?.clear()
        googleMap = null

        super.onDestroy()
    }

    override fun createPresenter() = RunningActivityPresenter()
    override fun initTimer() {
        startTimerRunningTime = System.nanoTime()
        startRunMillis = System.currentTimeMillis()
    }

    override fun startAnimation() {
        isStartButtonClicked = true
        startAnimation(startLayout as CardView, R.animator.flip_out)
        startAnimation(exitLayout as CardView, R.animator.flip_in)
    }

    override fun setUpAnimatedLayouts() {
        exitLayout?.isVisible = true
        startRunningButton?.isClickable = false
        finishRunningButton?.isClickable = true
    }

    override fun showLoading() {}
    override fun hideLoading() {}
}