package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface AnimationModeCallback {
    fun onAnimationModeReceived(device: BluetoothDevice, mode: Int)
}