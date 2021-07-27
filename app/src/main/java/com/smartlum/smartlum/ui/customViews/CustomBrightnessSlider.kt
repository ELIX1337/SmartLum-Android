package com.smartlum.smartlum.ui.customViews;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.slider.Slider;

public class CustomBrightnessSlider extends Slider {

    private ColorPickerView wheel;
    private int trueColor;

    public CustomBrightnessSlider(@NonNull Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        super.setValueFrom(0);
        super.setValueTo(255);
    }

    public void setColor(int color) {
        final int topColor = Math.max((Math.max(Color.red(color), Color.green(color))), Color.blue(color));
        final float multiplier = (float) ( (float) 255 / topColor);
        final int trueRed = Math.round(Color.red(color) * multiplier);
        final int trueGreen = Math.round(Color.green(color) * multiplier);
        final int trueBlue = Math.round(Color.blue(color) * multiplier);

        trueColor = getIntFromColor(trueRed, trueGreen, trueBlue);
        setValue(topColor);
    }

    public void setColorWheel(ColorPickerView wheel) {
        this.wheel = wheel;
    }

    public int getColorBrightness() {
        int red = Math.round(Color.red(trueColor) * (getValue()/getValueTo()));
        int green = Math.round(Color.green(trueColor) * (getValue()/getValueTo()));
        int blue = Math.round(Color.blue(trueColor) * (getValue()/getValueTo()));
        if (red != 0 && green !=0 && blue != 0) {
            wheel.setColor(getIntFromColor(red, green, blue), false);
        } else {
            red = Math.round(255 * (getValue()/getValueTo()));
            green = Math.round(255 * (getValue()/getValueTo()));
            blue = Math.round(255 * (getValue()/getValueTo()));
        }
        return getIntFromColor(red, green, blue);
    }

    private int getIntFromColor(int red, int green, int blue) {
        red = (red << 16) & 0x00FF0000;
        green = (green << 8) & 0x0000FF00;
        blue = blue & 0x000000FF;

        return 0xFF000000 | red | green | blue;
    }

}
