package com.github.rtyvz.senla.tr.runningtracker.ui.running.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class EnableGpsDialog : DialogFragment() {

    companion object {
        val TAG = EnableGpsDialog::class.java.simpleName.toString()

        fun newInstance(): EnableGpsDialog {
            return EnableGpsDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.running_activity_are_you_enable_gps))
            .setPositiveButton(getString(R.string.running_activity_dialog_confirm_click_finish_button)) { dialog, _ ->
                dialog.dismiss()
            }
            .create()
    }
}