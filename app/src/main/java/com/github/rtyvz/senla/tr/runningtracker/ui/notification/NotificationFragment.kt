package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationFragment : Fragment(), DeleteNotificationDialog.OnRemoveNotification {

    companion object {
        val TAG = NotificationFragment::class.java.simpleName.toString()
        private const val TIME_PICKER_DIALOG = "TIME_PICKER_DIALOG"
        private const val DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG"
        private const val IS_ENABLE_NOTIFICATION_FLAG = 1
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }

    private lateinit var fab: FloatingActionButton
    private lateinit var notificationRecyclerView: RecyclerView
    private var timePicker: MaterialTimePicker? = null
    private var datePicker: MaterialDatePicker<Long>? = null
    private var hour: Int = 0
    private var minute: Int = 0
    private var positionForAdapterAdapter: Int = 0
    private var alarmEntity: AlarmEntity? = null
    private val notificationAdapter by lazy {
        NotificationAdapter({ isChecked, alarmEntity ->
            val changedStatueAlarm = when (isChecked) {
                true -> {
                    NotificationWorkManager().createWorkForNotification(alarmEntity)
                    alarmEntity.copy(isEnabled = 1)
                }
                else -> {
                    NotificationWorkManager().deleteWork(
                        alarmEntity.alarmId.toString(),
                        requireContext()
                    )
                    alarmEntity.copy(isEnabled = 0)
                }
            }
            App.notificationRepository.updateNotification(changedStatueAlarm)
        }, { clickCallBack, position ->
            alarmEntity = clickCallBack
            positionForAdapterAdapter = position
            timePicker?.show(childFragmentManager, TIME_PICKER_DIALOG)
        }, { longClickCallback, position ->
            DeleteNotificationDialog.newInstance(longClickCallback, position).show(
                childFragmentManager, DeleteNotificationDialog.TAG
            )

        })
    }


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_notification, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        findViews(view)
        createTimePicker()
        createDatePicker()
        getNotificationsFromDb()

        notificationRecyclerView.adapter = notificationAdapter
        fab.setOnClickListener {
            timePicker?.show(childFragmentManager, TIME_PICKER_DIALOG)
        }
    }

    private fun getNotificationsFromDb() {
        App.notificationRepository.getNotifications {
            notificationAdapter.addItems(it)
        }
    }

    private fun findViews(view: View) {
        fab = view.findViewById(R.id.addNotificationFab)
        notificationRecyclerView = view.findViewById(R.id.notificationsList)
    }

    private fun createTimePicker() {
        timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText(getString(R.string.notification_fragment_select_time_for_run))
            .build()

        timePicker?.addOnPositiveButtonClickListener {
            hour = timePicker?.hour ?: 0
            minute = timePicker?.minute ?: 0
            datePicker?.show(childFragmentManager, DATE_PICKER_DIALOG)
            timePicker?.dismiss()
        }

        timePicker?.addOnDismissListener {
            it.dismiss()
        }
    }

    private fun createDatePicker() {

        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.notification_fragment_select_day_for_run))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()

        datePicker?.addOnCancelListener {
            it.dismiss()
        }

        datePicker?.addOnPositiveButtonClickListener { dateLong ->
            createNotificationWork(dateLong)
        }
    }

    private fun removeItem(longClickCallback: AlarmEntity, position: Int) {
        notificationAdapter.removeItem(position)
        App.notificationRepository.deleteNotification(longClickCallback)
    }

    private fun createNotificationWork(dateLong: Long) {
        var innerAlarmEntity: AlarmEntity? = null

        if (alarmEntity == null) {
            innerAlarmEntity = AlarmEntity(
                Random().nextInt(Int.MAX_VALUE),
                hour,
                minute,
                getString(R.string.notification_fragment_lets_go_running),
                dateLong,
                IS_ENABLE_NOTIFICATION_FLAG
            )

            innerAlarmEntity.let { settings ->
                NotificationWorkManager().createWorkForNotification(settings)
                App.notificationRepository.saveNotificationInDb(settings)
                notificationAdapter.addItem(settings)
            }
        } else {
            alarmEntity?.let { entity ->
                //remove old settings for current notify
                NotificationWorkManager().deleteWork(entity.alarmId.toString(), requireContext())
                App.notificationRepository.deleteNotification(entity)
                innerAlarmEntity = AlarmEntity(
                    Random().nextInt(Int.MAX_VALUE),
                    hour,
                    minute,
                    getString(R.string.notification_fragment_lets_go_running),
                    dateLong,
                    IS_ENABLE_NOTIFICATION_FLAG
                )
            }
            //create new notification
            innerAlarmEntity?.let {
                NotificationWorkManager().createWorkForNotification(it)
                App.notificationRepository.updateNotification(it)
                notificationAdapter.updateItem(positionForAdapterAdapter, it)
            }
            positionForAdapterAdapter = 0
            alarmEntity = null
        }
    }

    override fun onDestroy() {
        timePicker = null
        datePicker = null

        super.onDestroy()
    }

    override fun removeNotification(alarmEntity: AlarmEntity, position: Int) {
        NotificationWorkManager().deleteWork(
            alarmEntity.alarmId.toString(),
            requireContext()
        )
        App.notificationRepository.deleteNotification(alarmEntity)
        removeItem(alarmEntity, position)
    }
}