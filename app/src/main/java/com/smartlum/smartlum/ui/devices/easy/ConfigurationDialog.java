package com.smartlum.smartlum.ui.devices.easy;

import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager.widget.ViewPager;

import com.smartlum.smartlum.R;
import com.smartlum.smartlum.adapter.FragmentAdapter;
import com.smartlum.smartlum.ui.customViews.CustomViewPager;
import com.smartlum.smartlum.utils.Sratocol;
import com.smartlum.smartlum.viewmodels.EasyViewModel;
import com.google.android.material.button.MaterialButton;

import org.jetbrains.annotations.NotNull;

public class ConfigurationDialog extends DialogFragment {

    public static final String TAG = "CONFIGURATION DIALOG";
    public static final String CONFIGURING_REGISTER = "com.smartlum.smartlum.ui.devices.easy.SENSOR_LOCATION";
    public static final String LAST_KEY = "com.smartlum.smartlum.ui.devices.easy.LAST_KEY";
    public static final String LAST_VALUE = "com.smartlum.smartlum.ui.devices.easy.LAST_VALUE";
    public static final String CLOSE_OBSERVER = "com.smartlum.smartlum.ui.devices.easy.CLOSE_OBSERVER";

    public MaterialButton btnConfirm;
    private CustomViewPager viewPager;
    private ProgressBar progressIndicator;
    private FragmentAdapter adapter;
    private int goBackCounter = 0;

    private EasyViewModel viewModel;
    private Bundle lastConfig;

    private boolean successWrite = false;
    private boolean isLastTab;

    private final Handler  handler = new Handler();
    private final Runnable runnable = new Runnable() {
        int counter = 0;
        @Override
        public void run() {
            if (successWrite) {
                handler.removeCallbacks(runnable);
                goNext();
                counter = 0;
            } else {
                handler.postDelayed(this,300);
                viewModel.writeData(lastConfig.getByte(LAST_KEY), lastConfig.getInt(LAST_VALUE));
                counter ++;
                if (counter == 20) {
                    endConfiguration(false);
                }
            }
        }
    };

    public static void display(FragmentManager fragmentManager) {
        ConfigurationDialog configurationDialog = new ConfigurationDialog();
        configurationDialog.show(fragmentManager, TAG);
    }

