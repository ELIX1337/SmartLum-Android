package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

public interface AnimationModeCallback {
    void onAnimationModeReceived(@NonNull final BluetoothDevice device, final int mode);
}
