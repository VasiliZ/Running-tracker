package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.extension.toDateTime
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView

class NotificationAdapter(
    private val switchCheckingCallback: OnSwipeStateChanger,
    private val clickHandler: (AlarmEntity, Int, Boolean?) -> (Unit),
    private val longClickHandler: (AlarmEntity, Int) -> (Unit)
) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private val dataList = mutableListOf<AlarmEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false)
        return NotificationViewHolder(view, switchCheckingCallback, clickHandler)
            .apply {
                view.setOnLongClickListener {
                    longClickHandler(dataList[adapterPosition], adapterPosition)
                    return@setOnLongClickListener true
                }
            }
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    class NotificationViewHolder(
        private val view: View,
        private val switchCallback: OnSwipeStateChanger,
        private val clickHandler: (AlarmEntity, Int, Boolean?) -> Unit
    ) :
        RecyclerView.ViewHolder(view) {

        companion object {
            private const val DATE_TIME_PATTERN = "dd.MM.yyyy"
        }

        private var timeTextView: MaterialTextView? = null
        private var daysTextView: MaterialTextView? = null
        private var switch: SwitchMaterial? = null

        fun bind(alarmEntity: AlarmEntity) {
            timeTextView = view.findViewById(R.id.timeTextView)
            daysTextView = view.findViewById(R.id.daysTextView)
            switch = view.findViewById(R.id.switchNotification)

            view.setOnClickListener {
                clickHandler(alarmEntity, adapterPosition, switch?.isChecked)
            }
            timeTextView?.text = String.format(
                itemView.context.getString(
                    R.string.notification_adapter_time_pattern,
                    when (alarmEntity.hour) {
                        in 0..9 -> "0${alarmEntity.hour}"
                        else -> alarmEntity.hour
                    },
                    when (alarmEntity.minute) {
                        in 0..9 -> "0${alarmEntity.minute}"
                        else -> alarmEntity.minute
                    }
                )
            )
            daysTextView?.text = alarmEntity.day.toDateTime(DATE_TIME_PATTERN)

            when (alarmEntity.isEnabled) {
                1 -> switch?.isChecked = true
                else -> switch?.isChecked = false
            }

            switch?.setOnCheckedChangeListener { _, isChecked ->
                switchCallback.changeSwipeToggle(isChecked, alarmEntity, adapterPosition)
            }
        }

        fun unbind() {
            itemView.setOnClickListener(null)
            switch?.setOnCheckedChangeListener(null)
            timeTextView = null
            daysTextView = null
            switch = null
        }
    }

    fun addItems(alarmsList: List<AlarmEntity>) {
        dataList.addAll(alarmsList)
        notifyDataSetChanged()
    }

    fun addItem(alarmEntity: AlarmEntity) {
        dataList.add(alarmEntity)
        notifyDataSetChanged()
    }

    fun removeItem(position: Int) {
        dataList.removeAt(position)
        notifyDataSetChanged()
    }

    fun updateItem(position: Int, alarmEntity: AlarmEntity) {
        dataList[position] = alarmEntity
        notifyDataSetChanged()
    }

    override fun onViewRecycled(holder: NotificationViewHolder) {
        holder.unbind()

        super.onViewRecycled(holder)
    }

    interface OnSwipeStateChanger {
        fun changeSwipeToggle(isChecked: Boolean, alarmEntity: AlarmEntity, adapterPosition: Int)
    }
}