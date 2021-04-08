package com.inchko.parkfinder.ui.rvZones

import android.graphics.Color
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.maps.android.PolyUtil
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.models.DirectionsResponse
import com.inchko.parkfinder.ui.map.MapViewModel
import com.inchko.parkfinder.ui.rvZones.recyView.ZoneAdapter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.delay
import retrofit2.Response


@AndroidEntryPoint
class RvZoneFragment : Fragment() {

    val rzViewModel: RvZoneViewModel by viewModels()
    val mapViewModel: MapViewModel by activityViewModels()
    private var polyline: PolylineOptions? = null
    private var zones: List<Zone>? = null
    private lateinit var button: ImageButton

    private var num = 0
    private var sort: Boolean = true //true = distance false = plazas libres
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // mapViewModel.updateZones()
        mapViewModel.zones.observe(viewLifecycleOwner, Observer {
                value: List<Zone>? ->
            value?.let {
                zones=it
                view?.let { it1 -> initRV(it1) }
            }
        })
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.RVReload)
        button.setOnClickListener {
            closeFragment()
        }
        initRV(view)
        // RecyclerView node initialized here
    }
    fun initRV(view:View){
        val rv: RecyclerView = view.findViewById(R.id.recyclerViewZones)
        rv.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            if (sort) {
                adapter = zones?.let { lz ->
                    Log.e("holder", "Zones loaded")
                    mapViewModel.currentLocation?.let { cl ->
                        ZoneAdapter(
                            lz.sortedBy { it.distancia },
                            cl
                        ) { it ->//Listener, add your actions here
                            Log.e("rv", "Zone clicked ${it.id}")/*
                                    val intent = Intent(context, Zone::class.java).apply {
                                        putExtra("id", it.id)
                                    }
                                *//*
                            mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLng(it.lat?.let { it1 ->
                                it.long?.let { it2 ->
                                    LatLng(
                                        it1, it2
                                    )
                                }
                            }))
                            mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(it.lat?.let { it1 ->
                                it.long?.let { it2 ->
                                    LatLng(
                                        it1, it2
                                    )
                                }
                            }, 19f))*/


                            it.lat?.let { it1 -> it.long?.let { it2 ->
                                LatLng(it1,
                                    it2
                                )
                            } }?.let { it2 ->
                                rzViewModel.getDirections(mapViewModel.currentLocation!!,
                                    it2
                                )
                            }

                            rzViewModel.response.observe(viewLifecycleOwner, Observer {
                                    value: Response<DirectionsResponse>? ->
                                value?.let {
                                    drawPolyline(it)
                                    closeFragment()
                                }
                            })

                            /*
                            val r=rzViewModel.response
                            if (r != null) {
                                drawPolyline(r)
                            }*/
                        }
                    }
                };
            } else {
                adapter = zones?.let { lz ->
                    Log.e("holder", "Zones loaded")
                    mapViewModel.currentLocation?.let {
                        ZoneAdapter(
                            lz.sortedByDescending { it.plazasLibres },
                            it
                        ) {//Listener, add your actions here
                            Log.e("rv", "Zone clicked ${it.id}")/*
                                    val intent = Intent(context, Zone::class.java).apply {
                                        putExtra("id", it.id)
                                    }
                                */
                            closeFragment()

                        }
                    };
                }
            }
        }
    }


    //closes the fragment
    fun closeFragment() {
        Log.e("debug", "close fragment executed")
        activity?.supportFragmentManager?.saveFragmentInstanceState(this)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
        rzViewModel.response.value=null
    }


    private fun drawPolyline(response: Response<DirectionsResponse>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        mapViewModel.mMap?.addPolyline(polyline)
        //mapViewModel.mMap?.clear()
    }


}