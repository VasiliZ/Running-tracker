package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.TrackEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.humanizeDistance
import com.github.rtyvz.senla.tr.runningtracker.extension.toDateTime
import com.github.rtyvz.senla.tr.runningtracker.extension.toDateTimeWithUTC
import com.google.android.material.textview.MaterialTextView

class TracksAdapter(private val handleItemClick: (TrackEntity) -> (Unit)) :
    ListAdapter<TrackEntity, TracksAdapter.RunningViewHolder>(DiffUtilCallback()) {

    companion object {
        private const val DATE_START_PATTERN = "dd MM yyyy"
        private const val RUNNING_TIME_PATTERN = "HH:mm:ss,SS"
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RunningViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.track_item, parent, false)
        return RunningViewHolder(view).apply {
            this.itemView.setOnClickListener {
                handleItemClick(currentList[adapterPosition])
            }
        }
    }

    override fun onBindViewHolder(holder: RunningViewHolder, position: Int) {
        holder.bind(currentList[position])
    }

    class RunningViewHolder(private val view: View) : RecyclerView.ViewHolder(view) {
        private lateinit var dateOfRunning: MaterialTextView
        private lateinit var distanceTextView: MaterialTextView
        private lateinit var timeRunning: MaterialTextView

        fun bind(data: TrackEntity) {
            dateOfRunning = view.findViewById(R.id.dateOdRunTextView)
            distanceTextView = view.findViewById(R.id.runDistanceTextView)
            timeRunning = view.findViewById(R.id.timeRunningTextView)

            dateOfRunning.text = data.beginsAt.toDateTime(DATE_START_PATTERN)
            distanceTextView.text = String.format(
                view.context.getString(
                    R.string.running_activity_run_distance_pattern,
                    data.distance.toString(),
                    data.distance.humanizeDistance().trim()
                )
            )
            timeRunning.text = data.time.toDateTimeWithUTC(RUNNING_TIME_PATTERN)
        }
    }

    class DiffUtilCallback : DiffUtil.ItemCallback<TrackEntity>() {
        override fun areItemsTheSame(oldItem: TrackEntity, newItem: TrackEntity) =
            oldItem.beginsAt == newItem.beginsAt

        override fun areContentsTheSame(oldItem: TrackEntity, newItem: TrackEntity) =
            oldItem.beginsAt == newItem.beginsAt
    }
}