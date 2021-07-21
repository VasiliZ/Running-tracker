package com.github.rtyvz.senla.tr.runningtracker.extension

import android.graphics.LinearGradient
import android.graphics.Shader
import com.google.android.material.textview.MaterialTextView

fun MaterialTextView.setTextGradient(colorList: IntArray) {
    val paint = this.paint
    val width = paint.measureText(this.text.toString())
    val textShader =
        LinearGradient(
            0f, 0f, width, this.textSize,
            colorList, null, Shader.TileMode.REPEAT
        )
    this.paint.shader = textShader
}