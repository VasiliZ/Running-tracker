package com.github.rtyvz.senla.tr.runningtracker.ui.running

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import com.github.rtyvz.senla.tr.runningtracker.R

class AreYouRunDialog : DialogFragment() {

    companion object {
        val TAG = AreYouRunDialog::class.java.simpleName.toString()
        fun newInstance(): AreYouRunDialog {
            return AreYouRunDialog()
        }
    }

    private var callback: AreYouWantToRunningYetContract? = null

    override fun onAttach(context: Context) {
        super.onAttach(context)

        callback = (activity as AreYouWantToRunningYetContract)
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return AlertDialog.Builder(requireContext())
            .setTitle(getString(R.string.running_activity_are_you_running_notify))
            .setPositiveButton(getString(R.string.running_activity_dialog_ok_button)) { dialog, _ ->
                dialog.cancel()
                callback?.tryToRunningAgain()
            }
            .setNegativeButton(getString(R.string.running_activity_dialog_cancel_button)) { dialog, _ ->
                dialog.cancel()
            }.create()
    }

    override fun onDetach() {
        callback = null

        super.onDetach()
    }

    interface AreYouWantToRunningYetContract {
        fun tryToRunningAgain()
    }
}