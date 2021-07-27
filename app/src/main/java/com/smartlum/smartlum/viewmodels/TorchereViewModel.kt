package com.smartlum.smartlum.viewmodels

import android.app.Application
import android.bluetooth.BluetoothDevice
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice
import com.smartlum.smartlum.profiles.torchere.TorchereManager
import no.nordicsemi.android.ble.livedata.state.ConnectionState

/**
 * ViewModel устройства Torchere.
 * Подключаешь ее на необходимые Activity или Fragment
 * подписываешься на данные через методы get()
 * записываешь данные через методы set()
 */
class TorchereViewModel(application: Application) : AndroidViewModel(application) {
    private val torchereManager: TorchereManager = TorchereManager(getApplication())
    private var device: BluetoothDevice? = null
    fun getConnectionState(): LiveData<ConnectionState> {
        return torchereManager.state
    }

    fun getPrimaryColor(): LiveData<Int> {
        return torchereManager.primaryColor
    }

    fun getSecondaryColor(): LiveData<Int> {
        return torchereManager.secondaryColor
    }

    fun getRandomColor(): LiveData<Boolean> {
        return torchereManager.randomColor
    }

    fun getAnimationMode(): LiveData<Int> {
        return torchereManager.animationMode
    }

    fun getAnimationOnSpeed(): LiveData<Int> {
        return torchereManager.animationOnSpeed
    }

    fun getAnimationOffSpeed(): LiveData<Int> {
        return torchereManager.animationOffSpeed
    }

    fun getAnimationDirection(): LiveData<Int> {
        return torchereManager.animationDirection
    }

    fun getAnimationStep(): LiveData<Int> {
        return torchereManager.animationStep
    }

    fun connect(target: DiscoveredBluetoothDevice) {
        if (device == null) {
            device = target.device
            reconnect()
        }
    }

    /**
     * Reconnects to previously connected device.
     * If this device was not supported, its services were cleared on disconnection, so
     * reconnection may help.
     */
    fun reconnect() {
        if (device != null) {
            torchereManager.connect(device!!)
                .retry(3, 100)
                .useAutoConnect(false)
                .enqueue()
        }
    }

    fun disconnect() {
        device = null
        torchereManager.disconnect().enqueue()
    }

    fun setPrimaryColor(color: Int) {
        torchereManager.writePrimaryColor(color)
    }

    fun setSecondaryColor(color: Int) {
        torchereManager.writeSecondaryColor(color)
    }

    fun setRandomColor(state: Boolean) {
        torchereManager.writeRandomColor(state)
    }

    fun setAnimationMode(mode: Int) {
        torchereManager.writeAnimationMode(mode)
    }

    fun setAnimationOnSpeed(speed: Int) {
        torchereManager.writeAnimationOnSpeed(speed)
    }

    fun setAnimationOffSpeed(speed: Int) {
        torchereManager.writeAnimationOffSpeed(speed)
    }

    fun setAnimationDirection(direction: Int) {
        torchereManager.writeAnimationDirection(direction)
    }

    fun setAnimationStep(step: Int) {
        torchereManager.writeAnimationStep(step)
    }

    override fun onCleared() {
        super.onCleared()
        if (torchereManager.isConnected) {
            disconnect()
        }
    }

    companion object {
        private const val TAG = "TorchereViewModel:"
    }

}