package com.smartlum.smartlum.adapter;

import android.os.Parcelable;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentStatePagerAdapter;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class FragmentAdapter extends FragmentStatePagerAdapter {

    private final List<Fragment> mFragmentList = new ArrayList<>();
    private final List<String> mFragmentTitleList = new ArrayList<>();

    public FragmentAdapter(@NonNull FragmentManager manager) {
        super(manager, FragmentStatePagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
    }

    @NotNull
    @Override
    public Fragment getItem(int position) {
        return mFragmentList.get(position);
    }

    @Override
    public int getCount() {
        return mFragmentList.size();
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mFragmentTitleList.get(position);
    }

    public void addFragment(Fragment fragment, String title) {
        mFragmentList.add(fragment);
        mFragmentTitleList.add(title);
    }

    public void addFragment(int index, Fragment fragment, String title) {
        mFragmentList.add(index, fragment);
        mFragmentTitleList.add(index, title);
    }

    public int getFragmentIndex(String title) {
        return mFragmentTitleList.indexOf(title);
    }

    public void removeFragment(int index) {
        mFragmentList.remove(index);
        mFragmentTitleList.remove(index);
    }

    public void removeAllFragments() {
        mFragmentList.clear();
        mFragmentTitleList.clear();
    }

    @Override
    public int getItemPosition(Object object) {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE;
    }

    @Override
    public void restoreState(Parcelable state, ClassLoader loader) {
        //Do NOTHING;
    }

}
