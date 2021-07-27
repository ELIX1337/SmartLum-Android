package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface FirmwareVersionCallback {
    void onFirmwareVersionReceived(@NonNull final BluetoothDevice device, final int version);
}
