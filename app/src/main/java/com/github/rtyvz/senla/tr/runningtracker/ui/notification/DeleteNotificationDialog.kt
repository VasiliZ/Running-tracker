package com.github.rtyvz.senla.tr.runningtracker.ui.notification

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.entity.ui.AlarmEntity

class DeleteNotificationDialog : DialogFragment() {

    companion object {
        val TAG = DeleteNotificationDialog::class.java.simpleName.toString()
        private const val EXTRA_ALARM_ENTITY = "ALARM_ENTITY"
        private const val EXTRA_POSITION = "POSITION"

        fun newInstance(longClickCallback: AlarmEntity, position: Int): DeleteNotificationDialog {
            return DeleteNotificationDialog().apply {
                arguments = Bundle().apply {
                    putParcelable(EXTRA_ALARM_ENTITY, longClickCallback)
                    putInt(EXTRA_POSITION, position)
                }
            }
        }
    }

    private var callback: OnRemoveNotification? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = (parentFragment as OnRemoveNotification)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): AlertDialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.notification_adapter_delete_notification_title))
            .setNegativeButton(getString(R.string.notification_adapter_dialog_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .setPositiveButton(getString(R.string.notification_adapter_dialog_ok_button)) { dialog, _ ->
                arguments?.let { bundle ->
                    val entity = bundle.getParcelable<AlarmEntity>(EXTRA_ALARM_ENTITY)
                    if (entity != null) {
                        callback?.removeNotification(
                            entity,
                            bundle.getInt(EXTRA_POSITION)
                        )
                    }
                }
                dialog.dismiss()
            }.create()
    }

    override fun onDetach() {
        callback = null

        super.onDetach()
    }

    interface OnRemoveNotification {
        fun removeNotification(alarmEntity: AlarmEntity, position: Int)
    }
}