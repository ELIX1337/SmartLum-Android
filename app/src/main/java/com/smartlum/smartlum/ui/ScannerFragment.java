package com.smartlum.smartlum.ui;

import android.Manifest;
import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.DevicesAdapter;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.utils.DeviceQualifier;
import com.smartlum.smartlum.utils.Utils;
import com.smartlum.smartlum.viewmodels.ScannerStateLiveData;
import com.smartlum.smartlum.viewmodels.ScannerViewModel;
import com.google.android.material.button.MaterialButton;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ScannerFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ScannerFragment extends Fragment implements DevicesAdapter.OnItemClickListener {
    private static final int REQUEST_ACCESS_FINE_LOCATION = 1001; // Случайное число, используется как RequestCode

    // Backend
    private ScannerViewModel scannerViewModel;

    // UI
    private View noDevicesView;
    private View noLocationView;
    private View noBluetoothView;
    private View noLocationPermissionView;
    private MaterialButton btnRequestLocation;
    private MaterialButton btnOpenAppSettings;

    private ProgressBar progressBar;

    public ScannerFragment() {
        // Required empty public constructor
    }

    public static ScannerFragment newInstance(String param1, String param2) {
        return new ScannerFragment();
    }

    /**
     * Преопределяемые ниже методы:
     * onCreate, onCreateView, onViewCreated, onStart, onStop
     * описываются жизненным циклом (lifecycle) фрагмента и активити
     * они вызываются при создании, уничтожении, показе, скрытии фрагмента/активити
     * подробнее лучше почитать в интернете
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_scanner, container, false);
        requireActivity().setTitle(getResources().getString(R.string.scanner));

        // Инициализируем UI
        progressBar              = view.findViewById(R.id.scan_progress_bar);
        noDevicesView            = view.findViewById(R.id.no_devices);
        noLocationView           = view.findViewById(R.id.no_location);
        noBluetoothView          = view.findViewById(R.id.bluetooth_off);
        noLocationPermissionView = view.findViewById(R.id.no_location_permission);
        final MaterialButton btnEnableLocation  = view.findViewById(R.id.action_enable_location);
        final MaterialButton btnEnableBluetooth = view.findViewById(R.id.action_enable_bluetooth);
        btnRequestLocation = view.findViewById(R.id.action_grant_location_permission);
        btnOpenAppSettings = view.findViewById(R.id.action_permission_settings);

        // Задаем слушатели на кнопки
        btnEnableLocation.setOnClickListener(this::onEnableLocationClicked);
        btnEnableBluetooth.setOnClickListener(this::onEnableBluetoothClicked);
        btnRequestLocation.setOnClickListener(this::onGrantLocationPermissionClicked);
        btnOpenAppSettings.setOnClickListener(this::onPermissionSettingsClicked);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Создаем модель, которая содержит в себе логику сканирования BLE
        scannerViewModel = new ViewModelProvider(requireActivity()).get(ScannerViewModel.class);
        scannerViewModel.getScannerState().observe(getViewLifecycleOwner(), this::startScan);

        // Инициализация списка устройств
        final RecyclerView recyclerView = view.findViewById(R.id.recycler_view_ble_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(requireActivity(), DividerItemDecoration.VERTICAL));
        final RecyclerView.ItemAnimator animator = recyclerView.getItemAnimator();
        if (animator instanceof SimpleItemAnimator) {
            ((SimpleItemAnimator) animator).setSupportsChangeAnimations(false);
        }
        final DevicesAdapter adapter = new DevicesAdapter(this, scannerViewModel.getDevices());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        clear();
    }

    @Override
    public void onStop() {
        super.onStop();
        stopScan();
    }

    /**
     * Обработчик нажатий на элемент (девайс) из списка
     */
    @Override
    public void onItemClick(@NonNull DiscoveredBluetoothDevice device) {
        DeviceQualifier.startDeviceActivity(requireActivity(), device);
        Log.d("TAG", "onItemClick: " + device.getScanResult().getScanRecord().getServiceUuids());
        Log.d("TAG", "onItemClick: " + device.getScanResult().getAdvertisingSid());
    }

    /**
     * Получаем результат запроса разрешений
     */
    private final ActivityResultLauncher<String> mPermissionResult = registerForActivityResult(
            new ActivityResultContracts.RequestPermission(),
            result -> {
                if(result) {
                    Log.e("TAG", "onActivityResult: PERMISSION GRANTED");
                    scannerViewModel.refresh();
                } else {
                    Log.e("TAG", "onActivityResult: PERMISSION DENIED");
                    btnRequestLocation.setVisibility(View.GONE);
                    btnOpenAppSettings.setVisibility(View.VISIBLE);
                }
            });

    /**
     * Метод запрашивающий включение службы геолокации
     */
    public void onEnableLocationClicked(View view) {
        final Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(intent);
    }

    /**
     * Метод запрашивающий включение Bluetooth
     */
    public void onEnableBluetoothClicked(View view) {
        final Intent enableIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivity(enableIntent);
    }

    /**
     * Метод запрашивающий разрешение на использование службы геолокиции
     * Константа REQUEST_ACCESS_FINE_LOCATION используется как код, по которому потом будем ловить результат запроса
     */
    public void onGrantLocationPermissionClicked(View view) {
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION);
    }

    /**
     * Метод, который открывает настройки приложения.
     * Вызывается когда пользователь идиот и выбрал пункт "Запретить и никогда не показывать снова" при запросе разрешений
     * и теперь должен вручную их предоставить в настройках приложения.
     * Вешается на слушатель соответсвующей кнопки, а не вызывается автоматически при запрете
     */
    public void onPermissionSettingsClicked(View view) {
        final Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        intent.setData(Uri.fromParts("package", requireActivity().getPackageName(), null));
        startActivity(intent);
    }

    /**
     * Начинает сканирование Bluetooth предварительно проверив состояние Bluetooth и разрешения
     * Если все ОК - запускает сканирование
     * Иначе - покажет необходимое окно с проблемой.
     */
    private void startScan(final ScannerStateLiveData state) {
        // Сначала проверяем разрешение на геолокацию (без нее не работает Bluetooth). Для пользователей Android Marshmallow и выше
        if (Utils.isLocationPermissionsGranted(requireActivity())) {
            noLocationPermissionView.setVisibility(View.GONE);

            // Теперь проверяем, включен ли Bluetooth.
            if (state.isBluetoothEnabled()) {
                noBluetoothView.setVisibility(View.GONE);

                // Если дошли сюда - все окей, можно начинать сканирование.
                scannerViewModel.startScan();
                progressBar.setVisibility(View.VISIBLE);

                if (!state.hasRecords()) {
                    noDevicesView.setVisibility(View.VISIBLE);
                    if (Utils.isLocationRequired(requireActivity()) || !Utils.isLocationEnabled(requireActivity())) {
                        noLocationView.setVisibility(View.VISIBLE);
                        Log.d("TAG", !Utils.isLocationRequired(requireActivity()) + "*" + Utils.isLocationEnabled(requireActivity()));
                    } else {
                        noLocationView.setVisibility(View.INVISIBLE);
                        Log.e("TAG", "startScan: INVISIBLE" );
                    }
                } else {
                    noDevicesView.setVisibility(View.GONE);
                }
            } else {
                noBluetoothView.setVisibility(View.VISIBLE);
                progressBar.setVisibility(View.INVISIBLE);
                noDevicesView.setVisibility(View.GONE);
                clear();
            }
        } else {
            noLocationPermissionView.setVisibility(View.VISIBLE);
            noBluetoothView.setVisibility(View.GONE);
            progressBar.setVisibility(View.INVISIBLE);
            noDevicesView.setVisibility(View.GONE);

            // Проверяем, если пользователь запретил запрос разрешений НАВСЕГДА
            // чтобы показать кнопку перехода к настройкам приложения
            final boolean deniedForever = Utils.isLocationPermissionDeniedForever(requireActivity());
            btnRequestLocation.setVisibility(deniedForever ? View.GONE : View.VISIBLE);
            btnOpenAppSettings.setVisibility(deniedForever ? View.VISIBLE : View.GONE);
        }
    }

    /**
     * Останавливает сканирование BLE устройств.
     * Если быть точнее, дает команду своей ViewModel чтобы она остановила сканирование.
     */
    private void stopScan() {
        scannerViewModel.stopScan();
    }

    /**
     * Очищает список найденных устройств.
     */
    private void clear() {
        scannerViewModel.getDevices().clear();
        scannerViewModel.getScannerState().clearRecords();
    }

}