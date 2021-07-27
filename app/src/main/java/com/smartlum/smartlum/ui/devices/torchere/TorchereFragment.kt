package com.smartlum.smartlum.ui.devices.torchere

import android.animation.LayoutTransition
import android.annotation.SuppressLint
import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.CompoundButton
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RadioGroup
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.flask.colorpicker.ColorPickerView
import com.google.android.material.button.MaterialButton
import com.google.android.material.button.MaterialButtonToggleGroup
import com.google.android.material.card.MaterialCardView
import com.google.android.material.imageview.ShapeableImageView
import com.google.android.material.slider.Slider
import com.google.android.material.switchmaterial.SwitchMaterial
import com.google.android.material.textview.MaterialTextView
import com.smartlum.smartlum.R
import com.smartlum.smartlum.profiles.torchere.data.TorchereData
import com.smartlum.smartlum.ui.customViews.CustomBrightnessSlider
import com.smartlum.smartlum.ui.customViews.CustomScrollView
import com.smartlum.smartlum.viewmodels.TorchereViewModel
import java.util.*
import kotlin.math.roundToInt

class TorchereFragment : Fragment() {
    private var viewModel: TorchereViewModel? = null
    private var scrollView: CustomScrollView? = null
    private var randomColorCard: MaterialCardView? = null
    private var animationSpeedCard: MaterialCardView? = null
    private var animationDirectionCard: MaterialCardView? = null
    private var animationStepCard: MaterialCardView? = null
    private var colorPickerCard: MaterialCardView? = null
    private var brightnessCard: LinearLayout? = null
    private var colorPickerWheel: ColorPickerView? = null
    private var brightnessSlider: CustomBrightnessSlider? = null
    private var colorToggleGroup: MaterialButtonToggleGroup? = null
    private var primaryColorBtn: MaterialButton? = null
    private var secondaryColorBtn: MaterialButton? = null
    private var animationsHeader: ConstraintLayout? = null
    private var directionHeader: ConstraintLayout? = null
    private var animationName: MaterialTextView? = null
    private var directionName: MaterialTextView? = null
    private var animRadioGroup: RadioGroup? = null
    private var directionRadioGroup: RadioGroup? = null
    private var animation1: RadioButton? = null
    private var animation2: RadioButton? = null
    private var animation3: RadioButton? = null
    private var animation4: RadioButton? = null
    private var animation5: RadioButton? = null
    private var animation6: RadioButton? = null
    private var direction1: RadioButton? = null
    private var direction2: RadioButton? = null
    private var direction3: RadioButton? = null
    private var direction4: RadioButton? = null
    private var animOnSlider: Slider? = null
    private var animStepSlider: Slider? = null
    private var randomColorSwitch: SwitchMaterial? = null
    private var animationsHeaderArrow: ShapeableImageView? = null
    private var directionHeaderArrow: ShapeableImageView? = null
    private val hidingCells = ArrayList<View?>()
    private val allCells = ArrayList<View?>()
    private var primaryColor = Color.WHITE
    private var secondaryColor = Color.WHITE

    private enum class WritingColor {
        PRIMARY, SECONDARY
    }

