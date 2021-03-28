package com.inchko.parkfinder.ui.places

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inchko.parkfinder.R
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class PlacesFragment : Fragment() {

      val placesViewModel: PlacesViewModel by viewModels()

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_gallery, container, false)
        val textView: TextView = root.findViewById(R.id.text_gallery)
        placesViewModel.test.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        return root
    }
}