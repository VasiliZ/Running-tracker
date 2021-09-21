package com.github.rtyvz.senla.tr.runningtracker.ui.base

interface MainContract {
    interface View {
        fun showLoading()
        fun hideLoading()
    }

    interface Presenter<View : MainContract.View> {
        fun attachView(mvpView: View)
        fun detach()
    }
}