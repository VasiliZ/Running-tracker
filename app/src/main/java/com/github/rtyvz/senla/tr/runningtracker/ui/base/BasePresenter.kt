package com.github.rtyvz.senla.tr.runningtracker.ui.base

abstract class BasePresenter<VIEW : BaseView>(private var view: VIEW?) {

    fun detach() {
        view = null
    }

    protected fun isViewAttached(): Boolean {
        return view != null
    }
}