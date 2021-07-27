package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface RandomColorCallback {
    fun onRandomColorState(device: BluetoothDevice, state: Boolean)
}