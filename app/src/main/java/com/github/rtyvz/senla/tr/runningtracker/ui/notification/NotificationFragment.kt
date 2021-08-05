package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.github.rtyvz.senla.tr.runningtracker.R
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.util.*

class NotificationFragment : Fragment() {

    companion object {
        val TAG = NotificationFragment::class.java.simpleName.toString()
        private const val TIME_PICKER_DIALOG = "TIME_PICKER_DIALOG"
        private const val DATE_PICKER_DIALOG = "DATE_PICKER_DIALOG"
        fun newInstance(): NotificationFragment {
            return NotificationFragment()
        }
    }

    private lateinit var fab: FloatingActionButton
    private var timePicker: MaterialTimePicker? = null
    private var datePicker: MaterialDatePicker<Long>? = null

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
        fab.setOnClickListener {
            timePicker?.show(childFragmentManager, TIME_PICKER_DIALOG)
        }
    }

    private fun findViews(view: View) {
        fab = view.findViewById(R.id.addNotificationFab)
    }

    private fun createTimePicker() {
        timePicker = MaterialTimePicker.Builder().setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(Calendar.getInstance().get(Calendar.HOUR_OF_DAY))
            .setMinute(Calendar.getInstance().get(Calendar.MINUTE))
            .setTitleText(getString(R.string.natifocation_fragment_select_time_for_run))
            .build()

        timePicker?.addOnPositiveButtonClickListener {
            Log.d(TAG, "createTimePicker: ${timePicker?.hour} ${timePicker?.minute}")
            datePicker?.show(childFragmentManager, DATE_PICKER_DIALOG)
            timePicker?.dismiss()
        }

        timePicker?.addOnDismissListener {
            it.dismiss()
        }
    }

    private fun createDatePicker() {
        datePicker = MaterialDatePicker.Builder.datePicker()
            .setTitleText(getString(R.string.natifocation_fragment_select_day_for_run))
            .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
            .build()
    }

    override fun onDestroy() {
        timePicker = null
        datePicker = null

        super.onDestroy()
    }
}