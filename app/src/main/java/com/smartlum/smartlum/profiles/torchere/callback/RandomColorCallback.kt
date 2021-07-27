package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface RandomColorCallback {
    void onRandomColorState(@NonNull final BluetoothDevice device, final boolean state);
}
