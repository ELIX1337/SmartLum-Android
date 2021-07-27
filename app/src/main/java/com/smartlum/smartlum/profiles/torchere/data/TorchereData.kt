package com.smartlum.smartlum.profiles.torchere.data

import no.nordicsemi.android.ble.data.Data
import java.util.*

object TorchereData {
    @JvmField
    var animationModes: HashMap<Int, String>? = null
    @JvmField
    var animationDirections: HashMap<Int, String>? = null
    private const val STATE_OFF: Byte = 0x00
    private const val STATE_ON: Byte = 0x01
    @JvmStatic
    fun writeTrue(): Data {
        return Data.opCode(STATE_ON)
    }

    @JvmStatic
    fun writeFalse(): Data {
        return Data.opCode(STATE_OFF)
    }

    init {
        animationModes = HashMap()
        animationModes!![1] = "Tetris"
        animationModes!![2] = "Wave"
        animationModes!![3] = "Transfusion"
        animationModes!![4] = "Full Rainbow"
        animationModes!![5] = "Rainbow"
        animationModes!![6] = "Static"
    }

    init {
        animationDirections = HashMap()
        animationDirections!![1] = "From Bottom"
        animationDirections!![2] = "From Top"
        animationDirections!![3] = "To Center"
        animationDirections!![4] = "From Center"
    }
}