    @NotNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return new Dialog(getActivity(), getTheme())
        {
            @Override
            public void onBackPressed() {
                 if (goBackCounter != 1) {
                     Toast.makeText(getContext(), "R.string.device_is_not_configured_yet", Toast.LENGTH_SHORT).show();
                     goBackCounter ++;
                 } else {
                     endConfiguration(false);
                 }
            }
        };
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NORMAL, R.style.Theme_StairsLight);
    }

    @Override
    public void onStart() {
        super.onStart();
        Dialog dialog = getDialog();
        if (dialog != null) {
            int width = ViewGroup.LayoutParams.MATCH_PARENT;
            int height = ViewGroup.LayoutParams.MATCH_PARENT;
            dialog.getWindow().setLayout(width, height);
            dialog.getWindow().setWindowAnimations(R.style.Theme_StairsLight_SlideVertical);
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        View view         = inflater.inflate(R.layout.configure_dialog, container, false);
        btnConfirm        = view.findViewById(R.id.dialog_btn_confirm);
        progressIndicator = view.findViewById(R.id.pb_send_config);
        viewPager         = view.findViewById(R.id.dialog_viewpager);
        adapter           = new FragmentAdapter(getChildFragmentManager());

        addDefaultTabs();
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(0);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageSelected(int pos) {
                // Если текущая вкладка последняя...
                if (pos == adapter.getCount()-1) {
                    isLastTab = true;
                }
            }
            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {}
            @Override
            public void onPageScrollStateChanged(int arg0) {}
        });

        viewPager.setSwipeable(false);
        btnConfirm.setEnabled(false);
        btnConfirm.setOnClickListener(v -> sendConfig());

        return view;
    }

    private void sendConfig() {
        getChildFragmentManager().setFragmentResult(CLOSE_OBSERVER, null);
        progressIndicator.setVisibility(View.VISIBLE);
        btnConfirm.setEnabled(false);
        handler.post(runnable);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(EasyViewModel.class);
        viewModel.getLastConfig().observe(requireActivity(), bundle -> {
            lastConfig = bundle;
            btnConfirm.setEnabled(true);
        });

        viewModel.getSensorLocation(Sratocol.Register.TOP_SENS_DIRECTION).observe(requireActivity(), location -> {
            if (lastConfig.getInt(LAST_VALUE) == location) {
                successWrite = true;
            }
        });

        viewModel.getSensorLocation(Sratocol.Register.BOT_SENS_DIRECTION).observe(requireActivity(), location -> {
            if (lastConfig.getInt(LAST_VALUE) == location) {
                successWrite = true;
            }
        });

        viewModel.getTopSensorTriggerDistance().observe(requireActivity(), distance -> {
            if (lastConfig.getInt(LAST_VALUE) == distance) {
                successWrite = true;
            }
        });

        viewModel.getBotSensorTriggerDistance().observe(requireActivity(), distance -> {
            if (lastConfig.getInt(LAST_VALUE) == distance) {
                successWrite = true;
            }
        });

        viewModel.getStripType().observe(requireActivity(), stripType -> {
            if (lastConfig.getInt(LAST_VALUE) == stripType) {
                successWrite = true;
            }
        });
    }

    @Override
    public void onDismiss(@NonNull DialogInterface dialog) {
        handler.removeCallbacks(runnable);
        super.onDismiss(dialog);
    }

    private void addDefaultTabs() {
        Bundle bundle = new Bundle();

        bundle.putByte(CONFIGURING_REGISTER, Sratocol.Register.BOT_SENS_DIRECTION);
        Fragment botSensLocationTest = EasySensLocationConfigureFragment.newInstance(bundle);
        adapter.addFragment(botSensLocationTest, null);

        bundle.putByte(CONFIGURING_REGISTER, Sratocol.Register.TOP_SENS_DIRECTION);
        Fragment topSensLocationTest = EasySensLocationConfigureFragment.newInstance(bundle);
        adapter.addFragment(topSensLocationTest, null);

        bundle.putByte(CONFIGURING_REGISTER, Sratocol.Register.BOT_SENS_TRIGGER_DISTANCE);
        Fragment botSensDistanceTab = EasySensDistanceConfigureFragment.newInstance(bundle);
        adapter.addFragment(botSensDistanceTab, null);

        bundle.putByte(CONFIGURING_REGISTER, Sratocol.Register.TOP_SENS_TRIGGER_DISTANCE);
        Fragment topSensDistanceTab = EasySensDistanceConfigureFragment.newInstance(bundle);
        adapter.addFragment(topSensDistanceTab, null);

        bundle.putByte(CONFIGURING_REGISTER, Sratocol.Register.STRIP_TYPE);
        Fragment stripTypeTab = EasyStripTypeConfigureFragment.newInstance(bundle);
        adapter.addFragment(stripTypeTab, null);

        adapter.notifyDataSetChanged();
    }

    // Show next configuration slide
    private void goNext() {
        if (isLastTab) {
            endConfiguration(true);
        } else {
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1, true);
            successWrite = false;
            progressIndicator.setVisibility(View.GONE);
        }
        handler.removeCallbacks(runnable);
        handler.removeCallbacksAndMessages(runnable);
    }

    // Finishing configuration
    private void endConfiguration(boolean isSuccessful) {
        Bundle b = new Bundle();
        b.putBoolean("isConfigured", isSuccessful);
        getParentFragmentManager().setFragmentResult("isConfigured", b);
        handler.removeCallbacks(runnable);
        this.dismiss();
        requireActivity().finish();
    }

}
