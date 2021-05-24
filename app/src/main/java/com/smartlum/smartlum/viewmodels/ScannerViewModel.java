package com.smartlum.smartlum.viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothAdapter;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.LocationManager;
import android.os.ParcelUuid;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.smartlum.smartlum.profiles.blinky.BlinkyManager;
import com.smartlum.smartlum.profiles.easy.EasyManager;
import com.smartlum.smartlum.profiles.torchere.TorchereManager;
import com.smartlum.smartlum.utils.Utils;

import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

public class ScannerViewModel extends AndroidViewModel {

    /**
     * MutableLiveData содержащее список устройств
     */
    private final DevicesLiveData devicesLiveData;
    /**
     * MutableLiveData содержащее состояние сканнера
     */
    private final ScannerStateLiveData scannerStateLiveData;

    /**
     * Публичное API для получения списка устройств
     * @return Возвращает LiveData список устройств
     */
    public DevicesLiveData getDevices() {
        return devicesLiveData;
    }

    /**
     * Публичное API для получения состяние сканнера
     * @return Возвращает LiveData состояние сканнера
     */
    public ScannerStateLiveData getScannerState() {
        return scannerStateLiveData;
    }

    public ScannerViewModel(@NonNull Application application) {
        super(application);

        scannerStateLiveData = new ScannerStateLiveData(Utils.isBleEnabled(), Utils.isLocationEnabled(application));
        devicesLiveData = new DevicesLiveData();
        registerBroadcastReceivers(application);
    }

    /**
     * Удаляет ViewModel когда она более не используется
     * Вместе с этим обязательно удаляем BroadcastReceiver'ы чтобы избежать утечек памяти
     */
    @Override
    protected void onCleared() {
        super.onCleared();
        getApplication().unregisterReceiver(bluetoothStateBroadcastReceiver);

        if (Utils.isMarshmallowOrAbove()) {
            getApplication().unregisterReceiver(locationProviderChangedReceiver);
        }
    }

    /**
     * Делает рефреш, когда получены разрешения на местоположение.
     * В результате слушатель в {@link com.smartlum.smartlum.ui.ScannerFragment} попробует начать сканировние заново.
     */
    public void refresh() {
        scannerStateLiveData.refresh();
    }

    /**
     * Начинает сканирование BLE устройств.
     */
    public void startScan() {
        if (scannerStateLiveData.isScanning()) {
            return;
        }

        // Настройки сканирования
        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                .setReportDelay(500)
                .setUseHardwareBatchingIfSupported(false)
                .build();

        // Получаем BLE сканнер
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        // UUID сервисов, которые мы будем искать
        final ParcelUuid BLINKY_UUID = new ParcelUuid(BlinkyManager.LBS_UUID_SERVICE);
        final ParcelUuid EASY_UUID = new ParcelUuid(EasyManager.UART_UUID_SERVICE);
        final ParcelUuid TORCHERE_UUID = new ParcelUuid(TorchereManager.TORCHERE_SERVICE_UUID);
        final List<ParcelUuid> uuidList = new ArrayList<>();
        uuidList.add(BLINKY_UUID);
        uuidList.add(EASY_UUID);
        uuidList.add(TORCHERE_UUID);
        // Фильтр для сканирования. В него помещаем необходимые UUID
        List<ScanFilter> filters = new ArrayList<>();
        for (ParcelUuid a:uuidList) {
            filters.add(new ScanFilter.Builder().setServiceUuid(a).build());
        }
        // Вот здесь уже внатуре запускается сканирование.
        scanner.startScan(filters, settings, scanCallback);
        // Сообщаем нашей LiveData, что началось сканирование
        scannerStateLiveData.scanningStarted();
    }

    /**
     * Останавливает сканирование BLE устройств.
     */
    public void stopScan() {
        if (scannerStateLiveData.isScanning() && scannerStateLiveData.isBluetoothEnabled()) {
            final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
            scanner.stopScan(scanCallback);
            scannerStateLiveData.scanningStopped();
        }
    }

    /**
     * ScanCallback - результат сканирования
     */
    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(int callbackType, @NonNull final ScanResult result) {
            // This callback will be called only if the scan report delay is not set or is set to 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
                Utils.markLocationNotRequired(getApplication());

            if (devicesLiveData.deviceDiscovered(result)) {
                devicesLiveData.applyFilter();
                scannerStateLiveData.recordFound();
            }
        }

        @Override
        public void onBatchScanResults(@NonNull List<no.nordicsemi.android.support.v18.scanner.ScanResult> results) {
            // This callback will be called only if the report delay set above is greater then 0.

            // If the packet has been obtained while Location was disabled, mark Location as not required
            if (Utils.isLocationRequired(getApplication()) && !Utils.isLocationEnabled(getApplication()))
                Utils.markLocationNotRequired(getApplication());

            boolean atLeastOneMatchedFilter = false;
            for (final ScanResult result : results)
                atLeastOneMatchedFilter = devicesLiveData.deviceDiscovered(result) || atLeastOneMatchedFilter;
            if (atLeastOneMatchedFilter) {
                devicesLiveData.applyFilter();
                scannerStateLiveData.recordFound();
            }
        }

        @Override
        public void onScanFailed(int errorCode) {
            // TODO This should be handled
            scannerStateLiveData.scanningStopped();
        }
    };

    /**
     * Метод регистрирует необходиые BroadcastReceiver'ы.
     */
    private void registerBroadcastReceivers(Application application) {
        application.registerReceiver(bluetoothStateBroadcastReceiver, new IntentFilter(BluetoothAdapter.ACTION_STATE_CHANGED));
        if (Utils.isMarshmallowOrAbove()) {
            application.registerReceiver(locationProviderChangedReceiver, new IntentFilter(LocationManager.MODE_CHANGED_ACTION));
        }
    }

    /**
     * Broadcast receiver для мониторинга состояния службы геолокации (Bluetooth не работает без нее).
     */
    private final BroadcastReceiver locationProviderChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final boolean enabled = Utils.isLocationEnabled(context);
            scannerStateLiveData.setLocationEnabled(enabled);
        }
    };

    /**
     * Broadcast receiver для мониторинга состояния Bluetooth адаптера
     */
    private final BroadcastReceiver bluetoothStateBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(final Context context, final Intent intent) {
            final int state = intent.getIntExtra(BluetoothAdapter.EXTRA_STATE, BluetoothAdapter.STATE_OFF);
            final int previousState = intent.getIntExtra(BluetoothAdapter.EXTRA_PREVIOUS_STATE, BluetoothAdapter.STATE_OFF);

            switch (state) {
                case BluetoothAdapter.STATE_ON:
                    scannerStateLiveData.bluetoothEnabled();
                    break;
                case BluetoothAdapter.STATE_TURNING_OFF:
                case BluetoothAdapter.STATE_OFF:
                    if (previousState != BluetoothAdapter.STATE_TURNING_OFF && previousState != BluetoothAdapter.STATE_OFF) {
                        stopScan();
                        scannerStateLiveData.bluetoothDisabled();
                    }
                    break;
            }
        }
    };
}
