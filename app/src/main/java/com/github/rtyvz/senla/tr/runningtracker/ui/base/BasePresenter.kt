package com.github.rtyvz.senla.tr.runningtracker.ui.base

import io.reactivex.rxjava3.disposables.CompositeDisposable

abstract class BasePresenter<VIEW : BaseView>(private var view: VIEW?) {

    private var disposable: CompositeDisposable? = null

    open fun onCreate() {
        disposable = CompositeDisposable()
    }

    fun detach() {
        disposable?.dispose()
        view = null
    }

    protected fun isViewAttached(): Boolean {
        return view != null
    }


}