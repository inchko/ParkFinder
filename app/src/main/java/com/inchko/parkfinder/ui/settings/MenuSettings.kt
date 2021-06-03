package com.inchko.parkfinder.ui.settings

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import com.inchko.parkfinder.R

class MenuSettings : Fragment() {
    private lateinit var back: ImageButton
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.settings_activity, container, false)
        back = root.findViewById(R.id.backSettings)
        back.visibility = View.INVISIBLE
        return root
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (savedInstanceState == null) {
            activity?.supportFragmentManager
                ?.beginTransaction()
                ?.replace(R.id.settings, SettingsActivity.SettingsFragment())
                ?.commit()
        }

    }
}