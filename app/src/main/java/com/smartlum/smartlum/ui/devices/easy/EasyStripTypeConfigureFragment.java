package com.smartlum.smartlum.ui.devices.easy;

import android.animation.ArgbEvaluator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.profiles.easy.data.EasyData;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.imageview.ShapeableImageView;

public class EasyStripTypeConfigureFragment extends Fragment {

    private byte CONFIGURING_REGISTER;

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.configure_fragment_strip_type, container, false);
        EasyViewModel viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);
        view.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        if (getArguments() != null) {
            CONFIGURING_REGISTER = getArguments().getByte(ConfigurationDialog.CONFIGURING_REGISTER);
        }

        ShapeableImageView image = view.findViewById(R.id.image_test);
        FrameLayout content = view.findViewById(R.id.frame_strip_type_config);

        int colorFrom = getResources().getColor(R.color.colorGradientEnd, null);
        int colorTo = getResources().getColor(R.color.colorGradientStart, null);
        ValueAnimator colorAnimation = ValueAnimator.ofObject(new ArgbEvaluator(), colorFrom, colorTo);
        colorAnimation.setDuration(250);
        colorAnimation.addUpdateListener(animator -> image.setColorFilter((int) animator.getAnimatedValue(), PorterDuff.Mode.SRC_ATOP));
        colorAnimation.setRepeatMode(ValueAnimator.REVERSE);
        colorAnimation.setRepeatCount(-1);

        MaterialButtonToggleGroup btnGroup = view.findViewById(R.id.tg_configure_strip_type);
        btnGroup.setSelectionRequired(true);
        btnGroup.addOnButtonCheckedListener((group, checkedId, isChecked) -> {
            final Bundle result = new Bundle();
            result.putByte(ConfigurationDialog.LAST_KEY, CONFIGURING_REGISTER);
            content.setVisibility(View.VISIBLE);
            if (isChecked) {
                if (checkedId == R.id.tg_btn_rgb) {
                    colorAnimation.start();
                    result.putInt(ConfigurationDialog.LAST_VALUE, EasyData.STRIP_TYPE_RGB);
                } else if (checkedId == R.id.tg_btn_default) {
                    colorAnimation.end();
                    image.setColorFilter(Color.WHITE);
                    result.putInt(ConfigurationDialog.LAST_VALUE, EasyData.STRIP_TYPE_DEFAULT);
                }
                viewModel.setLastConfig(result);
            }
        });
        return view;
    }

    public static EasyStripTypeConfigureFragment newInstance(Bundle bundle) {
        EasyStripTypeConfigureFragment f = new EasyStripTypeConfigureFragment();
        Bundle b = new Bundle();
        b.putByte(ConfigurationDialog.CONFIGURING_REGISTER, bundle.getByte(ConfigurationDialog.CONFIGURING_REGISTER));
        f.setArguments(b);
        return f;
    }
}