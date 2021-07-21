package com.github.rtyvz.senla.tr.runningtracker

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import com.github.rtyvz.senla.tr.runningtracker.extension.setTextGradient
import com.github.rtyvz.senla.tr.runningtracker.ui.login.LoginActivity
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.textview.MaterialTextView

class SplashScreenActivity : AppCompatActivity() {

    companion object {
        private const val DELAY_DURATION = 3000L
    }

    private lateinit var appLogoImageView: ShapeableImageView
    private lateinit var appNameTextView: MaterialTextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViews()
        setTextGradient()
        rotateLogo()
        openNextActivityWithDelay()
    }

    private fun findViews() {
        appLogoImageView = findViewById(R.id.appLogoImageView)
        appNameTextView = findViewById(R.id.appNameTextView)
    }

    private fun setTextGradient() {
        val colorList = resources.getIntArray(R.array.text_gradient_color)
        appNameTextView.setTextGradient(colorList)
    }

    private fun rotateLogo() {
        val rotateAnimation = AnimationUtils.loadAnimation(this, R.anim.rotate)
        appLogoImageView.startAnimation(rotateAnimation)
    }

    private fun openNextActivityWithDelay() {
        Handler(Looper.getMainLooper()).postDelayed({
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }, DELAY_DURATION)
    }
}