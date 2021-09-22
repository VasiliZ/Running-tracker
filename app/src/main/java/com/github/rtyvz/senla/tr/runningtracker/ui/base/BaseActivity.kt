package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<Presenter : MainContract.Presenter<View>, View : MainContract.View> :
    AppCompatActivity() {

    private var presenter: Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
        presenter?.attachView(getMvpView())
    }

    abstract fun createPresenter(): Presenter

    protected fun getPresenter(): Presenter {
        return presenter ?: error("Presenter is not created")
    }

    @Suppress("UNCHECKED_CAST")
    open fun getMvpView(): View {
        return this as? View ?: error("Cant cast to view interface")
    }

    override fun onDestroy() {
        presenter = null
        super.onDestroy()
    }
}