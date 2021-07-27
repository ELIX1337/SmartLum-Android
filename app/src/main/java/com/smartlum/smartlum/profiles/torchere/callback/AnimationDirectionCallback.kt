package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface AnimationDirectionCallback {
    fun onAnimationDirectionReceived(device: BluetoothDevice, direction: Int)
}