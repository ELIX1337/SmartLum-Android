package com.smartlum.smartlum.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.material.appbar.MaterialToolbar
import com.smartlum.smartlum.R

class AboutFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_about, container, false)
        setHasOptionsMenu(true)
        val toolbar: MaterialToolbar = view.findViewById(R.id.toolbarId)
        toolbar.setNavigationOnClickListener { v: View? ->
            Navigation.findNavController(
                v!!
            ).popBackStack()
        }
        return view
    }
}