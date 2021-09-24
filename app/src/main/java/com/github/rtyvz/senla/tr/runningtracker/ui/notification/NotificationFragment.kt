package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.recyclerview.widget.RecyclerView
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseFragment
import com.github.rtyvz.senla.tr.runningtracker.ui.base.BaseView
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.dialog.DeleteNotificationDialog
import com.github.rtyvz.senla.tr.runningtracker.ui.notification.presenter.NotificationPresenter
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.textview.MaterialTextView
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationFragment :
    BaseFragment<NotificationPresenter>(),
    BaseView,
    DeleteNotificationDialog.OnRemoveNotification,
    NotificationAdapter.OnSwipeStateChanger {

    companion object {
        val TAG = NotificationFragment::class.java.simpleName.toString()
        private const val TIME_PICKER_DIALOG = "TIME_PICKER_DIALOG"
        private const val DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG"
        private const val DEFAULT_HOUR = 0
        private const val DEFAULT_MINUTES = 0
        private const val EMPTY_ADAPTER_LIST = 0

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
        presenter.onCreate()
        initViews(view)
        createTimePicker()
        createDatePicker()

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

    private fun initViews(view: View) {
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
            presenter.onPositiveButtonClick(
                dateLong,
                alarmEntity,
                hour,
                minute,
                getString(R.string.notification_fragment_lets_go_running)
            )
        }
    }

    fun removeItem(position: Int) {
        notificationAdapter?.removeItem(position)
    }

    fun checkAdapterItemCount() {
        if (notificationAdapter?.itemCount == EMPTY_ADAPTER_LIST) {
            // todo I have a question
            emptyNotificationListTextView?.isVisible = true
        }
    }

    override fun removeNotification(alarmEntity: AlarmEntity, position: Int) {
        presenter.removeNotification(alarmEntity, position)
    }

    override fun changeSwipeToggle(
        isChecked: Boolean,
        alarmEntity: AlarmEntity,
        adapterPosition: Int
    ) {
        presenter.changeNotificationToggle(
            isChecked,
            alarmEntity,
            adapterPosition,
            hour,
            minute
        )
    }

    override fun createPresenter(): NotificationPresenter {
        return NotificationPresenter(this)
    }

    fun showEmptyListMessage() {
        emptyNotificationListTextView?.isVisible = true
    }

    fun hideEmptyListMessage() {
        emptyNotificationListTextView?.isVisible = false
    }

    fun setData(data: List<AlarmEntity>) {
        notificationAdapter?.addItems(data)
    }

    fun addItem(alarmEntity: AlarmEntity) {
        notificationAdapter?.addItem(alarmEntity)
    }

    fun updateItem(alarmEntity: AlarmEntity) {
        notificationAdapter?.updateItem(positionForAdapter, alarmEntity)
    }

    fun updateItem(alarmEntity: AlarmEntity, position: Int) {
        notificationAdapter?.updateItem(position, alarmEntity)
    }

    fun clearState() {
        switchChecked = false
        positionForAdapter = 0
        alarmEntity = null
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