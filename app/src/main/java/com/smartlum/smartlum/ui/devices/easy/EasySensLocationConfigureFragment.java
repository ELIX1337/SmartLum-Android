package com.smartlum.smartlum.ui.devices.easy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.Group;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.profiles.easy.data.EasyData;
import com.smartlum.smartlum.utils.Sratocol;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;

public class EasySensLocationConfigureFragment extends Fragment {
    private static final String TAG = "SENSOR LOCATION CONFIGURATION";

    private byte CONFIGURING_SENSOR;
    private MaterialButtonToggleGroup btnGroup;
    private ImageView imageSensLeft, imageSensRight, imageSensCenter;
    private ValueAnimator sensAnimation;

    private EasyViewModel viewModel;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.configure_fragment_sens_location, container, false);

        btnGroup          = view.findViewById(R.id.config_tg_sens_location);
        Group bottomGroup = view.findViewById(R.id.config_group_bottom_sensors);
        Group topGroup    = view.findViewById(R.id.config_group_top_sensor);

        viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);
        if (getArguments() != null) {
            CONFIGURING_SENSOR = getArguments().getByte(ConfigurationDialog.CONFIGURING_REGISTER);
        }

        final TextView header = view.findViewById(R.id.config_tv_sens_location_layout_header);

        if (CONFIGURING_SENSOR == Sratocol.Register.BOT_SENS_DIRECTION) {
            topGroup.setVisibility(View.INVISIBLE);
            imageSensLeft    = view.findViewById(R.id.config_image_sens_location_bot_left);
            imageSensRight   = view.findViewById(R.id.config_image_sens_location_bot_right);
            imageSensCenter  = view.findViewById(R.id.config_image_sens_location_bot_center);
            header.setText("R.string.config_specify_the_location_of_the_bottom_sensor");
        } else if (CONFIGURING_SENSOR == Sratocol.Register.TOP_SENS_DIRECTION) {
            bottomGroup.setVisibility(View.INVISIBLE);
            imageSensLeft    = view.findViewById(R.id.config_image_sens_location_top_left);
            imageSensRight   = view.findViewById(R.id.config_image_sens_location_top_right);
            imageSensCenter  = view.findViewById(R.id.config_image_sens_location_top_center);
            header.setText("R.string.config_specify_the_location_of_the_bottom_sensor");
        }
        imageSensLeft.setOnClickListener(this::onSensClick);
        imageSensRight.setOnClickListener(this::onSensClick);
        imageSensCenter.setOnClickListener(this::onSensClick);

        final int colorFrom = getResources().getColor(R.color.sl_light, null);
        final int colorTo = getResources().getColor(R.color.sl_secondary, null);

        sensAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        sensAnimation.setDuration(100);
        sensAnimation.setRepeatMode(ValueAnimator.REVERSE);
        sensAnimation.setRepeatCount(ValueAnimator.INFINITE);

        btnGroup.setSelectionRequired(true);
        btnGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            final Bundle result = new Bundle();
            result.putByte(ConfigurationDialog.LAST_KEY, CONFIGURING_SENSOR);
            if (isChecked) {
                if (checkedId == R.id.config_tg_btn_sens_location_left) {
                    result.putInt(ConfigurationDialog.LAST_VALUE, EasyData.SENSOR_LOCATION_LEFT);
                    onSensClick(imageSensLeft);
                }
                else if (checkedId == R.id.config_tg_btn_sens_location_right) {
                    result.putInt(ConfigurationDialog.LAST_VALUE, EasyData.SENSOR_LOCATION_RIGHT);
                    onSensClick(imageSensRight);
                }
                else if (checkedId == R.id.config_tg_btn_sens_location_center) {
                    result.putInt(ConfigurationDialog.LAST_VALUE, EasyData.SENSOR_LOCATION_CENTER);
                    onSensClick(imageSensCenter);
                }
                viewModel.setLastConfig(result);
            }
        });

        return view;
    }

    private void onSensClick(View v) {
        if (v.equals(imageSensLeft)) {
            btnGroup.check(R.id.config_tg_btn_sens_location_left);
            Log.d(TAG,  CONFIGURING_SENSOR + " SENS TYPE: LEFT");
        }
        else if (v.equals(imageSensRight)) {
            btnGroup.check(R.id.config_tg_btn_sens_location_right);
            Log.d(TAG, CONFIGURING_SENSOR + " SENS TYPE: RIGHT");
        }
        else if (v.equals(imageSensCenter)) {
            btnGroup.check(R.id.config_tg_btn_sens_location_center);
            Log.d(TAG, CONFIGURING_SENSOR + " SENS TYPE: CENTER");
        }
        sensAnimation.end();
        sensAnimation.removeAllUpdateListeners();
        sensAnimation.addUpdateListener(animator -> ((ImageView)v).setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.SRC_ATOP));
        sensAnimation.start();
    }

    public static EasySensLocationConfigureFragment newInstance(Bundle bundle) {
        EasySensLocationConfigureFragment f = new EasySensLocationConfigureFragment();
        Bundle b = new Bundle();
        b.putByte(ConfigurationDialog.CONFIGURING_REGISTER, bundle.getByte(ConfigurationDialog.CONFIGURING_REGISTER));
        f.setArguments(b);
        return f;
    }

}