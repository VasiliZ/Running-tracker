package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment<PRESENTER : BasePresenter<BaseView>> :
    Fragment() {

    protected lateinit var presenter: PRESENTER

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = createPresenter()
        presenter.onCreate()
    }

    override fun onDestroyView() {
        presenter.detach()
        super.onDestroyView()
    }

    abstract fun createPresenter(): PRESENTER
}
