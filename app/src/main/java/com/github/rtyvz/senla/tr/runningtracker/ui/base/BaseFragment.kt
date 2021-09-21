package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import androidx.fragment.app.Fragment


abstract class BaseFragment<Presenter : MainContract.Presenter<View>, View : MainContract.View> : Fragment() {
    private var presenter: Presenter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        presenter = createPresenter()
    }

    override fun onViewCreated(view: android.view.View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        presenter?.attachView(getMvpView())
    }

    override fun onDestroyView() {
        presenter?.detach()
        super.onDestroyView()
    }

    override fun onDestroy() {
        this.presenter = null
        super.onDestroy()
    }

    abstract fun createPresenter(): Presenter

    @Suppress("UNCHECKED_CAST")
    open fun getMvpView(): View {
        return this as? View ?: throw IllegalArgumentException("Can't cast to view interface")
    }

    protected fun getPresenter(): Presenter {
        return presenter ?: throw IllegalArgumentException("Presenter is not created")
    }
}
