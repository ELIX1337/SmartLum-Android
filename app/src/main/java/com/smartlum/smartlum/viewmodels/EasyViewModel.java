package com.smartlum.smartlum.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;

import com.smartlum.smartlum.adapter.DeviceError;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.profiles.easy.EasyManager;
import com.smartlum.smartlum.profiles.easy.data.EasyData;
import com.smartlum.smartlum.utils.Sratocol;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;

/**
 * ViewModel устройства SL-EASY.
 * Подключаешь ее на необходимые Activity или Fragment
 * подписываешься на данные через методы get()
 * записываешь данные через методы set()
 */

public class EasyViewModel extends AndroidViewModel {

    private final EasyManager easyManager;
    private BluetoothDevice device;

    private int dayNightMode;
    private int randomColorMode;

    private final MutableLiveData<Bundle> lastConfig = new MutableLiveData<>();

    public EasyViewModel(@NonNull Application application) {
        super(application);
        easyManager = new EasyManager(getApplication());
    }

    public LiveData<ConnectionState> getConnectionState() {
        return easyManager.getState();
    }

    public LiveData<Integer> getConfigStateFlag() {
        return easyManager.getConfigStateFlag();
    }

    public LiveData<Boolean> getSettingsReadStatus() {
        return easyManager.getSettingsReadStatus();
    }

    public LiveData<DeviceError> getErrors() { return easyManager.getErrors(); }

    public LiveData<Integer> getColor() {
        return easyManager.getColor();
    }

    public LiveData<Integer> getAnimationMode() {
        return easyManager.getAnimationMode();
    }

    public LiveData<Integer> getAnimationOnSpeed() {
        return easyManager.getAnimationOnSpeed();
    }

    public LiveData<Integer> getAnimationOffSpeed() {
        return easyManager.getAnimationOffSpeed();
    }

    public LiveData<Integer> getRandomColorMode() {
        return easyManager.getRandomColorMode();
    }

    public LiveData<Boolean> getAdaptiveBrightnessMode() {
        return easyManager.getAdaptiveBrightnessMode();
    }

    public LiveData<Integer> getDayNightMode() {
        return easyManager.getDayNightMode();
    }

    public LiveData<Integer> getDayNightModeOnTime() {
        return easyManager.getDayNightModeOnTime();
    }

    public LiveData<Integer> getDayNightModeOffTime() {
        return easyManager.getDayNightModeOffTime();
    }

    public LiveData<Integer> getStripType() {
        return easyManager.getStripType();
    }

    public LiveData<Integer> getStepsCount() {
        return easyManager.getStepsCount();
    }

    public LiveData<Integer> getBotSensorCurrentDistance() {
        return easyManager.getBotSensorCurrentDistance();
    }

    public LiveData<Integer> getTopSensorCurrentDistance() {
        return easyManager.getTopSensorCurrentDistance();
    }

    public LiveData<Integer> getBotSensorTriggerDistance() {
        return easyManager.getBotSensorTriggerDistance();
    }

    public LiveData<Integer> getTopSensorTriggerDistance() {
        return easyManager.getTopSensorTriggerDistance();
    }

    public LiveData<Integer> getSensorLocation(int sensor) {
        if (sensor == Sratocol.Register.TOP_SENS_DIRECTION)
            return easyManager.getTopSensorLocation();
        else if (sensor == Sratocol.Register.BOT_SENS_DIRECTION)
            return easyManager.getBotSensorLocation();
        else return null;
    }

    public LiveData<Bundle> getLastConfig() {
        return lastConfig;
    }

    public LiveData<Integer> getBotSensCurrentLightness() {
        return easyManager.getBotSensCurrentLightness();
    }

    public void connect(@NonNull final DiscoveredBluetoothDevice target) {
        if (device == null) {
            device = target.getDevice();
            reconnect();
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    public void reconnect() {
        if (device != null) {
            easyManager.connect(device)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .enqueue();
        }
    }

    public void disconnect() {
        device = null;
        easyManager.disconnect().enqueue();
    }

    public void requestSettings() {
        easyManager.writeSettingsRequestData();
    }

    public void setColor(int color) {
        easyManager.writeColorData(color);
    }

    public void setAnimationMode(int mode) {
        easyManager.writeAnimationModeData(mode);
    }

    public void setAnimationOnSpeed(int speed) {
        easyManager.writeAnimationOnSpeedData(speed);
    }

    public void setAnimationOffSpeed(int speed) {
        easyManager.writeAnimationOffSpeedData(speed);
    }

    public void activateRandomColor(boolean isActive) {
        easyManager.writeRandomColorModeData((isActive ? randomColorMode : EasyData.RANDOM_COLOR_MODE_OFF));
    }

    public void setRandomColorMode(int mode) {
        randomColorMode = mode;
        easyManager.writeRandomColorModeData(mode);
    }

    public void setAdaptiveMode(boolean isOn) {
        easyManager.writeAdaptiveBrightnessModeData((isOn ? EasyData.ADAPTIVE_BRIGHTNESS_MODE_ON : EasyData.ADAPTIVE_BRIGHTNESS_MODE_OFF));
    }

    public void activateDayNightMode(boolean isActive) {
        easyManager.writeDayNightModeData((isActive ? dayNightMode : EasyData.DAY_NIGHT_MODE_OFF));
    }

    public void setDayNightMode(int mode) {
        dayNightMode = mode;
        easyManager.writeDayNightModeData(mode);
    }

    public void setDayNightModeOnTime(int timeFrom) {
        easyManager.writeDayNightModeOnTime(timeFrom);
    }

    public void setDayNightModeOffTime(int timeTo) {
        easyManager.writeDayNightModeOffTime(timeTo);
    }

    public void setStripType(int type) {
        easyManager.writeStripTypeData(type);
    }

    public void setLastConfig(Bundle bundle) {
        lastConfig.postValue(bundle);
    }

    public void setSensorLocation(int sensor, int location) {
        if (sensor == Sratocol.Register.TOP_SENS_DIRECTION)
            easyManager.writeTopSensorLocation(location);
        else if (sensor == Sratocol.Register.BOT_SENS_DIRECTION)
            easyManager.writeBotSensorLocation(location);
    }

    public void setSensorDistance(int sensor, int distance) {
        if (sensor == Sratocol.Register.TOP_SENS_TRIGGER_DISTANCE)
            easyManager.writeTopSensorDistance(distance);
        else if (sensor == Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE)
            easyManager.writeBotSensorDistance(distance);
    }

    public void setFactorySettings() {
        easyManager.writeFactorySettings();
    }

    public void writeData(byte register, int value) {
        easyManager.writeData(register, value);
    }

    public void readData(byte register) {
        easyManager.readData(register);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (easyManager.isConnected()) {
            disconnect();
        }
    }
}
