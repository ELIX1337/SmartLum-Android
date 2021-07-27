package com.smartlum.smartlum.profiles.torchere.callback

import android.bluetooth.BluetoothDevice
import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback
import no.nordicsemi.android.ble.data.Data

abstract class FirmwareVersionDataCallback : ProfileDataCallback, FirmwareVersionCallback {
    override fun onDataReceived(device: BluetoothDevice, data: Data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data)
            return
        }
        val value = data.getIntValue(Data.FORMAT_UINT8, 0)!!
        onFirmwareVersionReceived(device, value)
    }
}