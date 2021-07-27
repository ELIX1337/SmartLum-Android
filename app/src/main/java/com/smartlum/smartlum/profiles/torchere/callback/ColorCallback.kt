package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface ColorCallback {
    void onColorReceived(@NonNull final BluetoothDevice device, final int color);
}
