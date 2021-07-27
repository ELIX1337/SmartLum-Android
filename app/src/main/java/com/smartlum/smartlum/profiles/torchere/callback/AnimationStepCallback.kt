package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface AnimationStepCallback {
    fun onAnimationStepReceived(device: BluetoothDevice, step: Int)
}