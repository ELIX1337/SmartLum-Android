package com.smartlum.smartlum.ui.devices.torchere;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.button.MaterialButtonToggleGroup;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.smartlum.smartlum.R;
import com.smartlum.smartlum.profiles.torchere.data.TorchereData;
import com.smartlum.smartlum.ui.customViews.CustomBrightnessSlider;
import com.smartlum.smartlum.ui.customViews.CustomScrollView;
import com.smartlum.smartlum.viewmodels.TorchereViewModel;

import org.jetbrains.annotations.NotNull;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TorchereFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TorchereFragment extends Fragment {

    private TorchereViewModel viewModel;

    private CustomScrollView scrollView;
    private ColorPickerView colorPickerWheel;
    private CustomBrightnessSlider brightnessSlider;
    private MaterialButtonToggleGroup colorToggleGroup;
    private MaterialButton primaryColorBtn, secondaryColorBtn;
    private ConstraintLayout animationsHeader, directionHeader;
    private MaterialTextView animationName, directionName;
    private RadioGroup animRadioGroup, directionRadioGroup;
    private RadioButton animation1;
    private RadioButton animation2;
    private RadioButton animation3;
    private RadioButton animation4;
    private RadioButton animation5;
    private RadioButton animation6;
    private RadioButton direction1;
    private RadioButton direction2;
    private RadioButton direction3;
    private RadioButton direction4;
    private Slider animOnSlider;
    private Slider animOffSlider;
    private SwitchMaterial randomColorSwitch;
    private ShapeableImageView animationsHeaderArrow, directionHeaderArrow;

    private int primaryColor = Color.WHITE,
                secondaryColor = Color.WHITE;

    private enum WRITING_COLOR {
        PRIMARY, SECONDARY
    }
    private WRITING_COLOR writingColor = WRITING_COLOR.PRIMARY;

    public TorchereFragment() {}

    public static TorchereFragment newInstance(String param1, String param2) {
        return new TorchereFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_torchere, container, false);
        final ViewGroup layout = view.findViewById(R.id.torchere_layout);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);

        scrollView            = view.findViewById(R.id.torchere_scroll_view);
        colorPickerWheel      = view.findViewById(R.id.torchere_color_picker_wheel);
        brightnessSlider      = view.findViewById(R.id.torchere_slider_brightness);
        colorToggleGroup      = view.findViewById(R.id.torchere_color_toggle_group);
        primaryColorBtn       = view.findViewById(R.id.torchere_color_toggle_btn_primary);
        secondaryColorBtn     = view.findViewById(R.id.torchere_color_toggle_btn_secondary);
        animationsHeader      = view.findViewById(R.id.torchere_animations_header);
        directionHeader       = view.findViewById(R.id.torchere_animation_direction_header);
        animationName         = view.findViewById(R.id.torchere_animation_name);
        directionName         = view.findViewById(R.id.torchere_animation_direction_name);
        animRadioGroup        = view.findViewById(R.id.torchere_animation_radio_group);
        animation1            = view.findViewById(R.id.torchere_animation_1);
        animation2            = view.findViewById(R.id.torchere_animation_2);
        animation3            = view.findViewById(R.id.torchere_animation_3);
        animation4            = view.findViewById(R.id.torchere_animation_4);
        animation5            = view.findViewById(R.id.torchere_animation_5);
        animation6            = view.findViewById(R.id.torchere_animation_6);
        directionRadioGroup   = view.findViewById(R.id.torchere_animation__direction_radio_group);
        direction1            = view.findViewById(R.id.torchere_direction_1);
        direction2            = view.findViewById(R.id.torchere_direction_2);
        direction3            = view.findViewById(R.id.torchere_direction_3);
        direction4            = view.findViewById(R.id.torchere_direction_4);
        animationsHeaderArrow = view.findViewById(R.id.torchere_animations_header_arrow);
        directionHeaderArrow  = view.findViewById(R.id.torchere_animation_direction_header_arrow);
        animOnSlider          = view.findViewById(R.id.torchere_animation_on_speed);
        animOffSlider         = view.findViewById(R.id.torchere_animation_off_speed);
        randomColorSwitch     = view.findViewById(R.id.torchere_random_color_mode);

        colorPickerWheel.setVisibility(View.VISIBLE);
        colorPickerWheel.addOnColorChangedListener(selectedColor -> {
            if (writingColor == WRITING_COLOR.PRIMARY) {
                viewModel.setPrimaryColor(selectedColor);
                primaryColor = selectedColor;
            } else if (writingColor == WRITING_COLOR.SECONDARY) {
                viewModel.setSecondaryColor(selectedColor);
                secondaryColor = selectedColor;
            }
            brightnessSlider.setColor(selectedColor);
        });
        brightnessSlider.setColorWheel(colorPickerWheel);
        brightnessSlider.addOnChangeListener((slider, value, fromUser) -> {
            final int color = ((CustomBrightnessSlider) slider).getColorBrightness();
            if (writingColor == WRITING_COLOR.PRIMARY) {
                viewModel.setPrimaryColor(color);
                primaryColor = color;
            } else if (writingColor == WRITING_COLOR.SECONDARY) {
                viewModel.setSecondaryColor(color);
                secondaryColor = color;
            }
        });
        colorPickerWheel.setOnTouchListener(this::blockPageScroll);
        colorToggleGroup.addOnButtonCheckedListener(this::onColorToggleChange);
        animationsHeaderArrow.setOnClickListener(this::expandAndCollapseAnimation);
        directionHeaderArrow.setOnClickListener(this::expandAndCollapseDirection);
        animationsHeader.setOnClickListener(this::expandAndCollapseAnimation);
        directionHeader.setOnClickListener(this::expandAndCollapseDirection);
        animRadioGroup.setOnCheckedChangeListener(this::onAnimationModeCheckedChange);
        directionRadioGroup.setOnCheckedChangeListener(this::onDirectionCheckedChange);
        animOnSlider.addOnChangeListener(this::onSliderValueChange);
        animOffSlider.addOnChangeListener(this::onSliderValueChange);
        randomColorSwitch.setOnCheckedChangeListener(this::onSwitchRandomStateChange);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TorchereViewModel.class);
        primaryColorBtn.setChecked(true);
        colorToggleGroup.setSelectionRequired(true);
        viewModel.getPrimaryColor().observe(getViewLifecycleOwner(), data -> {
            if (writingColor == WRITING_COLOR.PRIMARY) {
                colorPickerWheel.setColor(data, false);
            }
            primaryColor = data;
        });
        viewModel.getSecondaryColor().observe(getViewLifecycleOwner(), data -> {
            if (writingColor == WRITING_COLOR.SECONDARY) {
                colorPickerWheel.setColor(data, false);
            }
            secondaryColor = data;
        });
        viewModel.getRandomColor().observe(getViewLifecycleOwner(), data -> randomColorSwitch.setChecked(data));
        viewModel.getAnimationMode().observe(getViewLifecycleOwner(), data -> {
            setAnimationName(data);
            switch (data) {
                case 1:
                    animation1.setChecked(true);
                    break;
                case 2:
                    animation2.setChecked(true);
                    break;
                case 3:
                    animation3.setChecked(true);
                    break;
                case 4:
                    animation4.setChecked(true);
                    break;
                case 5:
                    animation5.setChecked(true);
                    break;
                case 6:
                    animation6.setChecked(true);
                    break;
            }
        });
        viewModel.getAnimationDirection().observe(getViewLifecycleOwner(), data -> {
            setDirectionName(data);
            switch (data) {
                case 1:
                    direction1.setChecked(true);
                    break;
                case 2:
                    direction2.setChecked(true);
                    break;
                case 3:
                    direction3.setChecked(true);
                    break;
                case 4:
                    direction4.setChecked(true);
                    break;
            }
        });
        viewModel.getAnimationOnSpeed().observe(getViewLifecycleOwner(), data -> animOnSlider.setValue(data));
    }

    private boolean blockPageScroll(View v, @NotNull MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                scrollView.setEnableScrolling(false);
                break;
            case MotionEvent.ACTION_UP:
                scrollView.setEnableScrolling(true);
                break;
        }
        return false;
    }

    private void expandAndCollapseAnimation(View v) {
        if (animRadioGroup.getVisibility() == View.VISIBLE) {
            animRadioGroup.setVisibility(View.GONE);
            animationsHeaderArrow.animate().rotation(90);
        } else {
            animRadioGroup.setVisibility(View.VISIBLE);
            animationsHeaderArrow.animate().rotation(0);
        }
    }

    private void expandAndCollapseDirection(View v) {
        if (directionRadioGroup.getVisibility() == View.VISIBLE) {
            directionRadioGroup.setVisibility(View.GONE);
            directionHeaderArrow.animate().rotation(90);
        } else {
            directionRadioGroup.setVisibility(View.VISIBLE);
            directionHeaderArrow.animate().rotation(0);
        }
    }

    private void onColorToggleChange(MaterialButtonToggleGroup group, int checkedId, boolean isChecked) {
        if (isChecked) {
            if (checkedId == primaryColorBtn.getId()) {
                writingColor = WRITING_COLOR.PRIMARY;
                colorPickerWheel.setColor(primaryColor, false);
                brightnessSlider.setColor(primaryColor);
            }
            else if (checkedId == secondaryColorBtn.getId()) {
                writingColor = WRITING_COLOR.SECONDARY;
                colorPickerWheel.setColor(secondaryColor, false);
                brightnessSlider.setColor(secondaryColor);
            }
        }
    }

    private void onAnimationModeCheckedChange(RadioGroup group, int checkedId) {
        if (checkedId == animation1.getId()) {
            viewModel.setAnimationMode(1);
            setAnimationName(1);
        }
        else if (checkedId == animation2.getId()) {
            viewModel.setAnimationMode(2);
            setAnimationName(2);
        }
        else if (checkedId == animation3.getId()) {
            viewModel.setAnimationMode(3);
            setAnimationName(3);
        }
        else if (checkedId == animation4.getId()) {
            viewModel.setAnimationMode(4);
            setAnimationName(4);
        }
        else if (checkedId == animation5.getId()) {
            viewModel.setAnimationMode(5);
            setAnimationName(5);
        }
        else if (checkedId == animation6.getId()) {
            viewModel.setAnimationMode(6);
            setAnimationName(6);
        }
    }

    private void onDirectionCheckedChange(RadioGroup group, int checkedId) {
        if (checkedId == direction1.getId()) {
            viewModel.setAnimationDirection(1);
            setDirectionName(1);
        }
        else if (checkedId == direction2.getId()) {
            viewModel.setAnimationDirection(2);
            setDirectionName(2);
        }
        else if (checkedId == direction3.getId()) {
            viewModel.setAnimationDirection(3);
            setDirectionName(3);
        }
        else if (checkedId == direction4.getId()) {
            viewModel.setAnimationDirection(4);
            setDirectionName(4);
        }
    }

    private void setAnimationName(final int name) {
        animationName.setText(TorchereData.animationModes.get(name));
    }

    private void setDirectionName(final int name) {
        directionName.setText(TorchereData.animationDirections.get(name));
    }

    private void onSliderValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        if (fromUser) {
            if (slider.equals(animOnSlider)) {
                viewModel.setAnimationOnSpeed(Math.round(value));
            }
            else if (slider.equals(animOffSlider)) {
                //viewModel.setAnimationOffSpeed(Math.round(value));
            }
        }
    }

    private void onSwitchRandomStateChange(CompoundButton v, boolean isChecked) {
        viewModel.setRandomColor(isChecked);
    }
}