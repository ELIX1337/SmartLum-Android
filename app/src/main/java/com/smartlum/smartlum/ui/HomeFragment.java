package com.smartlum.smartlum.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.smartlum.smartlum.R;

public class HomeFragment extends Fragment {

    private FloatingActionButton fabInstagram, fabVK, favYoutube, fabWEB;

    public HomeFragment() { }

    public static HomeFragment newInstance(String param2) {
        return new HomeFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getActivity().setTitle("Home");
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        fabInstagram = view.findViewById(R.id.fab_instagram);
        fabVK        = view.findViewById(R.id.fab_vk);
        favYoutube   = view.findViewById(R.id.fab_youtube);
        fabWEB       = view.findViewById(R.id.fab_web);

        Intent intent = new Intent();
        intent.setAction(Intent.ACTION_VIEW);
        intent.addCategory(Intent.CATEGORY_BROWSABLE);

        fabInstagram.setOnClickListener(v -> {
            intent.setData(Uri.parse(getString(R.string.link_instagram)));
            startActivity(intent);
        });

        fabVK.setOnClickListener(v -> {
            intent.setData(Uri.parse(getString(R.string.link_vk)));
            startActivity(intent);
        });
        favYoutube.setOnClickListener(v -> {
            intent.setData(Uri.parse(getString(R.string.link_youtube)));
            startActivity(intent);
        });
        fabWEB.setOnClickListener(v -> {
            intent.setData(Uri.parse(getString(R.string.link_website)));
            startActivity(intent);
        });
        return view;
    }
}