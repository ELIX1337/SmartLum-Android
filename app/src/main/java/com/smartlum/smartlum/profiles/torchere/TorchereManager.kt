package com.smartlum.smartlum.profiles.torchere

import android.bluetooth.BluetoothDevice
import android.bluetooth.BluetoothGatt
import android.bluetooth.BluetoothGattCharacteristic
import android.bluetooth.BluetoothGattService
import android.content.Context
import android.graphics.Color
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.smartlum.smartlum.profiles.torchere.callback.*
import com.smartlum.smartlum.profiles.torchere.data.TorchereData.writeFalse
import com.smartlum.smartlum.profiles.torchere.data.TorchereData.writeTrue
import no.nordicsemi.android.ble.data.Data
import no.nordicsemi.android.ble.livedata.ObservableBleManager
import java.util.*

class TorchereManager(context: Context) : ObservableBleManager(context) {
    private var firmwareVersionCharacteristic: BluetoothGattCharacteristic? = null
    private var dfuCharacteristic: BluetoothGattCharacteristic? = null
    private var colorPrimaryCharacteristic: BluetoothGattCharacteristic? = null
    private var colorSecondaryCharacteristic: BluetoothGattCharacteristic? = null
    private var colorRandomCharacteristic: BluetoothGattCharacteristic? = null
    private var animationModeCharacteristic: BluetoothGattCharacteristic? = null
    private var animationOnSpeedCharacteristic: BluetoothGattCharacteristic? = null
    private var animationOffSpeedCharacteristic: BluetoothGattCharacteristic? = null
    private var animationDirectionCharacteristic: BluetoothGattCharacteristic? = null
    private var animationStepCharacteristic: BluetoothGattCharacteristic? = null
    private val supported = false
    private val firmwareVersion = MutableLiveData<Int>()
    val dfuState = MutableLiveData<Boolean>()
    val primaryColor = MutableLiveData<Int>()
    val secondaryColor = MutableLiveData<Int>()
    val randomColor = MutableLiveData<Boolean>()
    val animationMode = MutableLiveData<Int>()
    val animationOnSpeed = MutableLiveData<Int>()
    val animationOffSpeed = MutableLiveData<Int>()
    val animationDirection = MutableLiveData<Int>()
    val animationStep = MutableLiveData<Int>()
    fun getFirmwareVersion(): LiveData<Int> {
        return firmwareVersion
    }

    fun getPrimaryColor(): LiveData<Int> {
        return primaryColor
    }

    fun getSecondaryColor(): LiveData<Int> {
        return secondaryColor
    }

    fun getRandomColor(): LiveData<Boolean> {
        return randomColor
    }

    fun getAnimationMode(): LiveData<Int> {
        return animationMode
    }

    fun getAnimationOnSpeed(): LiveData<Int> {
        return animationOnSpeed
    }

    fun getAnimationOffSpeed(): LiveData<Int> {
        return animationOffSpeed
    }

    fun getAnimationDirection(): LiveData<Int> {
        return animationDirection
    }

    fun getAnimationStep(): LiveData<Int> {
        return animationStep
    }

    override fun getGattCallback(): BleManagerGattCallback {
        return TorchereManagerGattCallback()
    }

    private fun getDeviceInfoCharacteristics(service: BluetoothGattService) {
        firmwareVersionCharacteristic = service.getCharacteristic(
            DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID
        )
        dfuCharacteristic = service.getCharacteristic(DEVICE_DFU_CHARACTERISTIC_UUID)
    }

    private fun getColorCharacteristics(service: BluetoothGattService) {
        colorPrimaryCharacteristic = service.getCharacteristic(COLOR_PRIMARY_CHARACTERISTIC_UUID)
        colorSecondaryCharacteristic =
            service.getCharacteristic(COLOR_SECONDARY_CHARACTERISTIC_UUID)
        colorRandomCharacteristic = service.getCharacteristic(COLOR_RANDOM_CHARACTERISTIC_UUID)
    }

    private fun getAnimationCharacteristics(service: BluetoothGattService) {
        animationModeCharacteristic = service.getCharacteristic(ANIMATION_MODE_CHARACTERISTIC_UUID)
        animationOnSpeedCharacteristic = service.getCharacteristic(
            ANIMATION_ON_SPEED_CHARACTERISTIC_UUID
        )
        animationOffSpeedCharacteristic = service.getCharacteristic(
            ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID
        )
        animationDirectionCharacteristic = service.getCharacteristic(
            ANIMATION_DIRECTION_CHARACTERISTIC_UUID
        )
        animationStepCharacteristic = service.getCharacteristic(ANIMATION_STEP_CHARACTERISTIC_UUID)
    }

