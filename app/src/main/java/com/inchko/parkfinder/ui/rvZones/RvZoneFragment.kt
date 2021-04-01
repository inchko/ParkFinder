package com.inchko.parkfinder.ui.rvZones

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.ui.rvZones.recyView.ZoneAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class RvZoneFragment : Fragment() {

    val rzViewModel: RvZoneViewModel by viewModels()

    private var zones: List<Zone>?=null

    private var num = 0

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        rzViewModel.zones.observe(viewLifecycleOwner, Observer {
            zones = it
        })
        // RecyclerView node initialized here
        val rv: RecyclerView = view.findViewById(R.id.recyclerViewZones)
        rv.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = zones?.let { adapter ->
                ZoneAdapter(adapter) {
                    Log.e("rv", "Zone clicked ${it.id}")/*
                        val intent = Intent(context, Zone::class.java).apply {
                            putExtra("id", it.id)
                        }
                    */
                }
            };

        }
    }


    //closes the fragment with the general button
    fun test() {
        Log.e("shower", "hola")
        rzViewModel.sum()
        activity?.supportFragmentManager?.saveFragmentInstanceState(this)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();


    }
}
