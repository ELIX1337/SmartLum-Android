/*
 * Copyright (c) 2018, Nordic Semiconductor
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice, this list of conditions and the following disclaimer in the
 * documentation and/or other materials provided with the distribution.
 *
 * 3. Neither the name of the copyright holder nor the names of its contributors may be used to endorse or promote products derived from this
 * software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT
 * LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT
 * HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 * ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE
 * USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.smartlum.smartlum.adapter

import android.bluetooth.BluetoothDevice
import android.os.Parcel
import android.os.Parcelable
import com.smartlum.smartlum.utils.DeviceQualifier
import no.nordicsemi.android.support.v18.scanner.ScanResult
import org.jetbrains.annotations.Contract

class DiscoveredBluetoothDevice : Parcelable {
    val device: BluetoothDevice
    private var lastScanResult: ScanResult? = null
    var name: String? = null
        private set
    var rssi = 0
        private set
    private var previousRssi = 0

    /**
     * Returns the highest recorded RSSI value during the scan.
     *
     * @return Highest RSSI value.
     */
    var highestRssi = -128
        private set

    enum class Type {
        NORDIC_BLINKY, EASY, TORCHERE
    }

    private var deviceType: Type? = null

    constructor(scanResult: ScanResult) {
        device = scanResult.device
        update(scanResult)
        deviceType = DeviceQualifier.defineDeviceType(scanResult)
    }

    val address: String
        get() = device.address

    fun getDeviceType(): Type {
        return deviceType!!
    }

    val scanResult: ScanResult
        get() = lastScanResult!!

    /**
     * This method returns true if the RSSI range has changed. The RSSI range depends on drawable
     * levels from [com.smartlum.smartlum.R.drawable.ic_signal_bar].
     *
     * @return True, if the RSSI range has changed.
     */
    /* package */
    fun hasRssiLevelChanged(): Boolean {
        val newLevel =
            if (rssi <= 10) 0 else if (rssi <= 28) 1 else if (rssi <= 45) 2 else if (rssi <= 65) 3 else 4
        val oldLevel =
            if (previousRssi <= 10) 0 else if (previousRssi <= 28) 1 else if (previousRssi <= 45) 2 else if (previousRssi <= 65) 3 else 4
        return newLevel != oldLevel
    }

    /**
     * Updates the device values based on the scan result.
     *
     * @param scanResult the new received scan result.
     */
    fun update(scanResult: ScanResult) {
        lastScanResult = scanResult
        name = if (scanResult.scanRecord != null) scanResult.scanRecord!!.deviceName else null
        previousRssi = rssi
        rssi = scanResult.rssi
        if (highestRssi < rssi) highestRssi = rssi
    }

    fun matches(scanResult: ScanResult): Boolean {
        return device.address == scanResult.device.address
    }

    override fun hashCode(): Int {
        return device.hashCode()
    }

    override fun equals(other: Any?): Boolean {
        if (other is DiscoveredBluetoothDevice) {
            return device.address == other.device.address
        }
        return super.equals(other)
    }

    // Parcelable implementation
    private constructor(`in`: Parcel) {
        device = `in`.readParcelable(BluetoothDevice::class.java.classLoader)!!
        lastScanResult = `in`.readParcelable(ScanResult::class.java.classLoader)
        name = `in`.readString()
        rssi = `in`.readInt()
        previousRssi = `in`.readInt()
        highestRssi = `in`.readInt()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeParcelable(device, flags)
        parcel.writeParcelable(lastScanResult, flags)
        parcel.writeString(name)
        parcel.writeInt(rssi)
        parcel.writeInt(previousRssi)
        parcel.writeInt(highestRssi)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object {
        @JvmField val CREATOR: Parcelable.Creator<DiscoveredBluetoothDevice> =
            object : Parcelable.Creator<DiscoveredBluetoothDevice> {
                @Contract("_ -> new")
                override fun createFromParcel(source: Parcel): DiscoveredBluetoothDevice {
                    return DiscoveredBluetoothDevice(source)
                }

                override fun newArray(size: Int): Array<DiscoveredBluetoothDevice?> {
                    return arrayOfNulls(size)
                }
            }
    }
}