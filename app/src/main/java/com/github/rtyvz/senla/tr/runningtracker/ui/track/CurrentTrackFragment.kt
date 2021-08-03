package com.github.rtyvz.senla.tr.runningtracker.ui.track

import android.os.Bundle
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
import com.google.android.gms.maps.*
import com.google.android.gms.maps.model.*
import com.google.android.material.textview.MaterialTextView

class CurrentTrackFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

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

    private lateinit var trackEntity: TrackEntity
    private var map: GoogleMap? = null
    private var currentTrackMapView: MapView? = null
    private lateinit var distanceTextView: MaterialTextView
    private lateinit var timeActionTextView: MaterialTextView

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

        with(currentTrackMapView) {
            this?.onCreate(savedInstanceState)
            this?.onResume()
            this?.getMapAsync(this@CurrentTrackFragment)
            MapsInitializer.initialize(requireContext())
        }
    }

    private fun findViews(view: View) {
        currentTrackMapView = view.findViewById(R.id.currentTrackMapView)
        distanceTextView = view.findViewById(R.id.distanceTextView)
        timeActionTextView = view.findViewById(R.id.timeActionTextView)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap
        getPoints(googleMap)
    }

    private fun getPoints(googleMap: GoogleMap) {
        val track = arguments?.getParcelable<TrackEntity>(EXTRA_TRACK_ENTITY)
        if (track != null) {
            trackEntity = track
            App.mainRunningRepository.getTrackPoints(track.id, track.beginsAt) {
                when (it) {
                    is Result.Success -> {
                        setupMapData(googleMap, it.data.listPoints)
                        drawPath(googleMap, it.data.listPoints)
                        setDataOnUI()
                    }
                    is Result.Error -> {

                    }
                }
            }
        }
    }

    private fun setDataOnUI() {
        distanceTextView.text = String.format(
            getString(R.string.current_fragment_run_distance_pattern),
            trackEntity.distance,
            trackEntity.distance.humanizeDistance()
        )
        timeActionTextView.text = trackEntity.time.toDateTimeWithUTC(DATE_TIME_PATTERN)
    }

    private fun drawPath(googleMap: GoogleMap, listPoints: List<PointEntity>) {
        val lineOptions = PolylineOptions()
        with(lineOptions) {
            addAll(listPoints.map {
                it.toLatLng()
            })
            width(10f)
            color(ContextCompat.getColor(requireContext(), R.color.main_app_color))
            googleMap.addPolyline(lineOptions)
        }
    }

    private fun setupMapData(googleMap: GoogleMap, pointsList: List<PointEntity>) {
        val startPoint = pointsList.first()
        val finishPoint = pointsList.last()
        val startMarker = MarkerOptions().position(LatLng(startPoint.lat, startPoint.lng))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN))
            .title(START_MARKER_TITLE)
        val finishMarker = MarkerOptions()
            .position(LatLng(finishPoint.lat, finishPoint.lng))
            .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            .title(FINISH_MARKER_TITLE)
        googleMap.addMarker(startMarker)
        googleMap.addMarker(finishMarker)
        val cameraUpdate =
            CameraUpdateFactory.newLatLngBounds(getMiddlePoint(pointsList), CAMERA_PADDING)
        googleMap.animateCamera(cameraUpdate)
    }

    private fun getMiddlePoint(pointsList: List<PointEntity>): LatLngBounds {
        val bounds = LatLngBounds.builder()
        pointsList.forEach {
            bounds.include(LatLng(it.lat, it.lng))
        }
        return bounds.build()
    }

    override fun onDestroy() {
        super.onDestroy()

        currentTrackMapView?.onDestroy()
    }

    override fun onLowMemory() {
        currentTrackMapView?.onLowMemory()
        super.onLowMemory()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        marker.showInfoWindow()
        return true
    }
}