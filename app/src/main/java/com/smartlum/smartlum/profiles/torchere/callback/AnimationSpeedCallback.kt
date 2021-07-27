package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface AnimationSpeedCallback {
    fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int)
}