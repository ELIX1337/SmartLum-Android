package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface AnimationStepCallback {
    void onAnimationStepReceived(@NonNull final BluetoothDevice device, final int step);
}
