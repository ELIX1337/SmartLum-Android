package com.smartlum.smartlum.viewmodels

import android.app.Application
import android.bluetooth.BluetoothAdapter
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.location.LocationManager
import android.os.ParcelUuid
import androidx.lifecycle.AndroidViewModel
import com.smartlum.smartlum.profiles.torchere.TorchereManager
import com.smartlum.smartlum.utils.Utils
import no.nordicsemi.android.support.v18.scanner.*
import java.util.*

class ScannerViewModel(application: Application) : AndroidViewModel(application) {
    /**
     * Публичное API для получения списка устройств
     * @return Возвращает LiveData список устройств
     */
    /**
     * MutableLiveData содержащее список устройств
     */
    val devices = DevicesLiveData()
    /**
     * Публичное API для получения состяние сканнера
     * @return Возвращает LiveData состояние сканнера
     */
    /**
     * MutableLiveData содержащее состояние сканнера
     */
    val scannerState = ScannerStateLiveData(Utils.isBleEnabled, Utils.isLocationEnabled(application))

    /**
     * Удаляет ViewModel когда она более не используется
     * Вместе с этим обязательно удаляем BroadcastReceiver'ы чтобы избежать утечек памяти
     */
    override fun onCleared() {
        super.onCleared()
        getApplication<Application>().unregisterReceiver(bluetoothStateBroadcastReceiver)
        if (Utils.isMarshmallowOrAbove) {
            getApplication<Application>().unregisterReceiver(locationProviderChangedReceiver)
        }
    }

    /**
     * Делает рефреш, когда получены разрешения на местоположение.
     * В результате слушатель в [com.smartlum.smartlum.ui.ScannerFragment] попробует начать сканировние заново.
     */
    fun refresh() {
        scannerState.refresh()
    }

    /**
     * Начинает сканирование BLE устройств.
     */
    fun startScan() {
        if (scannerState.isScanning) {
            return
        }

        // Настройки сканирования
        val settings = ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            .setReportDelay(500)
            .setUseHardwareBatchingIfSupported(false)
            .build()

        // Получаем BLE сканнер
        val scanner = BluetoothLeScannerCompat.getScanner()
        // UUID сервисов, которые мы будем искать
        val TORCHERE_UUID = ParcelUuid(TorchereManager.TORCHERE_SERVICE_UUID)
        val FL_MINI_UUID = ParcelUuid(TorchereManager.FL_MINI_SERVICE_UUID)
        val uuidList: MutableList<ParcelUuid> = ArrayList()
        uuidList.add(TORCHERE_UUID)
        uuidList.add(FL_MINI_UUID)
        // Фильтр для сканирования. В него помещаем необходимые UUID
        val filters: MutableList<ScanFilter> = ArrayList()
        for (a in uuidList) {
            filters.add(ScanFilter.Builder().setServiceUuid(a).build())
        }
        // Вот здесь уже внатуре запускается сканирование.
        scanner.startScan(filters, settings, scanCallback)
        // Сообщаем нашей LiveData, что началось сканирование
        scannerState.scanningStarted()
    }

    /**
     * Останавливает сканирование BLE устройств.
     */
    fun stopScan() {
        if (scannerState.isScanning && scannerState.isBluetoothEnabled) {
            val scanner = BluetoothLeScannerCompat.getScanner()
            scanner.stopScan(scanCallback)
            scannerState.scanningStopped()
        }
    }

    /**
     * ScanCallback - результат сканирования
     */
    private val scanCallback: ScanCallback = object : ScanCallback() {
        override fun onScanResult(callbackType: Int, result: ScanResult) {
            // This callback will be called only if the scan report delay is not set or is set to 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(
                    getApplication()
                )
            ) Utils.markLocationNotRequired(getApplication())
            if (devices.deviceDiscovered(result)) {
                devices.applyFilter()
                scannerState.recordFound()
            }
        }

        override fun onBatchScanResults(results: List<ScanResult>) {
            // This callback will be called only if the report delay set above is greater then 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(
                    getApplication()
                )
            ) Utils.markLocationNotRequired(getApplication())
            var atLeastOneMatchedFilter = false
            for (result in results) atLeastOneMatchedFilter =
                devices.deviceDiscovered(result) || atLeastOneMatchedFilter
            if (atLeastOneMatchedFilter) {
                devices.applyFilter()
                scannerState.recordFound()
            }
        }

        override fun onScanFailed(errorCode: Int) {
            // TODO This should be handled
            scannerState.scanningStopped()
        }
    }

    /**
     * Метод регистрирует необходиые BroadcastReciever'ы.
     */
    private fun registerBroadcastReceivers(application: Application) {
        application.registerReceiver(
            bluetoothStateBroadcastReceiver,
            IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED)
        )
        if (Utils.isMarshmallowOrAbove) {
            application.registerReceiver(
                locationProviderChangedReceiver,
                IntentFilter(LocationManager.MODE_CHANGED_ACTION)
            )
        }
    }

    /**
     * Broadcast receiver для мониторинга состояния службы геолокации (Bluetooth не работает без нее).
     */
    private val locationProviderChangedReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val enabled = Utils.isLocationEnabled(context)
            scannerState.setLocationEnabled(enabled)
        }
    }

    /**
     * Broadcast receiver для мониторинга состояния Bluetooth адаптера
     */
    private val bluetoothStateBroadcastReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context, intent: Intent) {
            val state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF)
            val previousState = intent.getIntExtra(
                BluetoothAdapter.EXTRA_PREVIOUS_STATE,
                BluetoothAdapter.STATE_OFF
            )
            when (state) {
                BluetoothAdapter.STATE_ON -> scannerState.bluetoothEnabled()
                BluetoothAdapter.STATE_TURNING_OFF, BluetoothAdapter.STATE_OFF -> if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                    stopScan()
                    scannerState.bluetoothDisabled()
                }
            }
        }
    }

    init {
        registerBroadcastReceivers(application)
    }
}