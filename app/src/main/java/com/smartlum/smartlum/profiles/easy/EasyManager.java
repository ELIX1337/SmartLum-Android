package com.smartlum.smartlum.profiles.easy;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartlum.smartlum.adapter.DeviceError;
import com.smartlum.smartlum.profiles.easy.callback.NordicBleUartTxCallback;
import com.smartlum.smartlum.utils.Sratocol;

import java.util.Arrays;
import java.util.UUID;

import no.nordicsemi.android.ble.data.Data;
import no.nordicsemi.android.ble.livedata.ObservableBleManager;

public class EasyManager extends ObservableBleManager {
    private static final String TAG = "EasyManager";

    /** Nordic UART Service UUID. */
    public final static UUID UART_UUID_SERVICE = UUID.fromString("6E400001-B5A3-F393-E0A9-E50E24DCCA9E");
    /** RX characteristic UUID. */
    private final static UUID RX_CHAR = UUID.fromString("6E400002-B5A3-F393-E0A9-E50E24DCCA9E");
    /** TX characteristic UUID. */
    private final static UUID TX_CHAR = UUID.fromString("6E400003-B5A3-F393-E0A9-E50E24DCCA9E");

    private BluetoothGattCharacteristic rxCharacteristic, txCharacteristic;
    private boolean supported;

    private final MutableLiveData<Integer> configStateFlag          = new MutableLiveData<>();
    private final MutableLiveData<Boolean> endOfSettings            = new MutableLiveData<>();
    private final MutableLiveData<DeviceError> errors               = new MutableLiveData<>();
    private final MutableLiveData<Integer> color                    = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationMode            = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationOnSpeed         = new MutableLiveData<>();
    private final MutableLiveData<Integer> animationOffSpeed        = new MutableLiveData<>();
    private final MutableLiveData<Integer> randomColorMode          = new MutableLiveData<>();
    private final MutableLiveData<Boolean> adaptiveBrightnessMode   = new MutableLiveData<>();
    private final MutableLiveData<Integer> dayNightMode             = new MutableLiveData<>();
    private final MutableLiveData<Integer> dayNightModeOnTime       = new MutableLiveData<>();
    private final MutableLiveData<Integer> dayNightModeOffTime      = new MutableLiveData<>();
    private final MutableLiveData<Integer> stripType                = new MutableLiveData<>();
    private final MutableLiveData<Integer> stepsCount               = new MutableLiveData<>();

    private final MutableLiveData<Integer> topSensorCurrentDistance = new MutableLiveData<>();
    private final MutableLiveData<Integer> botSensorCurrentDistance = new MutableLiveData<>();
    private final MutableLiveData<Integer> topSensorTriggerDistance = new MutableLiveData<>();
    private final MutableLiveData<Integer> botSensorTriggerDistance = new MutableLiveData<>();
    private final MutableLiveData<Integer> topSensorLocation = new MutableLiveData<>();
    private final MutableLiveData<Integer> botSensorLocation = new MutableLiveData<>();
    private final MutableLiveData<Integer> sensorsHardMode = new MutableLiveData<>();

    private final MutableLiveData<Integer> lightBotSens = new MutableLiveData<>();
    public LiveData<Integer> getBotSensCurrentLightness() {
        return lightBotSens;
    }

    public LiveData<Integer> getConfigStateFlag() {
        return configStateFlag;
    }

    public LiveData<Integer> getColor() {
        return color;
    }

    public LiveData<Boolean> getSettingsReadStatus() {
        return endOfSettings;
    }

