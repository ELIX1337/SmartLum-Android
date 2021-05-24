package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface AnimationDirectionCallback {
    void onAnimationDirectionReceived(@NonNull final BluetoothDevice device, final int direction);
}
