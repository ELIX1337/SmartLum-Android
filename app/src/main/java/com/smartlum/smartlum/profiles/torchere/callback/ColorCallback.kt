package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface ColorCallback {
    fun onColorReceived(device: BluetoothDevice, color: Int)
}