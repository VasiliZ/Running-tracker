package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment

abstract class BaseFragment<PRESENTER : BasePresenter<BaseView>> :
    Fragment() {

    protected lateinit var presenter: PRESENTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = createPresenter()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        presenter.onCreate()
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    abstract fun createPresenter(): PRESENTER
}
