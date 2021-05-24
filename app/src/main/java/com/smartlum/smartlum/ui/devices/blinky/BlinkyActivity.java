package com.smartlum.smartlum.ui.devices.blinky;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.viewmodels.BlinkyViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.switchmaterial.SwitchMaterial;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;

public class BlinkyActivity extends AppCompatActivity {

    public static final String BLINKY_DEVICE = "com.example.smartlum.ui.devices.blinky.BLINKY_DEVICE";

    private BlinkyViewModel viewModel;

    private SwitchMaterial led;
    private TextView buttonState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blinky);

        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(BLINKY_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        //getSupportActionBar().setTitle(deviceName != null ? deviceName : "Unknown Device");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        viewModel = new ViewModelProvider(this).get(BlinkyViewModel.class);
        viewModel.connect(device);

        final TextView ledState = findViewById(R.id.led_state);
        final ConstraintLayout progressContainer = findViewById(R.id.progress_container);
        final TextView connectionState = findViewById(R.id.connection_state);
        final View content = findViewById(R.id.device_container);
        final View notSupported = findViewById(R.id.not_supported);
        final MaterialButton btnClearCache = findViewById(R.id.action_clear_cache);
        led = findViewById(R.id.led_switch);
        btnClearCache.setOnClickListener(this::onTryAgainClicked);
        buttonState = findViewById(R.id.button_state);

        led.setOnCheckedChangeListener(((buttonView, isChecked) -> viewModel.setLedState(isChecked)));
        viewModel.getConnectionState().observe(this, state -> {
            switch (state.getState()) {
                case CONNECTING:
                    progressContainer.setVisibility(View.VISIBLE);
                    notSupported.setVisibility(View.GONE);
                    connectionState.setText(R.string.state_connecting);
                    break;
                case INITIALIZING:
                    connectionState.setText(R.string.state_initializing);
                    break;
                case READY:
                    progressContainer.setVisibility(View.GONE);
                    content.setVisibility(View.VISIBLE);
                    onConnectionStateChanged(true);
                    break;
                case DISCONNECTED:
                    if (state instanceof ConnectionState.Disconnected) {
                        final ConnectionState.Disconnected stateWithReason = (ConnectionState.Disconnected) state;
                        if (stateWithReason.isNotSupported()) {
                            progressContainer.setVisibility(View.GONE);
                            notSupported.setVisibility(View.VISIBLE);
                        }
                    }
                    // fallthrough
                case DISCONNECTING:
                    onConnectionStateChanged(false);
                    break;
            }
        });
        viewModel.getLedState().observe(this, isOn -> {
            ledState.setText(isOn ? R.string.turn_on : R.string.turn_off);
            led.setChecked(isOn);
        });
        viewModel.getButtonState().observe(this,
                pressed -> buttonState.setText(pressed ?
                        R.string.button_pressed : R.string.button_released));

    }

    public void onTryAgainClicked(View v) {
        viewModel.reconnect();
    }

    private void onConnectionStateChanged(final boolean connected) {
        led.setEnabled(connected);
        if (!connected) {
            led.setChecked(false);
            buttonState.setText(R.string.button_unknown);
        }
    }
}