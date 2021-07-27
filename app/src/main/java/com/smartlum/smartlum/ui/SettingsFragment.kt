package com.smartlum.smartlum.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.Navigation
import com.google.android.gms.oss.licenses.OssLicensesMenuActivity
import com.google.android.material.button.MaterialButton
import com.smartlum.smartlum.R

class SettingsFragment : Fragment() {
    private var btnLicences: MaterialButton? = null
    private var btnAbout: MaterialButton? = null
    private var btnPrivacy: MaterialButton? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Settings"
        val view = inflater.inflate(R.layout.fragment_settings, container, false)
        btnLicences = view.findViewById(R.id.btn_licences)
        btnAbout = view.findViewById(R.id.btn_about)
        btnPrivacy = view.findViewById(R.id.btn_privacy_policy)
        val intent = Intent(activity, OssLicensesMenuActivity::class.java)
        btnLicences?.setOnClickListener {
            intent.putExtra("title", "Open source licenses")
            startActivity(intent)
        }
        btnAbout?.setOnClickListener { v: View? ->
            Navigation.findNavController(v!!).navigate(R.id.action_settingsFragment_to_aboutFragment)
        }
        btnPrivacy?.setOnClickListener {
            val privacyIntent = Intent()
            privacyIntent.action = Intent.ACTION_VIEW
            privacyIntent.addCategory(Intent.CATEGORY_BROWSABLE)
            privacyIntent.data = Uri.parse(requireActivity().getString(R.string.link_policy))
            startActivity(privacyIntent)
        }
        return view
    }

    companion object {
        fun newInstance(param1: String?, param2: String?): SettingsFragment {
            return SettingsFragment()
        }
    }
}