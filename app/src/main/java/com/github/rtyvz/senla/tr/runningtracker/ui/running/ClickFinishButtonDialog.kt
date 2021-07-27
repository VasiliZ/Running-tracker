package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class ClickFinishButtonDialog : DialogFragment() {

    companion object {
        val TAG = ClickFinishButtonDialog::class.java.simpleName.toString()
        fun newInstance(): ClickFinishButtonDialog {
            return ClickFinishButtonDialog()
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.running_activity_dialog_finish_button_is_not_click_yet))
            .setPositiveButton(getString(R.string.running_activity_dialog_ok_button)) { dialog, _ ->
                dialog.dismiss()
            }.create()
    }
}