    public LiveData<DeviceError> getErrors() {
        return errors;
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

    public LiveData<Integer> getRandomColorMode() {
        return randomColorMode;
    }

    public LiveData<Boolean> getAdaptiveBrightnessMode() {
        return adaptiveBrightnessMode;
    }

    public LiveData<Integer> getDayNightMode() {
        return dayNightMode;
    }

    public LiveData<Integer> getDayNightModeOnTime() {
        return dayNightModeOnTime;
    }

    public LiveData<Integer> getDayNightModeOffTime() {
        return dayNightModeOffTime;
    }

    public LiveData<Integer> getStripType() {
        return stripType;
    }

    public LiveData<Integer> getStepsCount() {
        return stepsCount;
    }

    public LiveData<Integer> getTopSensorCurrentDistance() {
        return topSensorCurrentDistance;
    }

    public LiveData<Integer> getBotSensorCurrentDistance() {
        return botSensorCurrentDistance;
    }

    public LiveData<Integer> getTopSensorTriggerDistance() {
        return topSensorTriggerDistance;
    }

    public LiveData<Integer> getBotSensorTriggerDistance() {
        return botSensorTriggerDistance;
    }

    public LiveData<Integer> getTopSensorLocation() {
        return topSensorLocation;
    }

    public LiveData<Integer> getBotSensorLocation() {
        return botSensorLocation;
    }

    public LiveData<Integer> getSensorsHardModeStatus() {
        return sensorsHardMode;
    }

    public EasyManager(@NonNull final Context context) {
        super(context);
    }

    @NonNull
    @Override
    protected BleManagerGattCallback getGattCallback() {
        return new NordicBleUartGattCallback();
    }

    /**
     * Callback TX характеристики.
     * Мы считываем ее значение, когда оно изменяется.
     * Это по факту является приемом данных с устройства.
     * Когда придут данные, сработает метод {@link NordicBleUartTxCallback#onDataReceived}
     */
    private	final NordicBleUartTxCallback txCallback = new NordicBleUartTxCallback() {
        @Override
        public void onDataReceived(@NonNull BluetoothDevice device, @NonNull Data data) {
            sratocolPacketParser(Sratocol.unpackPacket(data.getValue()));
            Log.d(TAG, "data received: " + Arrays.toString(Sratocol.unsignedToBytes(data.getValue())));
        }
    };

    /**
     * BluetoothGatt callback object
     */
    private class NordicBleUartGattCallback extends BleManagerGattCallback {
        @Override
        protected void initialize() {
            setNotificationCallback(txCharacteristic).with(txCallback);
            enableNotifications(txCharacteristic).enqueue();
        }

        @Override
        protected boolean isRequiredServiceSupported(@NonNull BluetoothGatt gatt) {
            final BluetoothGattService service = gatt.getService(UART_UUID_SERVICE);
            if (service != null) {
                rxCharacteristic = service.getCharacteristic(RX_CHAR);
                txCharacteristic = service.getCharacteristic(TX_CHAR);
            }
            boolean writeRequest = false;
            if (rxCharacteristic != null) {
                final int rxProps = rxCharacteristic.getProperties();
                writeRequest = (rxProps & BluetoothGattCharacteristic.PROPERTY_WRITE) > 0;
            }
            supported = txCharacteristic != null && rxCharacteristic != null && writeRequest;
            return supported;
        }

        @Override
        protected void onDeviceDisconnected() {
            rxCharacteristic = null;
            txCharacteristic = null;
        }
    }

    public void readData(byte register) {
        writeCharacteristic(rxCharacteristic, Sratocol.createPacket(Sratocol.Command.READ, register, 0)).enqueue();
    }

    public void writeData(byte register, int value) {
        final byte[] packet = Sratocol.createPacket(Sratocol.Command.WRITE, register, value);
        writeCharacteristic(rxCharacteristic, packet).enqueue();
        //readData(register);
        Log.d(TAG, "writing data: " + Arrays.toString(Sratocol.unsignedToBytes(packet)));
        Log.d(TAG, "writeData: register - " + register + ", value - " + value);
    }

    public void writeSettingsRequestData() {
        writeCharacteristic(rxCharacteristic, Sratocol.createPacket(Sratocol.Command.GET_SETTINGS, Sratocol.Register.SETTINGS_FLAG, 0)).enqueue();
        Log.d(TAG, "settings requested");
    }

    public void writeColorData(int color) {
        writeCharacteristic(rxCharacteristic, Sratocol.createRGBPacket(color)).enqueue();
        Log.d(TAG, "writing data: " + Arrays.toString(Sratocol.unsignedToBytes(Sratocol.createRGBPacket(color))));
    }

    public void writeAnimationModeData(int mode) {
        writeData(Sratocol.Register.ANIMATION_MODE, mode);
    }

    public void writeAnimationOnSpeedData(int speed) {
        writeData(Sratocol.Register.ANIMATION_ON_SPEED, speed);
    }

    public void writeAnimationOffSpeedData(int speed) {
        writeData(Sratocol.Register.ANIMATION_OFF_SPEED, speed);
    }

    public void writeRandomColorModeData(int mode) {
        writeData(Sratocol.Register.RANDOM_COLOR_MODE, mode);
    }

    public void writeAdaptiveBrightnessModeData(int mode) {
        writeData(Sratocol.Register.BRIGHTNESS_MODE, mode);
    }

    public void writeDayNightModeData(int mode) {
        writeData(Sratocol.Register.DAY_NIGHT_MODE, mode);
    }

    public void writeDayNightModeOnTime(int timeFrom) {
        writeData(Sratocol.Register.ALARM_A, timeFrom);
    }

    public void writeDayNightModeOffTime(int timeTo) {
        writeData(Sratocol.Register.ALARM_B, timeTo);
    }

    public void writeStripTypeData(int type) {
        writeData(Sratocol.Register.STRIP_TYPE, type);
        stripType.postValue(type);
    }

    public void writeTopSensorLocation(int location) {
        writeData(Sratocol.Register.TOP_SENS_DIRECTION, location);
    }

    public void writeBotSensorLocation(int location) {
        writeData(Sratocol.Register.BOT_SENS_DIRECTION, location);
    }

    public void writeTopSensorDistance(int distance) {
        writeData(Sratocol.Register.TOP_SENS_TRIGGER_DISTANCE, distance);
    }

    public void writeBotSensorDistance(int distance) {
        writeData(Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE, distance);
    }

    public void writeFactorySettings() {
        writeCharacteristic(rxCharacteristic, Sratocol.resetDevice()).enqueue();
        Log.d(TAG, "writing data: " + Arrays.toString(Sratocol.resetDevice()));
    }

    private void sratocolPacketParser(@Nullable int[] packet) {
        if (packet != null) {
            switch (packet[0]) {
                case Sratocol.Command.WRITE:
                    dataParser(packet[1], packet[2]);
                    break;
                case Sratocol.Command.READ:
                    // TODO обработать (но она никогда не приходит)
                    break;
                case Sratocol.Command.RGB:
                    colorParser(packet[1]);
                    break;
                case Sratocol.Command.END_OF_SETTINGS:
                    endOfSettings.postValue(true);
                    break;
                case Sratocol.Command.ERROR:
                    errorParser(packet[1], packet[2]);
                    break;
            }
        }
    }

    private void errorParser(int register, int status) {
        final int[] array = new int[] {register, status};
        errors.postValue(new DeviceError(array));
    }

    private void dataParser(int register, int value) {
        switch (register) {
            case Sratocol.Register.SETTINGS_FLAG:
                configStateFlag.postValue(value);
                Log.d(TAG, "Read settings flag: " + value);
                break;

            case Sratocol.Register.ANIMATION_MODE:
                animationMode.postValue(value);
                Log.d(TAG, "Read animation mode: " + value);
                break;

            case Sratocol.Register.RANDOM_COLOR_MODE:
                randomColorMode.postValue(value);
                Log.d(TAG, "Read random color mode: " + value);
                break;

            case Sratocol.Register.ANIMATION_ON_SPEED:
                animationOnSpeed.postValue(value);
                Log.d(TAG, "Read animation on speed: " + value);
                break;

            case Sratocol.Register.ANIMATION_OFF_SPEED:
                animationOffSpeed.postValue(value);
                Log.d(TAG, "Read animation off speed: " + value);
                break;

            case Sratocol.Register.BRIGHTNESS_MODE:
                adaptiveBrightnessMode.postValue(value == 1);
                Log.d(TAG, "Read adaptive brightness: " + value);
                break;

            case Sratocol.Register.ALARM_A:
                dayNightModeOnTime.postValue(value);
                Log.d(TAG, "Read day/night ON time: " + value);
                break;

            case Sratocol.Register.ALARM_B:
                dayNightModeOffTime.postValue(value);
                Log.d(TAG, "Read day/night OFF time: " + value);
                break;

            case Sratocol.Register.DAY_NIGHT_MODE:
                dayNightMode.postValue(value);
                Log.d(TAG, "Read day/night mode: " + value);
                break;

            case Sratocol.Register.BOT_SENS_CURRENT_LIGHTNESS:
                lightBotSens.postValue(value);
                Log.d(TAG, "Read bot sensor current lightness: " + value);
                break;

            case Sratocol.Register.TOP_SENS_CURRENT_LIGHTNESS:
                Log.d(TAG, "Read top sensor current lightness: " + value);
                break;

            case Sratocol.Register.BOT_SENS_CURRENT_DISTANCE:
                botSensorCurrentDistance.postValue(value);
                Log.d(TAG, "Read bot sensor current distance: " + value);
                break;

            case Sratocol.Register.TOP_SENS_CURRENT_DISTANCE:
                topSensorCurrentDistance.postValue(value);
                Log.d(TAG, "Read top sensor current distance: " + value);
                break;

            case Sratocol.Register.TOP_SENS_TRIGGER_DISTANCE:
                topSensorTriggerDistance.postValue(value);
                Log.d(TAG, "Read top sensor trigger distance: " + value);
                break;

            case Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE:
                botSensorTriggerDistance.postValue(value);
                Log.d(TAG, "Read bot sensor trigger distance: " + value);
                break;

            case Sratocol.Register.STEPS_COUNT:
                stepsCount.postValue(value);
                Log.d(TAG, "Read stairs count: " + value);
                break;

            case Sratocol.Register.STRIP_TYPE:
                stripType.postValue(value);
                Log.d(TAG, "Read strip type: " + value);
                break;

            case Sratocol.Register.SENS_TYPE:
                sensorsHardMode.postValue(value);
                Log.d(TAG, "Read sensors hard mode status: " + value);
                break;

            case Sratocol.Register.TOP_SENS_DIRECTION:
                topSensorLocation.postValue(value);
                Log.d(TAG, "Read top sensor location: " + value);
                break;

            case Sratocol.Register.BOT_SENS_DIRECTION:
                botSensorLocation.postValue(value);
                Log.d(TAG, "Read bot sensor location: " + value);
                break;

            default:
                Log.d(TAG, "UNKNOWN DATA: "+ "register = " + register + " ,value = " + value);
                break;
        }
    }

    private void colorParser(int value) {
        color.postValue(value);
    }

}
