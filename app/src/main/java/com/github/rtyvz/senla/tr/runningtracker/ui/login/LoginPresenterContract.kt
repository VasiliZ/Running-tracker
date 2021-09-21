package com.github.rtyvz.senla.tr.runningtracker.ui.login

import com.github.rtyvz.senla.tr.runningtracker.ui.base.MainContract

interface LoginPresenterContract {
    interface ViewLogin:MainContract.View{

    }

    interface PresenterLogin:MainContract.Presenter<ViewLogin>{
        fun getToken()
    }
}