    private var writingColor = WritingColor.PRIMARY
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_torchere, container, false)
        val layout = view.findViewById<ViewGroup>(R.id.torchere_layout)
        layout.layoutTransition.enableTransitionType(LayoutTransition.CHANGING)
        randomColorCard = view.findViewById(R.id.torchere_random_color_mode_container)
        animationSpeedCard = view.findViewById(R.id.torchere_animation_speed_container)
        animationDirectionCard = view.findViewById(R.id.torchere_animation_direction_container)
        animationStepCard = view.findViewById(R.id.torchere_animation_step_container)
        colorPickerCard = view.findViewById(R.id.color_picker_wheel_container)
        brightnessCard = view.findViewById(R.id.torchere_brightness_container)
        scrollView = view.findViewById(R.id.torchere_scroll_view)
        colorPickerWheel = view.findViewById(R.id.torchere_color_picker_wheel)
        brightnessSlider = view.findViewById(R.id.torchere_slider_brightness)
        colorToggleGroup = view.findViewById(R.id.torchere_color_toggle_group)
        primaryColorBtn = view.findViewById(R.id.torchere_color_toggle_btn_primary)
        secondaryColorBtn = view.findViewById(R.id.torchere_color_toggle_btn_secondary)
        animationsHeader = view.findViewById(R.id.torchere_animations_header)
        directionHeader = view.findViewById(R.id.torchere_animation_direction_header)
        animationName = view.findViewById(R.id.torchere_animation_name)
        directionName = view.findViewById(R.id.torchere_animation_direction_name)
        animRadioGroup = view.findViewById(R.id.torchere_animation_radio_group)
        animation1 = view.findViewById(R.id.torchere_animation_1)
        animation2 = view.findViewById(R.id.torchere_animation_2)
        animation3 = view.findViewById(R.id.torchere_animation_3)
        animation4 = view.findViewById(R.id.torchere_animation_4)
        animation5 = view.findViewById(R.id.torchere_animation_5)
        animation6 = view.findViewById(R.id.torchere_animation_6)
        directionRadioGroup = view.findViewById(R.id.torchere_animation__direction_radio_group)
        direction1 = view.findViewById(R.id.torchere_direction_1)
        direction2 = view.findViewById(R.id.torchere_direction_2)
        direction3 = view.findViewById(R.id.torchere_direction_3)
        direction4 = view.findViewById(R.id.torchere_direction_4)
        animationsHeaderArrow = view.findViewById(R.id.torchere_animations_header_arrow)
        directionHeaderArrow = view.findViewById(R.id.torchere_animation_direction_header_arrow)
        animOnSlider = view.findViewById(R.id.torchere_animation_on_speed)
        animStepSlider = view.findViewById(R.id.torchere_animation_step_slider)
        randomColorSwitch = view.findViewById(R.id.torchere_random_color_mode)
        animStepSlider?.valueFrom = 0f
        animStepSlider?.valueTo = 10f
        allCells.add(randomColorCard)
        allCells.add(animationSpeedCard)
        allCells.add(animationDirectionCard)
        allCells.add(animationStepCard)
        allCells.add(primaryColorBtn)
        allCells.add(secondaryColorBtn)
        allCells.add(colorPickerCard)
        allCells.add(brightnessCard)
        colorPickerWheel?.visibility = View.VISIBLE
        colorPickerWheel?.addOnColorChangedListener { selectedColor: Int ->
            if (writingColor == WritingColor.PRIMARY) {
                viewModel!!.setPrimaryColor(selectedColor)
                primaryColor = selectedColor
            } else if (writingColor == WritingColor.SECONDARY) {
                viewModel!!.setSecondaryColor(selectedColor)
                secondaryColor = selectedColor
            }
            brightnessSlider?.setColor(selectedColor)
        }
        brightnessSlider?.setColorWheel(colorPickerWheel)
        brightnessSlider?.addOnChangeListener { slider: Slider, _: Float, _: Boolean ->
            val color = (slider as CustomBrightnessSlider).colorBrightness
            if (writingColor == WritingColor.PRIMARY) {
                viewModel?.setPrimaryColor(color)
                primaryColor = color
            } else if (writingColor == WritingColor.SECONDARY) {
                viewModel?.setSecondaryColor(color)
                secondaryColor = color
            }
        }
        colorPickerWheel?.setOnTouchListener { _: View, event: MotionEvent ->
            blockPageScroll(event) }
        colorToggleGroup?.addOnButtonCheckedListener { _: MaterialButtonToggleGroup, checkedId: Int, isChecked: Boolean ->
            onColorToggleChange(checkedId, isChecked) }
        animationsHeaderArrow?.setOnClickListener { expandAndCollapseAnimation() }
        directionHeaderArrow?.setOnClickListener { expandAndCollapseDirection() }
        animationsHeader?.setOnClickListener { expandAndCollapseAnimation() }
        directionHeader?.setOnClickListener { expandAndCollapseDirection() }
        animRadioGroup?.setOnCheckedChangeListener{ _: RadioGroup, checkedId: Int ->
            onAnimationModeCheckedChange(checkedId) }
        directionRadioGroup?.setOnCheckedChangeListener{ _: RadioGroup, checkedId: Int ->
            onDirectionCheckedChange(checkedId) }
        animOnSlider?.addOnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
            onSliderValueChange(slider, value, fromUser) }
        animStepSlider?.addOnChangeListener { slider: Slider, value: Float, fromUser: Boolean ->
            onSliderValueChange(slider, value, fromUser) }
        randomColorSwitch?.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            onSwitchRandomStateChange(isChecked) }
        return view
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        viewModel = ViewModelProvider(requireActivity()).get(TorchereViewModel::class.java)
        primaryColorBtn!!.isChecked = true
        colorToggleGroup!!.isSelectionRequired = true
        viewModel!!.getPrimaryColor().observe(viewLifecycleOwner, { data: Int ->
            if (writingColor == WritingColor.PRIMARY) {
                colorPickerWheel!!.setColor(data, false)
            }
            primaryColor = data
        })
        viewModel!!.getSecondaryColor().observe(viewLifecycleOwner, { data: Int ->
            if (writingColor == WritingColor.SECONDARY) {
                colorPickerWheel!!.setColor(data, false)
            }
            secondaryColor = data
        })
        viewModel!!.getRandomColor().observe(
            viewLifecycleOwner,
            { data: Boolean? -> randomColorSwitch!!.isChecked = data!! })
        viewModel!!.getAnimationMode().observe(viewLifecycleOwner, { data: Int ->
            setAnimationName(data)
            hidingCells.clear()
            when (data) {
                1 -> animation1!!.isChecked = true
                2 -> {
                    animation2!!.isChecked = true
                    randomColorCard!!.visibility = View.GONE
                    hidingCells.add(randomColorCard)
                }
                3 -> {
                    animation3!!.isChecked = true
                    animationStepCard!!.visibility = View.GONE
                    animationDirectionCard!!.visibility = View.GONE
                    hidingCells.add(animationStepCard)
                    hidingCells.add(animationDirectionCard)
                }
                4 -> {
                    animation4!!.isChecked = true
                    animationStepCard!!.visibility = View.GONE
                    animationDirectionCard!!.visibility = View.GONE
                    hidingCells.add(animationStepCard)
                    hidingCells.add(animationDirectionCard)
                    hidingCells.add(randomColorCard)
                    hidingCells.add(primaryColorBtn)
                    hidingCells.add(secondaryColorBtn)
                    hidingCells.add(colorPickerCard)
                    hidingCells.add(brightnessCard)
                }
                5 -> {
                    animation5!!.isChecked = true
                    animationStepCard!!.visibility = View.GONE
                    hidingCells.add(animationStepCard)
                    hidingCells.add(randomColorCard)
                }
                6 -> {
                    animationStepCard!!.visibility = View.GONE
                    animationSpeedCard!!.visibility = View.GONE
                    animationDirectionCard!!.visibility = View.GONE
                    hidingCells.add(animationStepCard)
                    hidingCells.add(animationSpeedCard)
                    hidingCells.add(animationDirectionCard)
                    hidingCells.add(randomColorCard)
                    hidingCells.add(secondaryColorBtn)
                    animation6!!.isChecked = true
                }
            }
        })
        viewModel!!.getAnimationDirection().observe(viewLifecycleOwner, { data: Int ->
            setDirectionName(data)
            when (data) {
                1 -> direction1!!.isChecked = true
                2 -> direction2!!.isChecked = true
                3 -> direction3!!.isChecked = true
                4 -> direction4!!.isChecked = true
            }
        })
        viewModel?.getAnimationOnSpeed()?.observe(viewLifecycleOwner, { data: Int -> animOnSlider?.value = data.toFloat() })
        viewModel?.getAnimationStep()?.observe(viewLifecycleOwner, { data: Int -> animStepSlider?.value = data.toFloat() })
    }

    private fun blockPageScroll(event: MotionEvent): Boolean {
        when (event.action) {
            MotionEvent.ACTION_DOWN -> scrollView?.isEnableScrolling = false
            MotionEvent.ACTION_UP -> scrollView?.isEnableScrolling = true
        }
        return false
    }

    private fun expandAndCollapseAnimation() {
        if (animRadioGroup!!.visibility == View.VISIBLE) {
            animRadioGroup!!.visibility = View.GONE
            animationsHeaderArrow!!.animate().rotation(90f)
        } else {
            animRadioGroup!!.visibility = View.VISIBLE
            animationsHeaderArrow!!.animate().rotation(0f)
            scrollView!!.fullScroll(View.FOCUS_DOWN)
        }
    }

    private fun expandAndCollapseDirection() {
        if (directionRadioGroup!!.visibility == View.VISIBLE) {
            directionRadioGroup!!.visibility = View.GONE
            directionHeaderArrow!!.animate().rotation(90f)
        } else {
            directionRadioGroup!!.visibility = View.VISIBLE
            directionHeaderArrow!!.animate().rotation(0f)
        }
    }

    private fun onColorToggleChange(
        checkedId: Int,
        isChecked: Boolean
    ) {
        if (isChecked) {
            if (checkedId == primaryColorBtn!!.id) {
                writingColor = WritingColor.PRIMARY
                colorPickerWheel!!.setColor(primaryColor, false)
                brightnessSlider!!.setColor(primaryColor)
            } else if (checkedId == secondaryColorBtn!!.id) {
                writingColor = WritingColor.SECONDARY
                colorPickerWheel!!.setColor(secondaryColor, false)
                brightnessSlider!!.setColor(secondaryColor)
            }
        }
    }

    private fun onAnimationModeCheckedChange(checkedId: Int) {
        hidingCells.clear()
        showAllCells()
        if (checkedId == animation1!!.id) {
            viewModel?.setAnimationMode(1)
            setAnimationName(1)
            hidingCells.add(animationStepCard)
        } else if (checkedId == animation2!!.id) {
            viewModel?.setAnimationMode(2)
            setAnimationName(2)
            hidingCells.add(randomColorCard)
        } else if (checkedId == animation3!!.id) {
            viewModel?.setAnimationMode(3)
            setAnimationName(3)
            hidingCells.add(animationStepCard)
            hidingCells.add(animationDirectionCard)
        } else if (checkedId == animation4!!.id) {
            viewModel?.setAnimationMode(4)
            setAnimationName(4)
            hidingCells.add(animationStepCard)
            hidingCells.add(animationDirectionCard)
            hidingCells.add(randomColorCard)
            hidingCells.add(primaryColorBtn)
            hidingCells.add(secondaryColorBtn)
            hidingCells.add(colorPickerCard)
            hidingCells.add(brightnessCard)
        } else if (checkedId == animation5!!.id) {
            viewModel?.setAnimationMode(5)
            setAnimationName(5)
            hidingCells.add(animationStepCard)
            hidingCells.add(randomColorCard)
        } else if (checkedId == animation6!!.id) {
            viewModel?.setAnimationMode(6)
            setAnimationName(6)
            hidingCells.add(animationStepCard)
            hidingCells.add(animationSpeedCard)
            hidingCells.add(animationDirectionCard)
            hidingCells.add(randomColorCard)
            hidingCells.add(secondaryColorBtn)
        }
        updateUI()
    }

    private fun onDirectionCheckedChange(checkedId: Int) {
        if (checkedId == direction1!!.id) {
            viewModel!!.setAnimationDirection(1)
            setDirectionName(1)
        } else if (checkedId == direction2!!.id) {
            viewModel!!.setAnimationDirection(2)
            setDirectionName(2)
        } else if (checkedId == direction3!!.id) {
            viewModel!!.setAnimationDirection(3)
            setDirectionName(3)
        } else if (checkedId == direction4!!.id) {
            viewModel!!.setAnimationDirection(4)
            setDirectionName(4)
        }
    }

    private fun setAnimationName(name: Int) {
        animationName!!.text = TorchereData.animationModes!![name]
    }

    private fun setDirectionName(name: Int) {
        directionName!!.text = TorchereData.animationDirections!![name]
    }

    private fun onSliderValueChange(slider: Slider, value: Float, fromUser: Boolean) {
        if (fromUser) {
            if (slider == animOnSlider) {
                viewModel?.setAnimationOnSpeed(value.roundToInt())
            } else if (slider == animStepSlider) {
                viewModel?.setAnimationStep(value.roundToInt())
            }
        }
    }

    private fun onSwitchRandomStateChange(isChecked: Boolean) {
        viewModel?.setRandomColor(isChecked)
    }

    private fun updateUI() {
        for (cell in hidingCells) {
            cell?.visibility = View.GONE
        }
    }

    private fun showAllCells() {
        for (cell in allCells) {
            cell?.visibility = View.VISIBLE
        }
    }

}