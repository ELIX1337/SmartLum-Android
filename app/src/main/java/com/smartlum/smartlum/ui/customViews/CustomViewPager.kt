package com.smartlum.smartlum.ui.customViews

import android.content.Context
import android.util.AttributeSet
import android.view.MotionEvent
import androidx.viewpager.widget.ViewPager

class CustomViewPager : ViewPager {
    private var swipeable = true

    constructor(context: Context?) : super(context!!) {}
    constructor(context: Context?, attrs: AttributeSet?) : super(
        context!!, attrs
    ) {
    }

    // Call this method in your motion events when you want to disable or enable
    // It should work as desired.
    fun setSwipeable(swipeable: Boolean) {
        this.swipeable = swipeable
    }

    override fun onInterceptTouchEvent(arg0: MotionEvent): Boolean {
        return swipeable && super.onInterceptTouchEvent(arg0)
    }

    override fun onTouchEvent(event: MotionEvent): Boolean {
        return swipeable && super.onTouchEvent(event)
    }
}