package com.github.rtyvz.senla.tr.runningtracker.ui.base

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

abstract class BaseActivity<PRESENTER : BasePresenter<BaseView>> : AppCompatActivity() {

    protected var presenter: PRESENTER? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        presenter = createPresenter()
    }

    abstract fun createPresenter(): PRESENTER

    override fun onDestroy() {
        presenter = null
        super.onDestroy()
    }
}