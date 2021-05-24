package com.smartlum.smartlum.ui.devices.easy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.DiscoveredBluetoothDevice;
import com.smartlum.smartlum.utils.ErrorDialog;
import com.smartlum.smartlum.viewmodels.EasyViewModel;

import no.nordicsemi.android.ble.livedata.state.ConnectionState;

public class EasyActivity extends AppCompatActivity {
    public static final String EASY_DEVICE = "com.example.smartlum.ui.devices.easy.EASY_DEVICE";

    private EasyViewModel viewModel;

    private ConstraintLayout connectionLayout;
    private View fragmentLayout;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_easy);
        this.setTitle("Easy");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_container);
        final ViewGroup layout = findViewById(R.id.easy_activity_root_layout);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        fragmentLayout   = findViewById(R.id.nav_host_fragment_container);
        connectionLayout = findViewById(R.id.easy_connection_layout);
        ErrorDialog errorDialog = new ErrorDialog(this, getSupportFragmentManager());

        final Intent intent = getIntent();
        final DiscoveredBluetoothDevice device = intent.getParcelableExtra(EASY_DEVICE);
        final String deviceName = device.getName();
        final String deviceAddress = device.getAddress();

        viewModel = new ViewModelProvider(this).get(EasyViewModel.class);
        viewModel.connect(device);
        viewModel.getConnectionState().observe(this, state -> {
            this.setTitle("Easy - " + state.getState().toString());
            switch (state.getState()) {
                case CONNECTING:
                    fragmentLayout.setVisibility(View.GONE);
                    connectionLayout.setVisibility(View.VISIBLE);
                    break;
                case INITIALIZING:
                    break;
                case READY:
                    //progressContainer.setVisibility(View.GONE);
                    //content.setVisibility(View.VISIBLE);
                    //onConnectionStateChanged(true);
                    fragmentLayout.setVisibility(View.VISIBLE);
                    connectionLayout.setVisibility(View.GONE);
                    viewModel.requestSettings();
                    break;
                case DISCONNECTED:
                    if (state instanceof ConnectionState.Disconnected) {
                        final ConnectionState.Disconnected stateWithReason = (ConnectionState.Disconnected) state;
                        if (stateWithReason.isNotSupported()) {
                            this.finish();
                        }
                    }
                    // fallthrough
                case DISCONNECTING:
                    break;
            }
        });
        viewModel.getConfigStateFlag().observe(this, flag -> {
            if (flag == 0) {
                ConfigurationDialog.display(getSupportFragmentManager());
            }
        });

        viewModel.getErrors().observe(this, errorDialog::handleError);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            this.onBackPressed();
            return false;
        }
//        } else if (item.getItemId() == R.id.menu_button_device_settings) {
//            navController.navigate(R.id.easySettingsFragment);
//        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        viewModel.disconnect();
    }
}