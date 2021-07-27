package com.smartlum.smartlum.ui.devices.torchere

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.Navigation
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.smartlum.smartlum.R
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice
import com.smartlum.smartlum.viewmodels.TorchereViewModel
import no.nordicsemi.android.ble.livedata.state.ConnectionState

class TorchereActivity : AppCompatActivity() {
    private var viewModel: TorchereViewModel? = null
    private var fragmentLayout: View? = null
    private var connectingDialog: AlertDialog? = null
    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_torchere)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        val navController = Navigation.findNavController(this, R.id.nav_host_fragment_container)
        fragmentLayout = findViewById(R.id.nav_host_fragment_container)
        val intent = intent
        val device: DiscoveredBluetoothDevice? = intent.getParcelableExtra(TORCHERE_DEVICE)
        val deviceName = device?.name
        val deviceAddress = device?.address
        viewModel = ViewModelProvider(this).get(TorchereViewModel::class.java)
        if (device != null) {
            viewModel?.connect(device)
        }
        this.title = deviceName
        connectingDialog = MaterialAlertDialogBuilder(this)
            .setTitle("Connecting to $deviceName")
            .create()
        connectingDialog!!.setCancelable(false)
        viewModel!!.getConnectionState().observe(this, { state: ConnectionState ->
            connectingDialog!!.setMessage(state.state.toString())
            when (state.state) {
                ConnectionState.State.CONNECTING -> connectingDialog!!.show()
                ConnectionState.State.INITIALIZING -> {
                }
                ConnectionState.State.READY -> connectingDialog!!.dismiss()
                ConnectionState.State.DISCONNECTED -> if (state is ConnectionState.Disconnected) {
                    val stateWithReason: ConnectionState.Disconnected = state
                    if (stateWithReason.isNotSupported) {
                        connectingDialog!!.dismiss()
                        finish()
                    }
                }
                ConnectionState.State.DISCONNECTING -> {
                }
            }
        })
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == android.R.id.home) {
            onBackPressed()
            return false
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onDestroy() {
        super.onDestroy()
        viewModel!!.disconnect()
    }

    companion object {
        const val TORCHERE_DEVICE = "com.example.smartlum.ui.devices.torchere.TORCHERE_DEVICE"
    }
}