package com.smartlum.smartlum.ui.customViews

import android.content.Context
import android.graphics.Color
import android.util.AttributeSet
import com.flask.colorpicker.ColorPickerView
import com.google.android.material.slider.Slider
import kotlin.math.roundToInt

class CustomBrightnessSlider(context: Context, attrs: AttributeSet?) : Slider(context, attrs) {
    private var wheel: ColorPickerView? = null
    private var trueColor = 0
    fun setColor(color: Int) {
        val topColor =
            Color.red(color).coerceAtLeast(Color.green(color)).coerceAtLeast(Color.blue(color))
        val multiplier = (255.toFloat() / topColor)
        val trueRed = (Color.red(color) * multiplier).roundToInt()
        val trueGreen = (Color.green(color) * multiplier).roundToInt()
        val trueBlue = (Color.blue(color) * multiplier).roundToInt()
        trueColor = getIntFromColor(trueRed, trueGreen, trueBlue)
        value = topColor.toFloat()
    }

    fun setColorWheel(wheel: ColorPickerView?) {
        this.wheel = wheel
    }

    val colorBrightness: Int
        get() {
            var red = (Color.red(trueColor) * (value / valueTo)).roundToInt()
            var green = (Color.green(trueColor) * (value / valueTo)).roundToInt()
            var blue = (Color.blue(trueColor) * (value / valueTo)).roundToInt()
            if (red != 0 && green != 0 && blue != 0) {
                wheel!!.setColor(getIntFromColor(red, green, blue), false)
            } else {
                red = (255 * (value / valueTo)).roundToInt()
                green = (255 * (value / valueTo)).roundToInt()
                blue = (255 * (value / valueTo)).roundToInt()
            }
            return getIntFromColor(red, green, blue)
        }

    private fun getIntFromColor(red: Int, green: Int, blue: Int): Int {
        var mRed = red
        var mGreen = green
        var mBlue = blue
        mRed = mRed shl 16 and 0x00FF0000
        mGreen = mGreen shl 8 and 0x0000FF00
        mBlue = mBlue and 0x000000FF
        return -0x1000000 or mRed or mGreen or mBlue
    }

    init {
        super.setValueFrom(0f)
        super.setValueTo(255f)
    }
}