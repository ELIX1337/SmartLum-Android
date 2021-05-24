package com.smartlum.smartlum.profiles.torchere.callback;

import android.bluetooth.BluetoothDevice;
import android.graphics.Color;

import androidx.annotation.NonNull;

import no.nordicsemi.android.ble.callback.profile.ProfileDataCallback;
import no.nordicsemi.android.ble.data.Data;
@SuppressWarnings("ConstantConditions")
public abstract class ColorDataCallback implements ProfileDataCallback, ColorCallback {
    @Override
    public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
        if (data.size() != 3) {
            onInvalidDataReceived(device, data);
        } else {
            final byte[] array = data.getValue();
            final int red   = data.getIntValue(Data.FORMAT_UINT8, 0);
            final int green = data.getIntValue(Data.FORMAT_UINT8, 1);
            final int blue  = data.getIntValue(Data.FORMAT_UINT8, 2);
            final int color = Color.rgb(red,green,blue);
            onColorReceived(device, color);
        }
    }
}
