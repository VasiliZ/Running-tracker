package com.github.rtyvz.senla.tr.runningtracker.ui.tracks.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class ErrorFetchingPointsDialog : DialogFragment() {

    companion object {
        val TAG = ErrorFetchingPointsDialog::class.java.simpleName.toString()

        fun newInstance(): ErrorFetchingPointsDialog {
            return ErrorFetchingPointsDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.tracks_fragment_error_get_points))
            .setPositiveButton(getString(R.string.tracks_fragment_dialog_confirm_click_finish_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}