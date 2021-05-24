package com.smartlum.smartlum.utils;

import android.content.Context;
import android.content.Intent;

import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.profiles.blinky.BlinkyManager;
import com.smartlum.smartlum.profiles.easy.EasyManager;
import com.smartlum.smartlum.profiles.torchere.TorchereManager;
import com.smartlum.smartlum.ui.devices.blinky.BlinkyActivity;
import com.smartlum.smartlum.ui.devices.easy.EasyActivity;
import com.smartlum.smartlum.ui.devices.torchere.TorchereActivity;

import no.nordicsemi.android.support.v18.scanner.ScanResult;

/**
 * Класс-костыль для распознавания BLE устройств, т.к. приложение должно работать с различными девайсами
 * Принимает {@link ScanResult}, читает какие BLE сервисы имеет устройство и на их основе "понимает", что это за устройство
 */

public class DeviceQualifier {

    public static void startDeviceActivity(Context context, DiscoveredBluetoothDevice device) {
        final Intent intent;
        switch (device.getDeviceType()) {
            case NORDIC_BLINKY:
                intent = new Intent(context, BlinkyActivity.class);
                intent.putExtra(BlinkyActivity.BLINKY_DEVICE, device);
                break;
            case EASY:
                intent = new Intent(context, EasyActivity.class);
                intent.putExtra(EasyActivity.EASY_DEVICE, device);
                break;
            case TORCHERE:
                intent = new Intent(context, TorchereActivity.class);
                intent.putExtra(TorchereActivity.TORCHERE_DEVICE, device);
                break;
            default:
                throw new IllegalStateException("Unexpected device type: " + device.getDeviceType());
        }
        context.startActivity(intent);
    }

    public static DiscoveredBluetoothDevice.Type defineDeviceType(ScanResult result) {
        final String type = result.getScanRecord().getServiceUuids().get(0).toString();
        if (type.equals(BlinkyManager.LBS_UUID_SERVICE.toString()))
            return DiscoveredBluetoothDevice.Type.NORDIC_BLINKY;
        else if (type.equals(EasyManager.UART_UUID_SERVICE.toString()))
            return DiscoveredBluetoothDevice.Type.EASY;
        else if (type.equals(TorchereManager.TORCHERE_SERVICE_UUID.toString()))
            return DiscoveredBluetoothDevice.Type.TORCHERE;
        return null;
    }
}
