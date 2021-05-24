package com.smartlum.smartlum.profiles.torchere.data;

import androidx.annotation.NonNull;

import java.util.HashMap;

import no.nordicsemi.android.ble.data.Data;

public class TorchereData {
    public static HashMap<Integer, String> animationModes;
    static {
        animationModes = new HashMap<>();
        animationModes.put(1, "Tetris");
        animationModes.put(2, "Wave");
        animationModes.put(3, "Transfusion");
        animationModes.put(4, "Full Rainbow");
        animationModes.put(5, "Rainbow");
        animationModes.put(6, "Static");
    }

    public static HashMap<Integer, String> animationDirections;
    static {
        animationDirections = new HashMap<>();
        animationDirections.put(1, "From Bottom");
        animationDirections.put(2, "From Top");
        animationDirections.put(3, "To Center");
        animationDirections.put(4, "From Center");
    }

    private static final byte STATE_OFF = 0x00;
    private static final byte STATE_ON = 0x01;

    @NonNull
    public static Data writeTrue() {
        return Data.opCode(STATE_ON);
    }

    @NonNull
    public static Data writeFalse() {
        return Data.opCode(STATE_OFF);
    }
}
