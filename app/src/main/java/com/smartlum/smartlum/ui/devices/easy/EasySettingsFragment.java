package com.smartlum.smartlum.ui.devices.easy;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.profiles.easy.data.EasyData;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;

import org.jetbrains.annotations.NotNull;

public class EasySettingsFragment extends Fragment {

    private EasyViewModel viewModel;

    public EasySettingsFragment() { }

    public static @NotNull EasySettingsFragment newInstance(String param1, String param2) {
        return new EasySettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_easy_settings, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        final RadioGroup stripTypeGroup = view.findViewById(R.id.easy_strip_type);
        final MaterialTextView topSensorTriggerDistance = view.findViewById(R.id.easy_top_sensor_trigger_distance);
        final MaterialTextView botSensorTriggerDistance = view.findViewById(R.id.easy_bot_sensor_trigger_distance);
        final MaterialTextView stepsCount = view.findViewById(R.id.easy_number_of_steps);
        stripTypeGroup.setOnCheckedChangeListener((group, checkedId) -> {
            if (checkedId == R.id.easy_strip_type_default) {
                viewModel.setStripType(EasyData.STRIP_TYPE_DEFAULT);
            } else if (checkedId == R.id.easy_strip_type_rgb) {
                viewModel.setStripType(EasyData.STRIP_TYPE_RGB);
            }
        });

        viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);

        viewModel.getStripType().observe(requireActivity(), type -> {
            switch (type) {
                case EasyData.STRIP_TYPE_DEFAULT:
                    stripTypeGroup.check(R.id.easy_strip_type_default);
                    break;
                case EasyData.STRIP_TYPE_RGB:
                    stripTypeGroup.check(R.id.easy_strip_type_rgb);
                    break;
            }
        });

        viewModel.getTopSensorTriggerDistance().observe(requireActivity(), distance -> topSensorTriggerDistance.setText(String.valueOf(distance)));
        viewModel.getBotSensorTriggerDistance().observe(requireActivity(), distance -> botSensorTriggerDistance.setText(String.valueOf(distance)));
        viewModel.getStepsCount().observe(requireActivity(), count -> stepsCount.setText(String.valueOf(count)));

        final MaterialButton btnReset = view.findViewById(R.id.easy_btn_reset);
        btnReset.setOnClickListener(v -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
            builder.setPositiveButton("Confirm", (dialog, which) -> {
                viewModel.setFactorySettings();
                requireActivity().finish();
            });
            builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
            builder.setMessage("Device will be reset to factory setting. Are u sure?");
            AlertDialog dialog = builder.create();
            dialog.show();
        });
    }

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_device_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.easySettingsFragment) {
            requireActivity().onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}