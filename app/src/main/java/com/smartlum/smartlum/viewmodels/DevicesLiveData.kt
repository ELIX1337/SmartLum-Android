package com.smartlum.smartlum.viewmodels

import android.os.ParcelUuid
import androidx.lifecycle.LiveData
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice
import com.smartlum.smartlum.profiles.torchere.TorchereManager
import no.nordicsemi.android.support.v18.scanner.ScanResult
import java.util.*

/**
 * Этот класс хранит в себе список текущих найденных BLE устройств.
 */
class DevicesLiveData : LiveData<List<DiscoveredBluetoothDevice?>?> {
    private val devices: MutableList<DiscoveredBluetoothDevice> = ArrayList()
    private var filteredDevices: List<DiscoveredBluetoothDevice>? = null
    private var filterUuidRequired: Boolean
    private var filterNearbyOnly = false

    /* package */
    internal constructor(filterUuidRequired: Boolean, filterNearbyOnly: Boolean) {
        this.filterUuidRequired = filterUuidRequired
        this.filterNearbyOnly = filterNearbyOnly
    }

    /* package */
    internal constructor() {
        filterUuidRequired = false
    }

    /* package */
    @Synchronized
    fun bluetoothDisabled() {
        devices.clear()
        filteredDevices = null
        postValue(null)
    }

    /* package */
    fun filterByUuid(uuidRequired: Boolean): Boolean {
        filterUuidRequired = uuidRequired
        return applyFilter()
    }

    /* package */
    fun filterByDistance(nearbyOnly: Boolean): Boolean {
        filterNearbyOnly = nearbyOnly
        return applyFilter()
    }

    /* package */
    @Synchronized
    fun deviceDiscovered(result: ScanResult): Boolean {
        val device: DiscoveredBluetoothDevice

        // Check if it's a new device.
        val index = indexOf(result)
        if (index == -1) {
            device = DiscoveredBluetoothDevice(result)
            devices.add(device)
        } else {
            device = devices[index]
        }

        // Update RSSI and name.
        device.update(result)

        // Return true if the device was on the filtered list or is to be added.
        return (filteredDevices != null && filteredDevices!!.contains(device)
                || matchesUuidFilter(result) && matchesNearbyFilter(device.highestRssi))
    }

    /**
     * Clears the list of devices.
     */
    @Synchronized
    fun clear() {
        devices.clear()
        filteredDevices = null
        postValue(null)
    }

    /**
     * Refreshes the filtered device list based on the filter flags.
     */
    /* package */
    @Synchronized
    fun applyFilter(): Boolean {
        val tmp: MutableList<DiscoveredBluetoothDevice> = ArrayList()
        for (device in devices) {
            val result = device.scanResult
            if (matchesUuidFilter(result) && matchesNearbyFilter(device.highestRssi)) {
                tmp.add(device)
            }
        }
        filteredDevices = tmp
        postValue(filteredDevices)
        return !filteredDevices?.isEmpty()!!
    }

    /**
     * Finds the index of existing devices on the device list.
     *
     * @param result scan result.
     * @return Index of -1 if not found.
     */
    private fun indexOf(result: ScanResult): Int {
        for ((i, device) in devices.withIndex()) {
            if (device.matches(result)) return i
        }
        return -1
    }

    private fun matchesUuidFilter(result: ScanResult): Boolean {
        if (!filterUuidRequired) return true
        val record = result.scanRecord ?: return false
        val uuids = record.serviceUuids ?: return false
        return uuids.contains(FILTER_UUID)
        //return uuids.retainAll(Arrays.asList(FILTER));
    }

    private fun matchesNearbyFilter(rssi: Int): Boolean {
        return if (!filterNearbyOnly) true else rssi >= FILTER_RSSI
    }

    companion object {
        private val FILTER_UUID = ParcelUuid(TorchereManager.TORCHERE_SERVICE_UUID)
        private val FILTER_UUID2 = ParcelUuid(TorchereManager.FL_MINI_SERVICE_UUID)
        private val FILTER = arrayOf(FILTER_UUID, FILTER_UUID2)
        private const val FILTER_RSSI = -50 // [dBm]
    }
}