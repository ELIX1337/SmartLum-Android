package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class AnimationSpeedDataCallback implements ProfileDataCallback, AnimationSpeedCallback {
    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }
        final int value = data.getIntValue(Data.FORMAT_UINT8, 0);
        onAnimationSpeedReceived(device, value);
    }
}
