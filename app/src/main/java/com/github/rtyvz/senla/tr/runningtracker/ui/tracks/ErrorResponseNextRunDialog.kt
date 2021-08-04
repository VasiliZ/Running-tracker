package com.github.rtyvz.senla.tr.runningtracker.ui.tracks

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class ErrorResponseNextRunDialog : DialogFragment() {

    private var callBack: ErrorResponseDialogCallBack? = null

    companion object {
        val TAG = ErrorResponseNextRunDialog::class.java.simpleName.toString()

        fun newInstance(): ErrorResponseNextRunDialog {
            return ErrorResponseNextRunDialog()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callBack = (activity as ErrorResponseDialogCallBack)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.running_activity_error_get_data))
            .setPositiveButton(getString(R.string.running_activity_dialog_ok_button)) { dialog, _ ->
                callBack?.retryRequestTracksDataFromDb()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.running_activity_dialog_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }

    override fun onDetach() {
        callBack = null

        super.onDetach()
    }

    interface ErrorResponseDialogCallBack {
        fun retryRequestTracksDataFromDb()
    }
}