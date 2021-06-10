package com.inchko.parkfinder.ui.information

import android.os.Bundle
import android.text.method.LinkMovementMethod
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import com.inchko.parkfinder.R


class InformationFragment : Fragment() {

    private lateinit var informationViewModel: InformationViewModel
    private lateinit var polLink: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        informationViewModel =
            ViewModelProvider(this).get(InformationViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_information, container, false)
        polLink = root.findViewById(R.id.InformationLink)
        polLink.movementMethod = LinkMovementMethod.getInstance();
        return root
    }
}