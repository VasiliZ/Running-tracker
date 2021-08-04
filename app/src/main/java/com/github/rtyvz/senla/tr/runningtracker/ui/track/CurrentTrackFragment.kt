package com.github.rtyvz.senla.tr.runningtracker.ui.track

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.network.Result
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.PointEntity
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.humanizeDistance
import com.github.rtyvz.senla.tr.runningtracker.extension.toDateTimeWithUTC
import com.github.rtyvz.senla.tr.runningtracker.extension.toLatLng
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.model.*
import com.google.android.material.textview.MaterialTextView

class CurrentTrackFragment : Fragment(), GoogleMap.OnMarkerClickListener {

    companion object {
        fun newInstance(trackEntity: TrackEntity): CurrentTrackFragment {
            return CurrentTrackFragment().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_TRACK_ENTITY, trackEntity)
                }
            }
        }

        private const val EXTRA_TRACK_ENTITY = "TRACK_ENTITY"
        private const val START_MARKER_TITLE = "Старт"
        private const val FINISH_MARKER_TITLE = "Финиш"
        private const val DATE_TIME_PATTERN = "HH:mm:ss,SS"
        private const val CAMERA_PADDING = 300
        val TAG = CurrentTrackFragment::class.java.simpleName.toString()
    }

    private var mapView: MapView? = null
    private var map: GoogleMap? = null
    private lateinit var distanceTextView: MaterialTextView
    private lateinit var timeActionTextView: MaterialTextView
    private var trackPoints: List<PointEntity> = emptyList()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_current_track, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)

        mapView?.onCreate(savedInstanceState)
        mapView?.onResume()

        try {
            MapsInitializer.initialize(requireContext().applicationContext)
        } catch (e: Exception) {
            Log.d(TAG, "onViewCreated: map init error")
        }

        mapView?.getMapAsync { googleMap ->
            val track = arguments?.getParcelable<TrackEntity>(EXTRA_TRACK_ENTITY)
            getPoints(track)
            map = googleMap
        }
    }

    override fun onResume() {
        super.onResume()

        mapView?.onResume()
    }

    private fun findViews(view: View) {
        distanceTextView = view.findViewById(R.id.distanceTextView)
        timeActionTextView = view.findViewById(R.id.timeActionTextView)
        mapView = view.findViewById(R.id.currentTrackMapView)
    }

    private fun getPoints(trackEntity: TrackEntity?) {
        if (trackEntity != null) {
            App.mainRunningRepository.getTrackPoints(trackEntity.id, trackEntity.beginsAt) {
                when (it) {
                    is Result.Success -> {
                        trackPoints = it.data.listPoints
                        if (trackPoints.isNotEmpty()) {
                            map?.clear()
                            setupMapData(map, trackPoints)
                            drawPath(map, trackPoints)
                            setDataOnUI(trackEntity)
                        }
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    private fun setDataOnUI(trackEntity: TrackEntity) {
        distanceTextView.text = String.format(
            getString(R.string.current_fragment_run_distance_pattern),
            trackEntity.distance,
            trackEntity.distance.humanizeDistance()
        )
        timeActionTextView.text = trackEntity.time.toDateTimeWithUTC(DATE_TIME_PATTERN)
    }

    private fun drawPath(googleMap: GoogleMap?, listPoints: List<PointEntity>) {
        googleMap?.let {
            val lineOptions = PolylineOptions()
            with(lineOptions) {
                addAll(listPoints.map { point ->
                    point.toLatLng()
                })
                width(10f)
                color(ContextCompat.getColor(requireContext(), R.color.main_app_color))
                it.addPolyline(lineOptions)
            }
        }
    }

    private fun setupMapData(googleMap: GoogleMap?, pointsList: List<PointEntity>) {
        googleMap?.let { it ->
            val startPoint = pointsList.first()
            val finishPoint = pointsList.last()
            val startMarker = MarkerOptions().position(
                LatLng(
                    startPoint.lat,
                    startPoint.lng
                )
            )
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
                .title(START_MARKER_TITLE)
            val finishMarker = MarkerOptions()
                .position(LatLng(finishPoint.lat, finishPoint.lng))
                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                .title(FINISH_MARKER_TITLE)
            it.addMarker(startMarker)
            it.addMarker(finishMarker)
            val cameraUpdate =
                CameraUpdateFactory.newLatLngBounds(getMiddlePoint(pointsList), CAMERA_PADDING)
            it.animateCamera(cameraUpdate)
        }
    }

    private fun getMiddlePoint(pointsList: List<PointEntity>): LatLngBounds {
        val bounds = LatLngBounds.builder()
        pointsList.forEach {
            bounds.include(LatLng(it.lat, it.lng))
        }
        return bounds.build()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true
    }

    fun setTrack(trackEntity: TrackEntity) {
        getPoints(trackEntity)
    }

    override fun onPause() {
        mapView?.onPause()

        super.onPause()
    }

    override fun onDestroy() {
        mapView?.onDestroy()

        super.onDestroy()
    }
}