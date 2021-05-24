package com.smartlum.smartlum.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.oss.licenses.OssLicensesMenuActivity;
import com.google.android.material.button.MaterialButton;
import com.smartlum.smartlum.R;

public class SettingsFragment extends Fragment {

    private MaterialButton btnLicences, btnAbout, btnPrivacy;

    public SettingsFragment() {}
    public static SettingsFragment newInstance(String param1, String param2) {
        return new SettingsFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        getActivity().setTitle("Settings");
        View view = inflater.inflate(R.layout.fragment_settings, container, false);
        btnLicences = view.findViewById(R.id.btn_licences);
        btnAbout    = view.findViewById(R.id.btn_about);
        btnPrivacy  = view.findViewById(R.id.btn_privacy_policy);

        final Intent intent = new Intent(getActivity(), OssLicensesMenuActivity.class);
        btnLicences.setOnClickListener(v -> {
            intent.putExtra("title", "Open source licenses");
            startActivity(intent);
        });

        btnAbout.setOnClickListener(v -> Navigation.findNavController(v).navigate(R.id.action_settingsFragment_to_aboutFragment));
        btnPrivacy.setOnClickListener(v -> {
            final Intent privacyIntent = new Intent();
            privacyIntent.setAction(Intent.ACTION_VIEW);
            privacyIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            privacyIntent.setData(Uri.parse(requireActivity().getString(R.string.link_policy)));
            startActivity(privacyIntent);
        });
        return view;
    }
}