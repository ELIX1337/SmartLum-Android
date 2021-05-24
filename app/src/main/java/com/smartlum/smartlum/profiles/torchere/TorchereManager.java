package com.smartlum.smartlum.profiles.torchere;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartlum.smartlum.profiles.torchere.callback.AnimationDirectionDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.AnimationModeDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.AnimationSpeedDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.AnimationStepDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.ColorDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.FirmwareVersionDataCallback;
import com.smartlum.smartlum.profiles.torchere.callback.RandomColorDataCallback;
import com.smartlum.smartlum.profiles.torchere.data.TorchereData;

import java.util.Arrays;
import java.util.UUID;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;

public class TorchereManager extends ObservableBleManager {
    private static final String TAG = "EasyManager";

    /** Torchere Device Services UUID's. */
    public final static UUID TORCHERE_SERVICE_UUID    = UUID.fromString("BB930001-3CE1-4720-A753-28C0159DC777");
    public final static UUID DEVICE_INFO_SERVICE_UUID = UUID.fromString("BB93FFFF-3CE1-4720-A753-28C0159DC777");
    public final static UUID COLOR_SERVICE_UUID       = UUID.fromString("BB930B00-3CE1-4720-A753-28C0159DC777");
    public final static UUID ANIMATION_SERVICE_UUID   = UUID.fromString("BB930A00-3CE1-4720-A753-28C0159DC777");
    /** Device Info characteristic UUID's. */
    private final static UUID DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID = UUID.fromString("BB93FFFE-3CE1-4720-A753-28C0159DC777");
    private final static UUID DEVICE_DFU_CHARACTERISTIC_UUID              = UUID.fromString("BB93FFFD-3CE1-4720-A753-28C0159DC777");
    /** Color characteristic UUID's. */
    private final static UUID COLOR_PRIMARY_CHARACTERISTIC_UUID           = UUID.fromString("BB930B01-3CE1-4720-A753-28C0159DC777");
    private final static UUID COLOR_SECONDARY_CHARACTERISTIC_UUID         = UUID.fromString("BB930B02-3CE1-4720-A753-28C0159DC777");
    private final static UUID COLOR_RANDOM_CHARACTERISTIC_UUID            = UUID.fromString("BB930B03-3CE1-4720-A753-28C0159DC777");
    /** Animation characteristic UUID's. */
    private final static UUID ANIMATION_MODE_CHARACTERISTIC_UUID          = UUID.fromString("BB930A01-3CE1-4720-A753-28C0159DC777");
    private final static UUID ANIMATION_ON_SPEED_CHARACTERISTIC_UUID      = UUID.fromString("BB930A02-3CE1-4720-A753-28C0159DC777");
    private final static UUID ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID     = UUID.fromString("BB930A03-3CE1-4720-A753-28C0159DC777");
    private final static UUID ANIMATION_DIRECTION_CHARACTERISTIC_UUID     = UUID.fromString("BB930A04-3CE1-4720-A753-28C0159DC777");
    private final static UUID ANIMATION_STEP_CHARACTERISTIC_UUID          = UUID.fromString("BB930A05-3CE1-4720-A753-28C0159DC777");

    private BluetoothGattCharacteristic
            firmwareVersionCharacteristic,
            dfuCharacteristic,
            colorPrimaryCharacteristic,
            colorSecondaryCharacteristic,
            colorRandomCharacteristic,
            animationModeCharacteristic,
            animationOnSpeedCharacteristic,
            animationOffSpeedCharacteristic,
            animationDirectionCharacteristic,
            animationStepCharacteristic;
    private boolean supported;

    private final MutableLiveData<Integer> firmwareVersion    = new MutableLiveData<>();
    private final MutableLiveData<Boolean> dfuState           = new MutableLiveData<>();
    private final MutableLiveData<Integer> primaryColor       = new MutableLiveData<>();
    private final MutableLiveData<Integer> secondaryColor     = new MutableLiveData<>();
    private final MutableLiveData<Boolean> randomColor        = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationMode      = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationOnSpeed   = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationOffSpeed  = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationDirection = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationStep      = new MutableLiveData<>();

    public LiveData<Integer> getFirmwareVersion() {
        return firmwareVersion;
    }

    public LiveData<Boolean> getDfuState() {
        return dfuState;
    }

    public LiveData<Integer> getPrimaryColor() {
        return primaryColor;
    }

    public LiveData<Integer> getSecondaryColor() {
        return secondaryColor;
    }

    public LiveData<Boolean> getRandomColor() {
        return randomColor;
    }

    public LiveData<Integer> getAnimationMode() {
        return animationMode;
    }

    public LiveData<Integer> getAnimationOnSpeed() {
        return animationOnSpeed;
    }

