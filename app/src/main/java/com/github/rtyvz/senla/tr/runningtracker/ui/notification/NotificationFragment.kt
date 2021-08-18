package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import com.github.rtyvz.senla.tr.runningtracker.App
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationFragment : Fragment(), DeleteNotificationDialog.OnRemoveNotification,
    NotificationAdapter.OnSwipeStateChanger {

    companion object {
        val TAG = NotificationFragment::class.java.simpleName.toString()
        private const val TIME_PICKER_DIALOG = "TIME_PICKER_DIALOG"
        private const val DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG"
        private const val IS_ENABLE_NOTIFICATION_FLAG = 1
        private const val DEFAULT_HOUR = 0
        private const val DEFAULT_MINUTES = 0

        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }

    private var fab: FloatingActionButton? = null
    private var notificationRecyclerView: RecyclerView? = null
    private var timePicker: MaterialTimePicker? = null
    private var emptyNotificationListTextView: MaterialTextView? = null
    private var datePicker: MaterialDatePicker<Long>? = null
    private var hour: Int = 0
    private var minute: Int = 0
    private var positionForAdapter: Int = 0
    private var alarmEntity: AlarmEntity? = null
    private var switchChecked: Boolean = false
    private var notificationAdapter: NotificationAdapter? = null


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
        notificationAdapter = NotificationAdapter(this,
            { clickCallBack, position, isSwitchChecked ->
                isSwitchChecked?.let {
                    alarmEntity = clickCallBack
                    switchChecked = it
                    positionForAdapter = position
                    timePicker?.show(childFragmentManager, TIME_PICKER_DIALOG)
                }
            }, { longClickCallback, position ->
                DeleteNotificationDialog.newInstance(longClickCallback, position).show(
                    childFragmentManager, DeleteNotificationDialog.TAG
                )
            })

        notificationRecyclerView?.adapter = notificationAdapter
        fab?.setOnClickListener {
            timePicker?.show(childFragmentManager, TIME_PICKER_DIALOG)
        }
    }

    private fun getNotificationsFromDb() {
        App.notificationRepository.getNotifications {
            if (it.isEmpty()) {
                emptyNotificationListTextView?.isVisible = true
            } else {
                emptyNotificationListTextView?.isVisible = false
                notificationAdapter?.addItems(it)
            }
        }
    }

    private fun findViews(view: View) {
        emptyNotificationListTextView = view.findViewById(R.id.emptyNotificationListTextView)
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
            hour = timePicker?.hour ?: DEFAULT_HOUR
            minute = timePicker?.minute ?: DEFAULT_MINUTES
            datePicker?.show(childFragmentManager, DATE_PICKER_DIALOG)
            timePicker?.dismiss()
        }

        timePicker?.addOnCancelListener {
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

    private fun updateAdapter(position: Int, alarmEntity: AlarmEntity) {
        notificationAdapter?.updateItem(position, alarmEntity)
    }

    private fun removeItem(longClickCallback: AlarmEntity, position: Int) {
        notificationAdapter?.removeItem(position)
        App.notificationRepository.deleteNotification(longClickCallback)
    }

    private fun createNotificationWork(dateLong: Long) {
        var innerAlarmEntity: AlarmEntity
        emptyNotificationListTextView?.isVisible = false
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
                notificationAdapter?.addItem(settings)
            }
        } else {
            alarmEntity?.let { entity ->
                //remove old settings for current notify
                //because task does't trigger if was been cancel
                NotificationWorkManager().deleteWork(entity.alarmId.toString())
                App.notificationRepository.deleteNotification(entity)

                innerAlarmEntity = AlarmEntity(
                    Random().nextInt(Int.MAX_VALUE),
                    hour,
                    minute,
                    getString(R.string.notification_fragment_lets_go_running),
                    dateLong,
                    IS_ENABLE_NOTIFICATION_FLAG
                )
                innerAlarmEntity.let {
                    if (entity.isEnabled == IS_ENABLE_NOTIFICATION_FLAG) {
                        NotificationWorkManager().createWorkForNotification(it)
                    }
                    App.notificationRepository.saveNotificationInDb(it)
                    notificationAdapter?.updateItem(positionForAdapter, it)
                }
            }

            //create new notification
            switchChecked = false
            positionForAdapter = 0
            alarmEntity = null
        }
    }


    override fun removeNotification(alarmEntity: AlarmEntity, position: Int) {
        NotificationWorkManager().deleteWork(alarmEntity.alarmId.toString())
        App.notificationRepository.deleteNotification(alarmEntity)
        removeItem(alarmEntity, position)
        if (notificationAdapter?.itemCount == 0) {
            emptyNotificationListTextView?.isVisible = true
        }
    }

    override fun changeSwipeToggle(
        isChecked: Boolean,
        alarmEntity: AlarmEntity,
        adapterPosition: Int
    ) {
        when (isChecked) {
            true -> {
                val newEntity = alarmEntity.copy(
                    alarmId = Random().nextInt(Int.MAX_VALUE),
                    hour = hour,
                    minute = minute,
                    isEnabled = 1,
                    oldId = alarmEntity.alarmId
                )

                if (notificationRecyclerView?.isComputingLayout == false) {
                    updateAdapter(adapterPosition, newEntity)
                }

                NotificationWorkManager().createWorkForNotification(newEntity)
                App.notificationRepository.updateNotification(newEntity)
            }
            else -> {
                val newAlarm = alarmEntity.copy(isEnabled = 0, oldId = alarmEntity.alarmId)

                if (notificationRecyclerView?.isComputingLayout == false) {
                    updateAdapter(adapterPosition, newAlarm)
                }
                App.notificationRepository.updateNotification(newAlarm)
                NotificationWorkManager().deleteWork(alarmEntity.alarmId.toString())
            }
        }
    }

    override fun onDestroyView() {
        fab = null
        timePicker = null
        timePicker?.clearOnPositiveButtonClickListeners()
        timePicker?.clearOnNegativeButtonClickListeners()
        datePicker = null
        datePicker?.clearOnPositiveButtonClickListeners()
        datePicker?.clearOnNegativeButtonClickListeners()
        notificationRecyclerView = null

        super.onDestroyView()
    }
}