package com.inchko.parkfinder.ui.rvZones

import android.content.Context
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
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.models.DirectionsResponse
import com.inchko.parkfinder.ui.map.MapViewModel
import com.inchko.parkfinder.ui.rvZones.recyView.ZoneAdapter
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response


@AndroidEntryPoint
class RvZoneFragment : Fragment() {

    val rzViewModel: RvZoneViewModel by viewModels()
    val mapViewModel: MapViewModel by activityViewModels()
    private var polyline: PolylineOptions? = null
    private var zones: List<Zone>? = null
    private lateinit var button: ImageButton

    private var num = 0
    private var sort: Boolean = true //false = distance true = plazas libres
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // mapViewModel.updateZones()
        mapViewModel.zones.observe(viewLifecycleOwner, Observer { value: List<Zone>? ->
            value?.let {
                zones = it
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
        //  initRV(view)
        // RecyclerView node initialized here
    }

    fun initRV(view: View) {
        val rv: RecyclerView = view.findViewById(R.id.recyclerViewZones)
        rv.apply {
            layoutManager = LinearLayoutManager(activity)
            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val sp = context?.getSharedPreferences("vehicle", Context.MODE_PRIVATE)
            sort = sharedPreferences.getBoolean("orderZones", false)
            val vehicle = sharedPreferences.getBoolean("showVehicle", false)
            val userCar = sp?.getString("caruserID", "")
            val typeVehicle = sp?.getInt("type", -1)
            if (!sort) {
                adapter = zones?.let { lz ->
                    Log.e("holder", "Zones loaded")
                    val zonesFinal = mutableListOf<Zone>()
                    zonesFinal.addAll(lz)
                    if (Firebase.auth.currentUser != null && userCar == Firebase.auth.currentUser.uid && vehicle && typeVehicle != -1) {
                        zonesFinal.clear()
                        for (z in lz) {
                            if (z.tipo == typeVehicle)
                                zonesFinal.add(z)
                        }
                    }
                    mapViewModel.currentLocation?.let { cl ->
                        ZoneAdapter(
                            zonesFinal.sortedBy { it.distancia }, rzViewModel,
                            cl
                        ) { zone ->//Listener, add your actions here
                            Log.e("rv", "Zone clicked ${zone.id}")

                            mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLng(zone.lat?.let { it1 ->
                                zone.long?.let { it2 ->
                                    LatLng(
                                        it1, it2
                                    )
                                }
                            }))
                            mapViewModel.mMap?.animateCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    zone.lat?.let { it1 ->
                                        zone.long?.let { it2 ->
                                            LatLng(
                                                it1, it2
                                            )
                                        }
                                    },
                                    19f
                                )
                            )


                            zone.lat?.let { it1 ->
                                zone.long?.let { it2 ->
                                    LatLng(
                                        it1,
                                        it2
                                    )
                                }
                            }?.let { it2 ->
                                rzViewModel.getDirections(
                                    mapViewModel.currentLocation!!,
                                    it2
                                )
                            }
                            rzViewModel.response.observe(
                                viewLifecycleOwner,
                                Observer { value: Response<DirectionsResponse>? ->
                                    value?.let {
                                        drawPolyline(it)
                                        val spw = context.getSharedPreferences(
                                            "watchZone",
                                            Context.MODE_PRIVATE
                                        ) ?: return@Observer
                                        with(spw.edit()) {
                                            putString("zoneID", zone.id)
                                            putString("zoneUserID", Firebase.auth.currentUser.uid)
                                            apply()
                                        }
                                        val test = spw.getString("zoneID", "")
                                        Log.e(
                                            "watchZone",
                                            "Watching zone : ${zone.id} value of the zone: $test"
                                        )
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
                    val zonesFinal = mutableListOf<Zone>()
                    zonesFinal.addAll(lz)
                    if (Firebase.auth.currentUser != null && userCar == Firebase.auth.currentUser.uid && vehicle && typeVehicle != -1) {
                        zonesFinal.clear()
                        for (z in lz) {
                            if (z.tipo == typeVehicle)
                                zonesFinal.add(z)
                        }
                    }
                    mapViewModel.currentLocation?.let {
                        ZoneAdapter(
                            zonesFinal.sortedByDescending { it.plazasLibres }, rzViewModel,
                            it
                        ) { zone ->//Listener, add your actions here
                            Log.e("rv", "Zone clicked order by plazas ${zone.id}")

                            mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLng(zone.lat?.let { it1 ->
                                zone.long?.let { it2 ->
                                    LatLng(
                                        it1, it2
                                    )
                                }
                            }))
                            mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLngZoom(zone.lat?.let { it1 ->
                                zone.long?.let { it2 ->
                                    LatLng(
                                        it1, it2
                                    )
                                }
                            }, 19f))


                            zone.lat?.let { it1 ->
                                zone.long?.let { it2 ->
                                    LatLng(
                                        it1,
                                        it2
                                    )
                                }
                            }?.let { it2 ->
                                rzViewModel.getDirections(
                                    mapViewModel.currentLocation!!,
                                    it2
                                )
                            }

                            rzViewModel.response.observe(
                                viewLifecycleOwner,
                                Observer { value: Response<DirectionsResponse>? ->
                                    value?.let {
                                        drawPolyline(it)
                                        val spw = context.getSharedPreferences(
                                            "watchZone",
                                            Context.MODE_PRIVATE
                                        )
                                        spw.edit().putString("zoneID", zone.id)
                                        spw.edit()
                                            .putString("zoneUserID", Firebase.auth.currentUser.uid)
                                        closeFragment()
                                    }
                                })
                        }
                    }
                };
            }
        }
    }


    //closes the fragment
    fun closeFragment() {
        Log.e("debug", "close fragment executed")
        activity?.supportFragmentManager?.saveFragmentInstanceState(this)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
        rzViewModel.response.value = null
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