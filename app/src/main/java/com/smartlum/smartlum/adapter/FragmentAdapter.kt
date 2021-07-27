package com.smartlum.smartlum.adapter

import android.os.Parcelable
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentStatePagerAdapter
import java.util.*

class FragmentAdapter(manager: FragmentManager) :
    FragmentStatePagerAdapter(manager, BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT) {
    private val mFragmentList: MutableList<Fragment> = ArrayList()
    private val mFragmentTitleList: MutableList<String> = ArrayList()
    override fun getItem(position: Int): Fragment {
        return mFragmentList[position]
    }

    override fun getCount(): Int {
        return mFragmentList.size
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return mFragmentTitleList[position]
    }

    fun addFragment(fragment: Fragment, title: String) {
        mFragmentList.add(fragment)
        mFragmentTitleList.add(title)
    }

    fun addFragment(index: Int, fragment: Fragment, title: String) {
        mFragmentList.add(index, fragment)
        mFragmentTitleList.add(index, title)
    }

    fun getFragmentIndex(title: String): Int {
        return mFragmentTitleList.indexOf(title)
    }

    fun removeFragment(index: Int) {
        mFragmentList.removeAt(index)
        mFragmentTitleList.removeAt(index)
    }

    fun removeAllFragments() {
        mFragmentList.clear()
        mFragmentTitleList.clear()
    }

    override fun getItemPosition(`object`: Any): Int {
        // Causes adapter to reload all Fragments when
        // notifyDataSetChanged is called
        return POSITION_NONE
    }

    override fun restoreState(state: Parcelable?, loader: ClassLoader?) {
        //Do NOTHING;
    }
}