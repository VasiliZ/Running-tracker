package com.github.rtyvz.senla.tr.runningtracker.ui.track

import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class ErrorGetCurrentTrackPointsDialog : DialogFragment() {

    companion object {
        val TAG = ErrorGetCurrentTrackPointsDialog::class.java.simpleName.toString()

        fun newInstance(): ErrorGetCurrentTrackPointsDialog {
            return ErrorGetCurrentTrackPointsDialog()
        }
    }

    private var callback: HandleErrorGetTrackPoints? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = (parentFragment as HandleErrorGetTrackPoints)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.track_fragment_error_get_data))
            .setPositiveButton(getString(R.string.track_fragment_dialog_ok_button)) { dialog, _ ->
                callback?.retryGetPoints()
                dialog.dismiss()
            }
            .setNegativeButton(getString(R.string.track_fragment_dialog_cancel_button)) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }

    override fun onDetach() {
        callback = null

        super.onDetach()
    }

    interface HandleErrorGetTrackPoints {
        fun retryGetPoints()
    }
}