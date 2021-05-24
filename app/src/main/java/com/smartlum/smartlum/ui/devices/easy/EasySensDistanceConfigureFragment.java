package com.smartlum.smartlum.ui.devices.easy;

import android.animation.LayoutTransition;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.utils.Sratocol;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.slider.LabelFormatter;
import com.google.android.material.slider.Slider;
import com.google.android.material.textview.MaterialTextView;

public class EasySensDistanceConfigureFragment extends Fragment {
    private static final String TAG = "SENSOR DISTANCE CONFIGURATION";

    private EasyViewModel viewModel;

    private byte CONFIGURING_SENSOR;
    private MaterialTextView tvCurrentDistance, tvCurrentDistanceHeader;
    private Slider sliderSensDistance;
    private MaterialButtonToggleGroup btnGroup;

    private enum SETUP_METHOD {
        AUTO, MANUAL
    }

    private SETUP_METHOD setupMethod = SETUP_METHOD.AUTO;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.configure_fragment_sens_distance, container, false);

        ConstraintLayout content    = view.findViewById(R.id.distance_layout_test);                  // Root layout
        tvCurrentDistanceHeader     = view.findViewById(R.id.config_tv_sens_distance_layout_header); // Header text of the layout
        TextView tvConfigType       = view.findViewById(R.id.config_tv_sens_distance_config_type);   // Config type text (auto/manual)
        tvCurrentDistance           = view.findViewById(R.id.config_tv_current_distance);            // Realtime sensor current distance
        sliderSensDistance          = view.findViewById(R.id.config_slider_sens_distance);           // Slider for setting manual distance
        btnGroup                    = view.findViewById(R.id.config_tg_sens_distance);               // Toggle button group (auto/manual)

        content.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING); // Enabling layout changing animations
        tvConfigType.setText("Choose a setup method");

        if (getArguments() != null) {
            CONFIGURING_SENSOR = getArguments().getByte(ConfigurationDialog.CONFIGURING_REGISTER);
        }
        final Bundle result = new Bundle();
        result.putByte(ConfigurationDialog.LAST_KEY, CONFIGURING_SENSOR);
        viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);
        if (CONFIGURING_SENSOR == Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE) {
            tvCurrentDistanceHeader.setText("Set distance for bottom sensor");
            viewModel.getBotSensorCurrentDistance()
                    .observe(requireActivity(), currentDistance -> {
                        tvCurrentDistance.setText(String.valueOf(currentDistance / 10));
                        if (setupMethod == SETUP_METHOD.AUTO) {
                            result.putInt(ConfigurationDialog.LAST_VALUE, currentDistance);
                            viewModel.setLastConfig(result);
                        }
                    });
        }
        else if (CONFIGURING_SENSOR == Sratocol.Register.TOP_SENS_TRIGGER_DISTANCE) {
            tvCurrentDistanceHeader.setText("Set distance for top sensor");
            viewModel.getTopSensorCurrentDistance()
                    .observe(requireActivity(), currentDistance -> {
                        tvCurrentDistance.setText(String.valueOf(currentDistance / 10));
                        if (setupMethod == SETUP_METHOD.AUTO) {
                            result.putInt(ConfigurationDialog.LAST_VALUE, currentDistance);
                            viewModel.setLastConfig(result);
                        }
                    });
        }

        btnGroup.setSelectionRequired(true);
        btnGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            if (isChecked) {
                if (checkedId == R.id.config_tg_btn_sens_distance_auto) {
                    sliderSensDistance.setVisibility(View.GONE);
                    tvCurrentDistance.setVisibility(View.VISIBLE);
                    tvConfigType.setText("Current distance");
                    setupMethod = SETUP_METHOD.AUTO;
                    Log.d("TAG", "SENS CONFIG MODE: AUTO");
                } else if (checkedId == R.id.config_tg_btn_sens_distance_manual) {
                    sliderSensDistance.setVisibility(View.VISIBLE);
                    tvCurrentDistance.setVisibility(View.GONE);
                    tvConfigType.setText("Manual setting of the trigger range");
                    setupMethod = SETUP_METHOD.MANUAL;
                    Log.d("TAG", "SENS CONFIG MODE: MANUAL");
                }
            }
        });

        LabelFormatter labelFormatter = value -> {
            double inch = value / 2.54 ;
            return String.format("cm: " + value + " | inch: %.1f", inch );
        };

        sliderSensDistance.setValueFrom(10);
        sliderSensDistance.setValueTo(300);
        sliderSensDistance.setValue(sliderSensDistance.getValueFrom());
        sliderSensDistance.setStepSize(10);
        sliderSensDistance.setLabelFormatter(labelFormatter);
        sliderSensDistance.addOnChangeListener((slider, value, fromUser) -> {
            if (setupMethod == SETUP_METHOD.MANUAL) {
                result.putByte(ConfigurationDialog.LAST_KEY, CONFIGURING_SENSOR);
                result.putInt(ConfigurationDialog.LAST_VALUE, (int) (value * 10));
            }
        });

        return view;
    }

    public static EasySensDistanceConfigureFragment newInstance(Bundle bundle) {
        EasySensDistanceConfigureFragment f = new EasySensDistanceConfigureFragment();
        Bundle b = new Bundle();
        b.putByte(ConfigurationDialog.CONFIGURING_REGISTER, bundle.getByte(ConfigurationDialog.CONFIGURING_REGISTER));
        f.setArguments(b);
        return f;
    }

    @Override
    public void onResume() {
        if (CONFIGURING_SENSOR == Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE) {
            getParentFragmentManager().setFragmentResultListener(ConfigurationDialog.CLOSE_OBSERVER, this, (requestKey, bundle) -> viewModel.getBotSensorCurrentDistance().removeObservers(requireActivity()));
        } else {
            getParentFragmentManager().setFragmentResultListener(ConfigurationDialog.CLOSE_OBSERVER, this, (requestKey, bundle) -> viewModel.getTopSensorCurrentDistance().removeObservers(requireActivity()));
        }
        Log.e(TAG, "onCreateView: REMOVED SENSOR OBSERVER");
        super.onResume();
    }

    @Override
    public void onPause() {
        getParentFragmentManager().clearFragmentResultListener(ConfigurationDialog.CLOSE_OBSERVER);
        super.onPause();
    }
}