package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment

abstract class BaseFragment<PRESENTER : BasePresenter<VIEW>, VIEW : BaseView> :
    Fragment() {

    protected var presenter: PRESENTER? = null
        get() = field ?: error("PRESENTER must be created")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = createPresenter()
    }

    override fun onDestroyView() {
        presenter?.detach()
        super.onDestroyView()
    }

    override fun onDestroy() {
        this.presenter = null
        super.onDestroy()
    }

    abstract fun createPresenter(): PRESENTER

    @Suppress("UNCHECKED_CAST")
    open fun getMvpView(): VIEW {
        return this as? VIEW ?: error("Can't cast to view interface")
    }
}