    public LiveData<Integer> getAnimationOffSpeed() {
        return animationOffSpeed;
    }

    public LiveData<Integer> getAnimationDirection() {
        return animationDirection;
    }

    public LiveData<Integer> getAnimationStep() {
        return animationStep;
    }

    public TorchereManager(@NonNull final Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new TorchereManagerGattCallback();
    }

    private void getDeviceInfoCharacteristics(BluetoothGattService service) {
        firmwareVersionCharacteristic = service.getCharacteristic(DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID);
        dfuCharacteristic             = service.getCharacteristic(DEVICE_DFU_CHARACTERISTIC_UUID);
    }

    private void getColorCharacteristics(BluetoothGattService service) {
        colorPrimaryCharacteristic   = service.getCharacteristic(COLOR_PRIMARY_CHARACTERISTIC_UUID);
        colorSecondaryCharacteristic = service.getCharacteristic(COLOR_SECONDARY_CHARACTERISTIC_UUID);
        colorRandomCharacteristic    = service.getCharacteristic(COLOR_RANDOM_CHARACTERISTIC_UUID);
    }

    private void getAnimationCharacteristics(BluetoothGattService service) {
        animationModeCharacteristic      = service.getCharacteristic(ANIMATION_MODE_CHARACTERISTIC_UUID);
        animationOnSpeedCharacteristic   = service.getCharacteristic(ANIMATION_ON_SPEED_CHARACTERISTIC_UUID);
        animationOffSpeedCharacteristic  = service.getCharacteristic(ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID);
        animationDirectionCharacteristic = service.getCharacteristic(ANIMATION_DIRECTION_CHARACTERISTIC_UUID);
        animationStepCharacteristic      = service.getCharacteristic(ANIMATION_STEP_CHARACTERISTIC_UUID);
    }

    /**
     * Color Characteristic's Callback.
     */
    private	final ColorDataCallback primaryColorCallback = new ColorDataCallback() {
        @Override
        public void onColorReceived(@NonNull BluetoothDevice device, int color) {
            Log.d(TAG, "onPrimaryColorReceived: " + Color.red(color) + "." + Color.green(color) + "." + Color.blue(color));
            primaryColor.postValue(color);
        }
    };

    private	final ColorDataCallback secondaryColorCallback = new ColorDataCallback() {
        @Override
        public void onColorReceived(@NonNull BluetoothDevice device, int color) {
            Log.d(TAG, "onSecondaryColorReceived: " + Color.red(color) + "." + Color.green(color) + "." + Color.blue(color));
            secondaryColor.postValue(color);
        }
    };

    private final RandomColorDataCallback randomColorCallback = new RandomColorDataCallback() {
        @Override
        public void onRandomColorState(@NonNull BluetoothDevice device, boolean state) {
            Log.d(TAG, "onRandomColorState: " + state);
            randomColor.postValue(state);
        }
    };

    /**
     * Animation Characteristic's Callback.
     */
    private final AnimationModeDataCallback animationModeCallback = new AnimationModeDataCallback() {
        @Override
        public void onAnimationModeReceived(@NonNull BluetoothDevice device, int mode) {
            Log.d(TAG, "onAnimationModeReceived: " + mode);
            animationMode.postValue(mode);
        }
    };

    private final AnimationSpeedDataCallback animationOnSpeedCallback = new AnimationSpeedDataCallback() {
        @Override
        public void onAnimationSpeedReceived(@NonNull BluetoothDevice device, int speed) {
            Log.d(TAG, "onAnimationOnStepReceived: " + speed);
            animationOnSpeed.postValue(speed);
        }
    };

    private final AnimationSpeedDataCallback animationOffSpeedCallback = new AnimationSpeedDataCallback() {
        @Override
        public void onAnimationSpeedReceived(@NonNull BluetoothDevice device, int speed) {
            Log.d(TAG, "onAnimationOffStepReceived: " + speed);
            animationOffSpeed.postValue(speed);
        }
    };

    private final AnimationDirectionDataCallback animationDirectionCallback = new AnimationDirectionDataCallback() {
        @Override
        public void onAnimationDirectionReceived(@NonNull BluetoothDevice device, int direction) {
            Log.d(TAG, "onAnimationDirectionReceived: " + direction);
            animationDirection.postValue(direction);
        }
    };

    private final AnimationStepDataCallback animationStepCallback = new AnimationStepDataCallback() {
        @Override
        public void onAnimationStepReceived(@NonNull BluetoothDevice device, int step) {
            Log.d(TAG, "onAnimationStepReceived: " + step);
            animationStep.postValue(step);
        }
    };

    /**
     * Device Info Characteristic's Callback.
     */
    private final FirmwareVersionDataCallback firmwareVersionCallback = new FirmwareVersionDataCallback() {
        @Override
        public void onFirmwareVersionReceived(@NonNull BluetoothDevice device, int version) {
            Log.d(TAG, "onFirmwareVersionReceived: " + version);
            firmwareVersion.postValue(version);
        }
    };

