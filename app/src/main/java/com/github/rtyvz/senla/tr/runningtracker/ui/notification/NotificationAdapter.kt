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

class NotificationAdapter(private val switchCheckingCallback: (Boolean, AlarmEntity) -> (Unit)) :
    RecyclerView.Adapter<NotificationAdapter.NotificationViewHolder>() {
    private val dataList = mutableListOf<AlarmEntity>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotificationViewHolder {
        return NotificationViewHolder(
            LayoutInflater.from(parent.context).inflate(R.layout.item_notification, parent, false),
            switchCheckingCallback
        )
    }

    override fun onBindViewHolder(holder: NotificationViewHolder, position: Int) {
        holder.bind(dataList[position])
    }

    override fun getItemCount() = dataList.size

    class NotificationViewHolder(
        view: View,
        private val switchCallback: (Boolean, AlarmEntity) -> (Unit)
    ) :
        RecyclerView.ViewHolder(view) {
        private val timeTextView: MaterialTextView = view.findViewById(R.id.timeTextView)
        private val daysTextView: MaterialTextView = view.findViewById(R.id.daysTextView)
        private val switch: SwitchMaterial = view.findViewById(R.id.switchNotification)

        fun bind(alarmEntity: AlarmEntity) {
            timeTextView.text = "${alarmEntity.hour} : ${alarmEntity.minute}"
            daysTextView.text = alarmEntity.day.toDateTime("dd.MM.yyyy")

            when (alarmEntity.isEnabled) {
                1 -> switch.isChecked = true
                else -> switch.isChecked = false
            }

            switch.setOnCheckedChangeListener { _, isChecked ->
                switchCallback(isChecked, alarmEntity)
            }
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
        notifyItemChanged(position)
    }
}