    /**
     * Color Characteristic's Callback.
     */
    private val primaryColorCallback: ColorDataCallback = object : ColorDataCallback() {
        override fun onColorReceived(device: BluetoothDevice, color: Int) {
            Log.d(
                TAG,
                "onPrimaryColorReceived: " + Color.red(color) + "." + Color.green(color) + "." + Color.blue(
                    color
                )
            )
            primaryColor.postValue(color)
        }
    }
    private val secondaryColorCallback: ColorDataCallback = object : ColorDataCallback() {
        override fun onColorReceived(device: BluetoothDevice, color: Int) {
            Log.d(
                TAG,
                "onSecondaryColorReceived: " + Color.red(color) + "." + Color.green(color) + "." + Color.blue(
                    color
                )
            )
            secondaryColor.postValue(color)
        }
    }
    private val randomColorCallback: RandomColorDataCallback = object : RandomColorDataCallback() {
        override fun onRandomColorState(device: BluetoothDevice, state: Boolean) {
            Log.d(TAG, "onRandomColorState: $state")
            randomColor.postValue(state)
        }
    }

    /**
     * Animation Characteristic's Callback.
     */
    private val animationModeCallback: AnimationModeDataCallback =
        object : AnimationModeDataCallback() {
            override fun onAnimationModeReceived(device: BluetoothDevice, mode: Int) {
                Log.d(TAG, "onAnimationModeReceived: $mode")
                animationMode.postValue(mode)
            }
        }
    private val animationOnSpeedCallback: AnimationSpeedDataCallback =
        object : AnimationSpeedDataCallback() {
            override fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int) {
                Log.d(TAG, "onAnimationOnStepReceived: $speed")
                animationOnSpeed.postValue(speed)
            }
        }
    private val animationOffSpeedCallback: AnimationSpeedDataCallback =
        object : AnimationSpeedDataCallback() {
            override fun onAnimationSpeedReceived(device: BluetoothDevice, speed: Int) {
                Log.d(TAG, "onAnimationOffStepReceived: $speed")
                animationOffSpeed.postValue(speed)
            }
        }
    private val animationDirectionCallback: AnimationDirectionDataCallback =
        object : AnimationDirectionDataCallback() {
            override fun onAnimationDirectionReceived(device: BluetoothDevice, direction: Int) {
                Log.d(TAG, "onAnimationDirectionReceived: $direction")
                animationDirection.postValue(direction)
            }
        }
    private val animationStepCallback: AnimationStepDataCallback =
        object : AnimationStepDataCallback() {
            override fun onAnimationStepReceived(device: BluetoothDevice, step: Int) {
                Log.d(TAG, "onAnimationStepReceived: $step")
                animationStep.postValue(step)
            }
        }

    /**
     * Device Info Characteristic's Callback.
     */
    private val firmwareVersionCallback: FirmwareVersionDataCallback =
        object : FirmwareVersionDataCallback() {
            override fun onFirmwareVersionReceived(device: BluetoothDevice, version: Int) {
                Log.d(TAG, "onFirmwareVersionReceived: $version")
                firmwareVersion.postValue(version)
            }
        }

    /**
     * BluetoothGatt callback object
     */
    private inner class TorchereManagerGattCallback : BleManagerGattCallback() {
        override fun initialize() {
            readCharacteristic(firmwareVersionCharacteristic).with(firmwareVersionCallback)
                .enqueue()
            readCharacteristic(colorPrimaryCharacteristic).with(primaryColorCallback).enqueue()
            readCharacteristic(colorSecondaryCharacteristic).with(secondaryColorCallback).enqueue()
            readCharacteristic(colorRandomCharacteristic).with(randomColorCallback).enqueue()
            readCharacteristic(animationModeCharacteristic).with(animationModeCallback).enqueue()
            readCharacteristic(animationOnSpeedCharacteristic).with(animationOnSpeedCallback)
                .enqueue()
            readCharacteristic(animationOffSpeedCharacteristic).with(animationOffSpeedCallback)
                .enqueue()
            readCharacteristic(animationDirectionCharacteristic).with(animationDirectionCallback)
                .enqueue()
            readCharacteristic(animationStepCharacteristic).with(animationStepCallback).enqueue()
            enableNotifications(dfuCharacteristic).enqueue()
        }

        override fun isRequiredServiceSupported(gatt: BluetoothGatt): Boolean {
            val deviceInfoService = gatt.getService(DEVICE_INFO_SERVICE_UUID)
            val colorService = gatt.getService(COLOR_SERVICE_UUID)
            val animationService = gatt.getService(ANIMATION_SERVICE_UUID)
            deviceInfoService?.let { getDeviceInfoCharacteristics(it) }
            colorService?.let { getColorCharacteristics(it) }
            animationService?.let { getAnimationCharacteristics(it) }
            for (service in gatt.services) {
                for (characteristic in service.characteristics) {
                    readCharacteristic(characteristic)
                }
            }
            return deviceInfoService != null && colorService != null && animationService != null
        }

        override fun onDeviceDisconnected() {
            firmwareVersionCharacteristic = null
            dfuCharacteristic = null
            colorPrimaryCharacteristic = null
            colorSecondaryCharacteristic = null
            colorRandomCharacteristic = null
            animationModeCharacteristic = null
            animationOnSpeedCharacteristic = null
            animationOffSpeedCharacteristic = null
            animationDirectionCharacteristic = null
            animationStepCharacteristic = null
        }
    }

    fun writePrimaryColor(color: Int) {
        // Are we connected?
        if (colorPrimaryCharacteristic == null) {
            Log.d(TAG, "colorPrimaryCharacteristic is null")
            return
        }
        val data = byteArrayOf(
            Color.red(color).toByte(), Color.green(color)
                .toByte(), Color.blue(color).toByte()
        )
        Log.d(TAG, "writePrimaryColor: " + data.contentToString())
        writeCharacteristic(colorPrimaryCharacteristic, data).enqueue()
    }

    fun writeSecondaryColor(color: Int) {
        // Are we connected?
        if (colorPrimaryCharacteristic == null) return
        val data = byteArrayOf(
            Color.red(color).toByte(), Color.green(color)
                .toByte(), Color.blue(color).toByte()
        )
        Log.d(TAG, "writeSecondaryColor: " + data.contentToString())
        writeCharacteristic(colorSecondaryCharacteristic, data).enqueue()
    }

    fun writeRandomColor(state: Boolean) {
        if (colorRandomCharacteristic == null) return
        writeCharacteristic(
            colorRandomCharacteristic,
            if (state) writeTrue() else writeFalse()
        ).enqueue()
    }

    fun writeAnimationMode(mode: Int) {
        if (animationModeCharacteristic == null) return
        writeCharacteristic(
            animationModeCharacteristic, Data.opCode(
                mode.toByte()
            )
        ).enqueue()
    }

    fun writeAnimationOnSpeed(speed: Int) {
        if (animationOnSpeedCharacteristic == null) return
        writeCharacteristic(
            animationOnSpeedCharacteristic, Data.opCode(
                speed.toByte()
            )
        ).enqueue()
    }

    fun writeAnimationOffSpeed(speed: Int) {
        if (animationOffSpeedCharacteristic == null) return
        writeCharacteristic(
            animationOffSpeedCharacteristic, Data.opCode(
                speed.toByte()
            )
        ).enqueue()
    }

    fun writeAnimationDirection(direction: Int) {
        if (animationDirectionCharacteristic == null) return
        writeCharacteristic(
            animationDirectionCharacteristic, Data.opCode(
                direction.toByte()
            )
        ).enqueue()
    }

    fun writeAnimationStep(step: Int) {
        if (animationStepCharacteristic == null) return
        writeCharacteristic(
            animationStepCharacteristic, Data.opCode(
                step.toByte()
            )
        ).enqueue()
    }

    companion object {
        private const val TAG = "EasyManager"

        /** Torchere Device Services UUID's.  */
        @JvmField
        val TORCHERE_SERVICE_UUID: UUID = UUID.fromString("BB930001-3CE1-4720-A753-28C0159DC777")
        @JvmField
        val FL_MINI_SERVICE_UUID: UUID = UUID.fromString("BB930002-3CE1-4720-A753-28C0159DC777")
        val DEVICE_INFO_SERVICE_UUID: UUID = UUID.fromString("BB93FFFF-3CE1-4720-A753-28C0159DC777")
        val COLOR_SERVICE_UUID: UUID = UUID.fromString("BB930B00-3CE1-4720-A753-28C0159DC777")
        val ANIMATION_SERVICE_UUID: UUID = UUID.fromString("BB930A00-3CE1-4720-A753-28C0159DC777")

        /** Device Info characteristic UUID's.  */
        private val DEVICE_FIRMWARE_VERSION_CHARACTERISTIC_UUID =
            UUID.fromString("BB93FFFE-3CE1-4720-A753-28C0159DC777")
        private val DEVICE_DFU_CHARACTERISTIC_UUID =
            UUID.fromString("BB93FFFD-3CE1-4720-A753-28C0159DC777")

        /** Color characteristic UUID's.  */
        private val COLOR_PRIMARY_CHARACTERISTIC_UUID =
            UUID.fromString("BB930B01-3CE1-4720-A753-28C0159DC777")
        private val COLOR_SECONDARY_CHARACTERISTIC_UUID =
            UUID.fromString("BB930B02-3CE1-4720-A753-28C0159DC777")
        private val COLOR_RANDOM_CHARACTERISTIC_UUID =
            UUID.fromString("BB930B03-3CE1-4720-A753-28C0159DC777")

        /** Animation characteristic UUID's.  */
        private val ANIMATION_MODE_CHARACTERISTIC_UUID =
            UUID.fromString("BB930A01-3CE1-4720-A753-28C0159DC777")
        private val ANIMATION_ON_SPEED_CHARACTERISTIC_UUID =
            UUID.fromString("BB930A02-3CE1-4720-A753-28C0159DC777")
        private val ANIMATION_OFF_SPEED_CHARACTERISTIC_UUID =
            UUID.fromString("BB930A03-3CE1-4720-A753-28C0159DC777")
        private val ANIMATION_DIRECTION_CHARACTERISTIC_UUID =
            UUID.fromString("BB930A04-3CE1-4720-A753-28C0159DC777")
        private val ANIMATION_STEP_CHARACTERISTIC_UUID =
            UUID.fromString("BB930A05-3CE1-4720-A753-28C0159DC777")
    }
}