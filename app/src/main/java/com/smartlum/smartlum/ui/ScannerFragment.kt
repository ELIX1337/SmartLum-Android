package com.smartlum.smartlum.ui

import android.Manifest
import android.bluetooth.BluetoothAdapter
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts.RequestPermission
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.SimpleItemAnimator
import com.google.android.material.button.MaterialButton
import com.smartlum.smartlum.R
import com.smartlum.smartlum.adapter.DevicesAdapter
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice
import com.smartlum.smartlum.utils.DeviceQualifier
import com.smartlum.smartlum.utils.Utils
import com.smartlum.smartlum.viewmodels.ScannerStateLiveData
import com.smartlum.smartlum.viewmodels.ScannerViewModel

/**
 * A simple [Fragment] subclass.
 * Use the [ScannerFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ScannerFragment : Fragment(), DevicesAdapter.OnItemClickListener {
    // Backend
    private var scannerViewModel: ScannerViewModel? = null

    // UI
    private var noDevicesView: View? = null
    private var noLocationView: View? = null
    private var noBluetoothView: View? = null
    private var noLocationPermissionView: View? = null
    private var btnRequestLocation: MaterialButton? = null
    private var btnOpenAppSettings: MaterialButton? = null
    private var progressBar: ProgressBar? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_scanner, container, false)
        requireActivity().title = resources.getString(R.string.scanner)

        // Инициализируем UI
        progressBar = view.findViewById(R.id.scan_progress_bar)
        noDevicesView = view.findViewById(R.id.no_devices)
        noLocationView = view.findViewById(R.id.no_location)
        noBluetoothView = view.findViewById(R.id.bluetooth_off)
        noLocationPermissionView = view.findViewById(R.id.no_location_permission)
        val btnEnableLocation: MaterialButton = view.findViewById(R.id.action_enable_location)
        val btnEnableBluetooth: MaterialButton = view.findViewById(R.id.action_enable_bluetooth)
        btnRequestLocation = view.findViewById(R.id.action_grant_location_permission)
        btnOpenAppSettings = view.findViewById(R.id.action_permission_settings)

        // Задаем слушатели на кнопки
        btnEnableLocation.setOnClickListener { onEnableLocationClicked() }
        btnEnableBluetooth.setOnClickListener { onEnableBluetoothClicked() }
        btnRequestLocation?.setOnClickListener { onGrantLocationPermissionClicked() }
        btnOpenAppSettings?.setOnClickListener { onPermissionSettingsClicked() }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Создаем модель, которая содержит в себе логику сканирования BLE
        scannerViewModel = ViewModelProvider(requireActivity()).get(
            ScannerViewModel::class.java
        )
        scannerViewModel?.scannerState?.observe(
            viewLifecycleOwner,
            { state: ScannerStateLiveData? ->
                if (state != null) {
                    startScan(state)
                }
            })

        // Инициализация списка устройств
        val recyclerView: RecyclerView = view.findViewById(R.id.recycler_view_ble_devices)
        recyclerView.layoutManager = LinearLayoutManager(requireActivity())
        recyclerView.addItemDecoration(
            DividerItemDecoration(
                requireActivity(),
                DividerItemDecoration.VERTICAL
            )
        )
        val animator = recyclerView.itemAnimator
        if (animator is SimpleItemAnimator) {
            animator.supportsChangeAnimations = false
        }
        val adapter = DevicesAdapter(this, scannerViewModel!!.devices)
        adapter.setOnItemClickListener(this)
        recyclerView.adapter = adapter
    }

    override fun onStart() {
        super.onStart()
        clear()
    }

    override fun onStop() {
        super.onStop()
        stopScan()
    }

    /**
     * Обработчик нажатий на элемент (девайс) из списка
     */
    override fun onItemClick(device: DiscoveredBluetoothDevice) {
        DeviceQualifier.startDeviceActivity(requireActivity(), device)
        Log.d("TAG", "onItemClick: " + device.scanResult.scanRecord!!.serviceUuids)
        Log.d("TAG", "onItemClick: " + device.scanResult.advertisingSid)
    }

    /**
     * Получаем результат запроса разрешений
     */
    private val mPermissionResult = registerForActivityResult(
        RequestPermission()
    ) { result: Boolean ->
        if (result) {
            Log.e("TAG", "onActivityResult: PERMISSION GRANTED")
            scannerViewModel!!.refresh()
        } else {
            Log.e("TAG", "onActivityResult: PERMISSION DENIED")
            btnRequestLocation!!.visibility = View.GONE
            btnOpenAppSettings!!.visibility = View.VISIBLE
        }
    }

    /**
     * Метод запрашивающий включение службы геолокации
     */
    fun onEnableLocationClicked() {
        val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
        startActivity(intent)
    }

    /**
     * Метод запрашивающий включение Bluetooth
     */
    fun onEnableBluetoothClicked() {
        val enableIntent = Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE)
        startActivity(enableIntent)
    }

    /**
     * Метод запрашивающий разрешение на использование службы геолокиции
     * Константа REQUEST_ACCESS_FINE_LOCATION используется как код, по которому потом будем ловить результат запроса
     */
    private fun onGrantLocationPermissionClicked() {
        mPermissionResult.launch(Manifest.permission.ACCESS_FINE_LOCATION)
    }

    /**
     * Метод, который открывает настройки приложения.
     * Вызывается когда пользователь идиот и выбрал пункт "Запретить и никогда не показывать снова" при запросе разрешений
     * и теперь должен вручную их предоставить в настройках приложения.
     * Вешается на слушатель соответсвующей кнопки, а не вызывается автоматически при запрете
     */
    private fun onPermissionSettingsClicked() {
        val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
        intent.data = Uri.fromParts("package", requireActivity().packageName, null)
        startActivity(intent)
    }

    /**
     * Начинает сканирование Bluetooth предварительно проверив состояние Bluetooth и разрешения
     * Если все ОК - запускает сканирование
     * Иначе - покажет необходимое окно с проблемой.
     */
    private fun startScan(state: ScannerStateLiveData) {
        // Сначала проверяем разрешение на геолокацию (без нее не работает Bluetooth). Для пользователей Android Marshmallow и выше
        if (Utils.isLocationPermissionsGranted(requireActivity())) {
            noLocationPermissionView!!.visibility = View.GONE

            // Теперь проверяем, включен ли Bluetooth.
            if (state.isBluetoothEnabled) {
                noBluetoothView!!.visibility = View.GONE

                // Если дошли сюда - все окей, можно начинать сканирование.
                scannerViewModel!!.startScan()
                progressBar!!.visibility = View.VISIBLE
                if (!state.hasRecords()) {
                    noDevicesView!!.visibility = View.VISIBLE
                    if (Utils.isLocationRequired(requireActivity()) || !Utils.isLocationEnabled(
                            requireActivity()
                        )
                    ) {
                        noLocationView!!.visibility = View.VISIBLE
                    } else {
                        noLocationView!!.visibility = View.INVISIBLE
                        Log.e("TAG", "startScan: INVISIBLE")
                    }
                } else {
                    noDevicesView!!.visibility = View.GONE
                }
            } else {
                noBluetoothView!!.visibility = View.VISIBLE
                progressBar!!.visibility = View.INVISIBLE
                noDevicesView!!.visibility = View.GONE
                clear()
            }
        } else {
            noLocationPermissionView!!.visibility = View.VISIBLE
            noBluetoothView!!.visibility = View.GONE
            progressBar!!.visibility = View.INVISIBLE
            noDevicesView!!.visibility = View.GONE

            // Проверяем, если пользователь запретил запрос разрешений НАВСЕГДА
            // чтобы показать кнопку перехода к настройкам приложения
            val deniedForever = Utils.isLocationPermissionDeniedForever(requireActivity())
            btnRequestLocation!!.visibility = if (deniedForever) View.GONE else View.VISIBLE
            btnOpenAppSettings!!.visibility = if (deniedForever) View.VISIBLE else View.GONE
        }
    }

    /**
     * Останавливает сканирование BLE устройств.
     * Если быть точнее, дает команду своей ViewModel чтобы она остановила сканирование.
     */
    private fun stopScan() {
        scannerViewModel!!.stopScan()
    }

    /**
     * Очищает список найденных устройств.
     */
    private fun clear() {
        scannerViewModel!!.devices.clear()
        scannerViewModel!!.scannerState.clearRecords()
    }

    companion object {
        private const val REQUEST_ACCESS_FINE_LOCATION =
            1001 // Случайное число, используется как RequestCode

        fun newInstance(param1: String?, param2: String?): ScannerFragment {
            return ScannerFragment()
        }
    }
}