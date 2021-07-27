package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice

interface FirmwareVersionCallback {
    fun onFirmwareVersionReceived(device: BluetoothDevice, version: Int)
}