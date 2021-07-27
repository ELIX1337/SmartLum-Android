package com.smartlum.smartlum.ui.devices.torchere;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Spinner;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.dialog.MaterialDialogs;
import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.viewmodels.TorchereViewModel;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;

public class TorchereActivity extends AppCompatActivity {
    public static final String TORCHERE_DEVICE = "com.example.smartlum.ui.devices.torchere.TORCHERE_DEVICE";

    private TorchereViewModel viewModel;

    private View fragmentLayout;
    private AlertDialog connectingDialog;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_torchere);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);

        fragmentLayout   = findViewById(R.id.nav_host_fragment_container);

        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(TORCHERE_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        viewModel = new ViewModelProvider(this).get(TorchereViewModel.class);
        viewModel.connect(device);
        this.setTitle(deviceName);

        connectingDialog = new MaterialAlertDialogBuilder(this)
                .setTitle("Connecting to " + deviceName)
                .create();
        connectingDialog.setCancelable(false);

        viewModel.getConnectionState().observe(this, state -> {
            connectingDialog.setMessage(state.getState().toString());
            switch (state.getState()) {
                case CONNECTING:
                    connectingDialog.show();
                    break;
                case INITIALIZING:
                    break;
                case READY:
                    connectingDialog.dismiss();
                    break;
                case DISCONNECTED:
                    if (state instanceof ConnectionState.Disconnected) {
                        final ConnectionState.Disconnected stateWithReason = (ConnectionState.Disconnected) state;
                        if (stateWithReason.isNotSupported()) {
                            connectingDialog.dismiss();
                            this.finish();
                        }
                    }
                    // fallthrough
                case DISCONNECTING:
                    break;
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return false;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.disconnect();
    }

}