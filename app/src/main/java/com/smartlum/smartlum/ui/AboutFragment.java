package com.smartlum.smartlum.ui;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.navigation.Navigation;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.appbar.MaterialToolbar;
import com.smartlum.smartlum.R;

public class AboutFragment extends Fragment {

    public AboutFragment() { }

    public static AboutFragment newInstance(String param1, String param2) {
        return new AboutFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_about, container, false);
        setHasOptionsMenu(true);
        MaterialToolbar toolbar = view.findViewById(R.id.toolbarId);

        toolbar.setNavigationOnClickListener(v -> Navigation.findNavController(v).popBackStack());
        return view;
    }
}