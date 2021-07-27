package com.smartlum.smartlum.utils

import android.content.Context
import android.content.Intent
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice
import com.smartlum.smartlum.profiles.torchere.TorchereManager
import com.smartlum.smartlum.ui.devices.torchere.TorchereActivity
import no.nordicsemi.android.support.v18.scanner.ScanResult

/**
 * Класс-костыль для распознавания BLE устройств, т.к. приложение должно работать с различными девайсами
 * Принимает [ScanResult], читает какие BLE сервисы имеет устройство и на их основе "понимает", что это за устройство
 */
object DeviceQualifier {
    fun startDeviceActivity(context: Context, device: DiscoveredBluetoothDevice) {
        val intent: Intent
        when (device.getDeviceType()) {
            DiscoveredBluetoothDevice.Type.TORCHERE -> {
                intent = Intent(context, TorchereActivity::class.java)
                intent.putExtra(TorchereActivity.TORCHERE_DEVICE, device)
            }
            else -> throw IllegalStateException("Unexpected device type: " + device.getDeviceType())
        }
        context.startActivity(intent)
    }

    fun defineDeviceType(result: ScanResult): DiscoveredBluetoothDevice.Type? {
        val type = result.scanRecord!!.serviceUuids!![0].toString()
        if (type == TorchereManager.TORCHERE_SERVICE_UUID.toString()) return DiscoveredBluetoothDevice.Type.TORCHERE else if (type == TorchereManager.FL_MINI_SERVICE_UUID.toString()) return DiscoveredBluetoothDevice.Type.TORCHERE
        return null
    }
}