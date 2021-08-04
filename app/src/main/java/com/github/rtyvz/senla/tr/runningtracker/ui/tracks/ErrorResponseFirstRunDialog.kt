package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class ErrorResponseFirstRunDialog : DialogFragment() {

    companion object {
        val TAG = ErrorResponseFirstRunDialog::class.java.simpleName.toString()

        fun newInstance(): ErrorResponseFirstRunDialog {
            return ErrorResponseFirstRunDialog()
        }
    }

    private var callback: ErrorResponseDialogCallBack? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        (activity as ErrorResponseDialogCallBack)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.running_activity_error_get_data))
            .setPositiveButton(R.string.running_activity_dialog_ok_button) { dialog, _ ->
                callback?.retryRequestTracksDataFromServer()
                dialog.dismiss()
            }
            .setNegativeButton(R.string.running_activity_dialog_cancel_button) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDetach() {
        callback = null

        super.onDetach()
    }

    interface ErrorResponseDialogCallBack {
        fun retryRequestTracksDataFromServer()
    }
}