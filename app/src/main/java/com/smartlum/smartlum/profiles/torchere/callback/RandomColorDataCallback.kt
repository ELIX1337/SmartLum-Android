package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

abstract class RandomColorDataCallback : ProfileDataCallback, RandomColorCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val state = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        if (state == STATE_ENABLED) {
            onRandomColorState(device, true)
        } else if (state == STATE_DISABLED) {
            onRandomColorState(device, false)
        } else {
            onInvalidDataReceived(device, data)
        }
    }

    companion object {
        private const val STATE_DISABLED = 0x00
        private const val STATE_ENABLED = 0x01
    }
}