    /**
     * BluetoothGatt callback object
     */
    private class TorchereManagerGattCallback extends BleManagerGattCallback {
        @Override
        protected void initialize() {
            readCharacteristic(firmwareVersionCharacteristic).with(firmwareVersionCallback).enqueue();
            readCharacteristic(colorPrimaryCharacteristic).with(primaryColorCallback).enqueue();
            readCharacteristic(colorSecondaryCharacteristic).with(secondaryColorCallback).enqueue();
            readCharacteristic(colorRandomCharacteristic).with(randomColorCallback).enqueue();
            readCharacteristic(animationModeCharacteristic).with(animationModeCallback).enqueue();
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback).enqueue();
            readCharacteristic(animationOffSpeedCharacteristic).with(animationOffSpeedCallback).enqueue();
            readCharacteristic(animationDirectionCharacteristic).with(animationDirectionCallback).enqueue();
            readCharacteristic(animationStepCharacteristic).with(animationStepCallback).enqueue();
            enableNotifications(dfuCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService deviceInfoService = gatt.getService(DEVICE_INFO_SERVICE_UUID);
            final BluetoothGattService colorService      = gatt.getService(COLOR_SERVICE_UUID);
            final BluetoothGattService animationService  = gatt.getService(ANIMATION_SERVICE_UUID);

            if (deviceInfoService != null) {
                getDeviceInfoCharacteristics(deviceInfoService);
            } if (colorService != null) {
                getColorCharacteristics(colorService);
            } if (animationService != null) {
                getAnimationCharacteristics(animationService);
            }
            for (BluetoothGattService service : gatt.getServices()) {
                for (BluetoothGattCharacteristic characteristic : service.getCharacteristics()) {
                    readCharacteristic(characteristic);
                }
            }
            return deviceInfoService != null && colorService != null && animationService != null;
        }

        @Override
        protected void onDeviceDisconnected() {
            firmwareVersionCharacteristic    = null;
            dfuCharacteristic                = null;
            colorPrimaryCharacteristic       = null;
            colorSecondaryCharacteristic     = null;
            colorRandomCharacteristic        = null;
            animationModeCharacteristic      = null;
            animationOnSpeedCharacteristic   = null;
            animationOffSpeedCharacteristic  = null;
            animationDirectionCharacteristic = null;
            animationStepCharacteristic      = null;
        }
    }

    public void writePrimaryColor(final int color) {
        // Are we connected?
        if (colorPrimaryCharacteristic == null) {
            Log.d(TAG, "colorPrimaryCharacteristic is null");
            return;
        }
        final byte[] data = new byte[] {(byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)};
        Log.d(TAG, "writePrimaryColor: " + Arrays.toString(data));
        writeCharacteristic(colorPrimaryCharacteristic, data).enqueue();
    }

    public void writeSecondaryColor(final int color) {
        // Are we connected?
        if (colorPrimaryCharacteristic == null)
            return;
        final byte[] data = new byte[] {(byte) Color.red(color), (byte) Color.green(color), (byte) Color.blue(color)};
        Log.d(TAG, "writeSecondaryColor: " + Arrays.toString(data));
        writeCharacteristic(colorSecondaryCharacteristic, data).enqueue();
    }

    public void writeRandomColor(final boolean state) {
        if (colorRandomCharacteristic == null)
            return;
        writeCharacteristic(colorRandomCharacteristic, state ? TorchereData.writeTrue() : TorchereData.writeFalse()).enqueue();
    }

    public void writeAnimationMode(final int mode) {
        if (animationModeCharacteristic == null)
            return;

        writeCharacteristic(animationModeCharacteristic, Data.opCode((byte) mode)).enqueue();
    }

    public void writeAnimationOnSpeed(final int speed) {
        if (animationOnSpeedCharacteristic == null)
            return;
        writeCharacteristic(animationOnSpeedCharacteristic,  Data.opCode((byte) speed)).enqueue();
    }

    public void writeAnimationOffSpeed(final int speed) {
        if (animationOffSpeedCharacteristic == null)
            return;
        writeCharacteristic(animationOffSpeedCharacteristic,  Data.opCode((byte) speed)).enqueue();
    }

    public void writeAnimationDirection(final int direction) {
        if (animationDirectionCharacteristic == null)
            return;
        writeCharacteristic(animationDirectionCharacteristic,  Data.opCode((byte) direction)).enqueue();
    }

    public void writeAnimationStep(final int step) {
        if (animationStepCharacteristic == null)
            return;
        writeCharacteristic(animationStepCharacteristic,  Data.opCode((byte) step)).enqueue();
    }

}