package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;

@SuppressWarnings("ConstantConditions")
public abstract class RandomColorDataCallback implements ProfileDataCallback, RandomColorCallback {
    private static final int STATE_DISABLED = 0x00;
    private static final int STATE_ENABLED  = 0x01;
    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        if (data.size() != 1) {
            onInvalidDataReceived(device, data);
            return;
        }

        final int state = data.getIntValue(Data.FORMAT_UINT8, 0);
        if (state == STATE_ENABLED) {
            onRandomColorState(device, true);
        } else if (state == STATE_DISABLED) {
            onRandomColorState(device, false);
        } else {
            onInvalidDataReceived(device, data);
        }
    }
}
