package com.smartlum.smartlum.ui

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import com.google.android.material.card.MaterialCardView
import com.smartlum.smartlum.R

class HomeFragment : Fragment() {
    private var fabInstagram: ImageView? = null
    private var fabVK: ImageView? = null
    private var fabWEB: ImageView? = null
    private var linkWeb: MaterialCardView? = null
    private var linkInstagram: MaterialCardView? = null
    private var linkVK: MaterialCardView? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        activity?.title = "Home"
        val view = inflater.inflate(R.layout.fragment_home, container, false)
        fabInstagram = view.findViewById(R.id.fab_instagram)
        fabVK = view.findViewById(R.id.fab_vk)
        fabWEB = view.findViewById(R.id.fab_web)
        linkInstagram = view.findViewById(R.id.home_link_instagram)
        linkVK = view.findViewById(R.id.home_link_vk)
        linkWeb = view.findViewById(R.id.home_link_web)
        val intent = Intent()
        intent.action = Intent.ACTION_VIEW
        intent.addCategory(Intent.CATEGORY_BROWSABLE)
        linkInstagram?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_instagram))
            startActivity(intent)
        }
        linkVK?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_vk))
            startActivity(intent)
        }
        linkWeb?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_website))
            startActivity(intent)
        }
        fabInstagram?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_instagram))
            startActivity(intent)
        }
        fabVK?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_vk))
            startActivity(intent)
        }
        fabWEB?.setOnClickListener {
            intent.data = Uri.parse(getString(R.string.link_website))
            startActivity(intent)
        }
        return view
    }

}