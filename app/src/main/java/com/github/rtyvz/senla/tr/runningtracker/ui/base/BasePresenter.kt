package com.github.rtyvz.senla.tr.runningtracker.ui.base

open class BasePresenter<View : MainContract.View> : MainContract.Presenter<View> {

    private var view: View? = null

    override fun attachView(mvpView: View) {
        view = mvpView
    }

    override fun detach() {
        view = null
    }

    protected fun getView(): View {
        return view ?: throw IllegalArgumentException("view is not attached")
    }

    protected fun isViewAttached(): Boolean {
        return view != null
    }
}