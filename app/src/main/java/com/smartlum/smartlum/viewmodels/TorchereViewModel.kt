package com.smartlum.smartlum.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.profiles.torchere.TorchereManager;
import no.nordicsemi.android.ble.livedata.state.ConnectionState;

/**
 * ViewModel устройства Torchere.
 * Подключаешь ее на необходимые Activity или Fragment
 * подписываешься на данные через методы get()
 * записываешь данные через методы set()
 */

public class TorchereViewModel extends AndroidViewModel {

    private static final String TAG = "TorchereViewModel:";
    private final TorchereManager torchereManager;
    private BluetoothDevice device;

    public TorchereViewModel(@NonNull Application application) {
        super(application);
        torchereManager = new TorchereManager(getApplication());
    }

    public LiveData<ConnectionState> getConnectionState() {
        return torchereManager.getState();
    }

    public LiveData<Integer> getPrimaryColor() {
        return torchereManager.getPrimaryColor();
    }

    public LiveData<Integer> getSecondaryColor() {
        return torchereManager.getSecondaryColor();
    }

    public LiveData<Boolean> getRandomColor() {
        return torchereManager.getRandomColor();
    }

    public  LiveData<Integer> getAnimationMode() {
        return torchereManager.getAnimationMode();
    }

    public LiveData<Integer> getAnimationOnSpeed() {
        return torchereManager.getAnimationOnSpeed();
    }

    public LiveData<Integer> getAnimationOffSpeed() {
        return torchereManager.getAnimationOffSpeed();
    }

    public LiveData<Integer> getAnimationDirection() {
        return torchereManager.getAnimationDirection();
    }

    public LiveData<Integer> getAnimationStep() {
        return torchereManager.getAnimationStep();
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
            torchereManager.connect(device)
                    .retry(3, 100)
                    .useAutoConnect(false)
                    .enqueue();
        }
    }

    public void disconnect() {
        device = null;
        torchereManager.disconnect().enqueue();
    }

    public void setPrimaryColor(final int color) {
        torchereManager.writePrimaryColor(color);
    }

    public void setSecondaryColor(final int color) {
        torchereManager.writeSecondaryColor(color);
    }

    public void setRandomColor(final boolean state) {
        torchereManager.writeRandomColor(state);
    }

    public void setAnimationMode(final int mode) {
        torchereManager.writeAnimationMode(mode);
    }

    public void setAnimationOnSpeed(final int speed) {
        torchereManager.writeAnimationOnSpeed(speed);
    }

    public void setAnimationOffSpeed(final int speed) {
        torchereManager.writeAnimationOffSpeed(speed);
    }

    public void setAnimationDirection(final int direction) {
        torchereManager.writeAnimationDirection(direction);
    }

    public void setAnimationStep(final int step) {
        torchereManager.writeAnimationStep(step);
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        if (torchereManager.isConnected()) {
            disconnect();
        }
    }
}
