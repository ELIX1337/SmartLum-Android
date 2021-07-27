package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice
import android.graphics.Color
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

abstract class ColorDataCallback : ProfileDataCallback, ColorCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 3) {
            onInvalidDataReceived(device, data)
        } else {
            data.value
            val red = data.getIntValue(Data.FORMAT_UINT8, 0)!!
            val green = data.getIntValue(Data.FORMAT_UINT8, 1)!!
            val blue = data.getIntValue(Data.FORMAT_UINT8, 2)!!
            val color = Color.rgb(red, green, blue)
            onColorReceived(device, color)
        }
    }
}