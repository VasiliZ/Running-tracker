package com.github.rtyvz.senla.tr.runningtracker.ui.splash

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.github.rtyvz.senla.tr.runningtracker.R
import com.github.rtyvz.senla.tr.runningtracker.extension.getRunningSharedPreference
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.github.rtyvz.senla.tr.runningtracker.ui.main.MainActivity
import com.google.android.material.imageview.ShapeableImageView

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private const val USER_TOKEN = "USER_TOKEN"
        private const val EMPTY_STRING = ""
        private const val DELAY_DURATION = 3000L
    }

    private var appLogoImageView: ShapeableImageView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        initViews()
        rotateLogo()
        openNextActivityWithDelay()
    }

    private fun initViews() {
        appLogoImageView = findViewById(R.id.appLogoImageView)
    }

    private fun rotateLogo() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        appLogoImageView?.startAnimation(rotateAnimation)
    }

    private fun openNextActivityWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            routeToAppropriateActivity()
            finish()
        }, DELAY_DURATION)
    }

    private fun routeToAppropriateActivity() {
        when (this.getRunningSharedPreference().getString(USER_TOKEN, EMPTY_STRING)) {
            EMPTY_STRING -> startActivity(Intent(this, LoginActivity::class.java))
            else -> startActivity(Intent(this, MainActivity::class.java))
        }
    }
}