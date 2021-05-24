package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface AnimationSpeedCallback {
    void onAnimationSpeedReceived(@NonNull final BluetoothDevice device, final int speed);
}
