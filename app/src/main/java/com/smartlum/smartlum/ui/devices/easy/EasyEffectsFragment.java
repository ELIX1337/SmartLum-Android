package com.smartlum.smartlum.ui.devices.easy;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.Navigation;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.profiles.easy.data.EasyData;
import com.smartlum.smartlum.ui.customViews.CustomBrightnessSlider;
import com.smartlum.smartlum.ui.customViews.CustomScrollView;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.flask.colorpicker.ColorPickerView;
import com.google.android.material.imageview.ShapeableImageView;
import com.google.android.material.slider.Slider;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textview.MaterialTextView;
import com.google.android.material.timepicker.MaterialTimePicker;
import com.google.android.material.timepicker.TimeFormat;

import org.jetbrains.annotations.NotNull;

public class EasyEffectsFragment extends Fragment {

    private EasyViewModel viewModel;

    private CustomScrollView scrollView;
    private ColorPickerView colorPickerWheel;
    private CustomBrightnessSlider brightnessSlider;
    private ConstraintLayout animationsHeader;
    private MaterialTextView animationName;
    private RadioGroup animRadioGroup;
    private RadioButton animation1;
    private RadioButton animation2;
    private RadioButton animation3;
    private Slider animOnSlider;
    private Slider animOffSlider;
    private SwitchMaterial randomColorSwitch;
    private RadioGroup randomColorRadioGroup;
    private RadioButton randomColorSingle;
    private RadioButton randomColorMultiple;
    private SwitchMaterial adaptiveBrightnessSwitch;
    private SwitchMaterial dayNightModeSwitch;
    private RadioGroup dayNightModeRadioGroup;
    private RadioButton dayNightModeTime;
    private RadioButton dayNightModeLightness;
    private ShapeableImageView dayNightModeTimeSettings;
    private ShapeableImageView animationsHeaderArrow;

    private int timeFrom;
    private int timeTo;

    public EasyEffectsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment EasyEffectsFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static EasyEffectsFragment newInstance(String param1, String param2) {
        return new EasyEffectsFragment();
    }

    /**
     * Преопределяемые ниже методы:
     * onCreate, onCreateView, onViewCreated, onStart, onStop
     * описываются жизненным циклом (lifecycle) фрагмента и активити
     * они вызываются при создании, уничтожении, показе, скрытии фрагмента/активити
     * подробнее лучше почитать в интернете
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        final View view = inflater.inflate(R.layout.fragment_easy_effects, container, false);
        final ViewGroup layout = view.findViewById(R.id.easy_layout);
        layout.getLayoutTransition().enableTransitionType(LayoutTransition.CHANGING);
        // Инициализируем UI

        scrollView                  = view.findViewById(R.id.easy_scroll_view);
        colorPickerWheel            = view.findViewById(R.id.color_picker_wheel);
        brightnessSlider            = view.findViewById(R.id.easy_slider_brightness);
        animationsHeader            = view.findViewById(R.id.easy_animations_header);
        animationName               = view.findViewById(R.id.easy_animation_name);
        animRadioGroup              = view.findViewById(R.id.easy_animation_radio_group);
        animation1                  = view.findViewById(R.id.easy_animation_1);
        animation2                  = view.findViewById(R.id.easy_animation_2);
        animation3                  = view.findViewById(R.id.easy_animation_3);
        animationsHeaderArrow       = view.findViewById(R.id.easy_animations_header_arrow);
        animOnSlider                = view.findViewById(R.id.easy_animation_on_speed);
        animOffSlider               = view.findViewById(R.id.easy_animation_off_speed);
        randomColorSwitch           = view.findViewById(R.id.easy_random_color_mode);
        randomColorRadioGroup       = view.findViewById(R.id.easy_random_color_mode_radio_group);
        randomColorSingle           = view.findViewById(R.id.easy_random_color_single);
        randomColorMultiple         = view.findViewById(R.id.easy_random_color_multiple);
        adaptiveBrightnessSwitch    = view.findViewById(R.id.easy_adaptive_brightness);
        dayNightModeSwitch          = view.findViewById(R.id.easy_day_night_mode);
        dayNightModeRadioGroup      = view.findViewById(R.id.easy_day_night_mode_radio_group);
        dayNightModeTime            = view.findViewById(R.id.easy_day_night_mode_time);
        dayNightModeLightness       = view.findViewById(R.id.easy_day_night_mode_lightness);
        dayNightModeTimeSettings    = view.findViewById(R.id.easy_day_night_mode_time_settings);

        colorPickerWheel.setVisibility(View.VISIBLE);
        colorPickerWheel.addOnColorChangedListener(selectedColor -> {
            viewModel.setColor(selectedColor);
            brightnessSlider.setColor(selectedColor);
        });
        brightnessSlider.setColorWheel(colorPickerWheel);
        brightnessSlider.addOnChangeListener((slider, value, fromUser) -> viewModel.setColor(((CustomBrightnessSlider) slider).getColorBrightness()));
        colorPickerWheel.setOnTouchListener(this::blockPageScroll);
        animationsHeaderArrow.setOnClickListener(this::expandAndCollapse);
        animationsHeader.setOnClickListener(this::expandAndCollapse);
        animRadioGroup.setOnCheckedChangeListener(this::onAnimationModeCheckedChange);
        animOnSlider.addOnChangeListener(this::onSliderValueChange);
        animOffSlider.addOnChangeListener(this::onSliderValueChange);
        randomColorSwitch.setOnCheckedChangeListener(this::onSwitchRandomStateChange);
        randomColorRadioGroup.setOnCheckedChangeListener(this::onRandomColorModeCheckedChange);
        adaptiveBrightnessSwitch.setOnCheckedChangeListener(this::onAdaptiveBrightnessModeStateChange);
        dayNightModeSwitch.setOnCheckedChangeListener(this::onDayNightModeStateChange);
        dayNightModeRadioGroup.setOnCheckedChangeListener(this::onDayNightModeCheckedChange);
        dayNightModeTimeSettings.setOnClickListener(this::setDayNightModeTime);

        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Модель
        viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);
        viewModel.requestSettings();

        viewModel.getStripType().observe(getViewLifecycleOwner(), type -> {
            final View colorWheelContainer = view.findViewById(R.id.color_picker_wheel_container);
            if (type == EasyData.STRIP_TYPE_DEFAULT) {
                colorWheelContainer.setVisibility(View.GONE);
                colorPickerWheel.setColor(R.color.teal_200,false);
            } else if (type == EasyData.STRIP_TYPE_RGB) {
                colorWheelContainer.setVisibility(View.VISIBLE);
            }
        });

        viewModel.getColor().observe(getViewLifecycleOwner(), data -> {
            colorPickerWheel.setColor(data, false);
            brightnessSlider.setColor(data);
            Log.d("TAG", "observeColor: " + data);
        });
        viewModel.getAnimationMode().observe(getViewLifecycleOwner(), data -> {
            Log.d("TAG", "observeAnimation: " + data);
            setAnimationName(data);
            switch (data) {
                case EasyData.ANIMATION_STEP_BY_STEP:
                    animation1.setChecked(true);
                    break;
                case EasyData.ANIMATION_SHARP:
                    animation2.setChecked(true);
                    break;
                case EasyData.ANIMATION_WAVE:
                    animation3.setChecked(true);
                    break;
            }
        });
        viewModel.getAnimationOnSpeed().observe(getViewLifecycleOwner(), data -> animOnSlider.setValue(data));
        viewModel.getAnimationOffSpeed().observe(getViewLifecycleOwner(), data -> animOffSlider.setValue(data));
        viewModel.getRandomColorMode().observe(getViewLifecycleOwner(), data -> {
            switch (data) {
                case EasyData.RANDOM_COLOR_MODE_OFF:
                    randomColorSwitch.setChecked(false);
                    break;
                case EasyData.RANDOM_COLOR_MODE_SINGLE:
                    randomColorRadioGroup.check(randomColorSingle.getId());
                    randomColorSwitch.setChecked(true);
                    break;
                case EasyData.RANDOM_COLOR_MODE_MULTIPLE:
                    randomColorRadioGroup.check(randomColorMultiple.getId());
                    randomColorSwitch.setChecked(true);
                    break;
            }
        });
        viewModel.getAdaptiveBrightnessMode().observe(requireActivity(), data -> adaptiveBrightnessSwitch.setChecked(data));
        viewModel.getDayNightMode().observe(requireActivity(), data -> {
            switch (data) {
                case EasyData.DAY_NIGHT_MODE_OFF:
                    dayNightModeSwitch.setChecked(false);
                    break;
                case EasyData.DAY_NIGHT_MODE_BY_TIME:
                    dayNightModeRadioGroup.check(dayNightModeTime.getId());
                    dayNightModeSwitch.setChecked(true);
                    break;
                case EasyData.DAY_NIGHT_MODE_BY_LIGHTNESS:
                    dayNightModeRadioGroup.check(dayNightModeLightness.getId());
                    dayNightModeSwitch.setChecked(true);
                    break;
            }
        });
        // Инициализация списка устройств
    }

    private void setAnimationName(Integer name) {
        switch (name) {
            case EasyData.ANIMATION_STEP_BY_STEP:
                animationName.setText("Step by step");
                break;
            case EasyData.ANIMATION_SHARP:
                animationName.setText("Sharp");
                break;
            case EasyData.ANIMATION_WAVE:
                animationName.setText("Wave");
                break;
        }
    }

    private void setDayNightModeTime(View view) {
        MaterialTimePicker timePickerFrom = new MaterialTimePicker
                .Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("ON time")
                .build();

        MaterialTimePicker timePickerTo = new MaterialTimePicker
                .Builder()
                .setTimeFormat(TimeFormat.CLOCK_24H)
                .setTitleText("OFF time")
                .build();

        timePickerFrom.show(getChildFragmentManager(), "dayNight_time_from");
        timePickerFrom.addOnPositiveButtonClickListener(v -> {
            timeFrom = (timePickerFrom.getHour() * 60) + timePickerFrom.getMinute();
            timePickerTo.show(getChildFragmentManager(), "dayNight_time_to");
        });
        timePickerTo.addOnPositiveButtonClickListener(v -> {
            timeTo = (timePickerTo.getHour() * 60) + timePickerTo.getMinute();
            viewModel.setDayNightModeOnTime(timeFrom);
            viewModel.setDayNightModeOffTime(timeTo);
        });
    }

    private void expandAndCollapse(View v) {
        if (animRadioGroup.getVisibility() == View.VISIBLE) {
            animRadioGroup.setVisibility(View.GONE);
            animationsHeaderArrow.animate().rotation(90);
        } else {
            animRadioGroup.setVisibility(View.VISIBLE);
            animationsHeaderArrow.animate().rotation(0);
        }
    }

    // Animation radio group listener
    private void onAnimationModeCheckedChange(RadioGroup group, int checkedId) {
        if (checkedId == animation1.getId()) {
            viewModel.setAnimationMode(EasyData.ANIMATION_STEP_BY_STEP);
            setAnimationName(EasyData.ANIMATION_STEP_BY_STEP);
        }
        else if (checkedId == animation2.getId()) {
            viewModel.setAnimationMode(EasyData.ANIMATION_SHARP);
            setAnimationName(EasyData.ANIMATION_SHARP);
        }
        else if (checkedId == animation3.getId()) {
            viewModel.setAnimationMode(EasyData.ANIMATION_WAVE);
            setAnimationName(EasyData.ANIMATION_WAVE);
        }
    }

    private void onSliderValueChange(@NonNull Slider slider, float value, boolean fromUser) {
        if (fromUser) {
            if (slider.equals(animOnSlider)) {
                viewModel.setAnimationOnSpeed(Math.round(value));
            }
            else if (slider.equals(animOffSlider)) {
                viewModel.setAnimationOffSpeed(Math.round(value));
            }
        }
    }

    private void onSwitchRandomStateChange(CompoundButton v, boolean isChecked) {
        viewModel.activateRandomColor(isChecked);
        enableRadioGroup(randomColorRadioGroup, isChecked);
    }

    private void onRandomColorModeCheckedChange(RadioGroup group, int checkedId) {
        if (checkedId == randomColorSingle.getId()) {
            viewModel.setRandomColorMode(EasyData.RANDOM_COLOR_MODE_SINGLE);
        }
        else if (checkedId == randomColorMultiple.getId()) {
            viewModel.setRandomColorMode(EasyData.RANDOM_COLOR_MODE_MULTIPLE);
        }
    }

    private void onAdaptiveBrightnessModeStateChange(CompoundButton v, boolean isChecked) {
        viewModel.setAdaptiveMode(isChecked);
    }

    private void onDayNightModeStateChange(CompoundButton v, boolean isChecked) {
        viewModel.activateDayNightMode(isChecked);
        enableRadioGroup(dayNightModeRadioGroup, isChecked);
        dayNightModeTimeSettings.setEnabled(isChecked);
    }

    private void onDayNightModeCheckedChange(RadioGroup group, int checkedId) {
        if (checkedId == dayNightModeTime.getId()) {
            viewModel.setDayNightMode(EasyData.DAY_NIGHT_MODE_BY_TIME);
            dayNightModeTimeSettings.setVisibility(View.VISIBLE);
        }
        else if (checkedId == dayNightModeLightness.getId()) {
            viewModel.setDayNightMode(EasyData.DAY_NIGHT_MODE_BY_LIGHTNESS);
            dayNightModeTimeSettings.setVisibility(View.GONE);
        }
    }

    private void enableRadioGroup(@NotNull RadioGroup radioGroup, boolean isEnabled) {
        radioGroup.setVisibility(isEnabled ? View.VISIBLE : View.GONE);
        for (int i = 0; i < radioGroup.getChildCount(); i++) {
            radioGroup.getChildAt(i).setEnabled(isEnabled);
        }
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

    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {
        requireActivity().getMenuInflater().inflate(R.menu.menu_device_settings, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == R.id.easySettingsFragment) {
            Navigation.findNavController(requireActivity(), R.id.nav_host_fragment_container).navigate(R.id.easySettingsFragment);
        }
        return super.onOptionsItemSelected(item);
    }
}
