package com.inchko.parkfinder.ui.map

import android.Manifest
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.content.res.Resources
import android.location.Location
import android.os.Bundle
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MapStyleOptions
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.MainActivity
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.CustomizeProfile
import com.inchko.parkfinder.ui.rvZones.RvZoneFragment
import com.inchko.parkfinder.utils.NotificationDisabeler
import dagger.hilt.android.AndroidEntryPoint
import java.util.*
import java.util.concurrent.TimeUnit
import kotlin.concurrent.timerTask


@AndroidEntryPoint
class MapFragment : Fragment(), OnMapReadyCallback, OnMyLocationButtonClickListener,
    OnMyLocationClickListener {

    private lateinit var mMap: GoogleMap
    private val mapViewModel: MapViewModel by activityViewModels()
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private var currentLocation: Location? = null;
    private val permissionCode = 101
    private var testLocation: LatLng? = null
    private lateinit var zoneButton: ImageButton
    private lateinit var parkingButton: ImageButton
    private lateinit var stopZoneButton: ImageButton
    private val mainFragment = RvZoneFragment()
    private lateinit var sharedPreferences: SharedPreferences
    private lateinit var parkingMarker: Marker
    private lateinit var zones: List<Zone>


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.fragment_map, container, false)
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity)
        val name = sharedPreferences.getString("range", "1")
        Log.e("zones", "rango: $name")
        if (name != null) {
            mapViewModel.updateZones(name.toInt())
        }
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        zoneButton = view.findViewById(R.id.openZones)
        stopZoneButton = view.findViewById(R.id.stopZone)
        val name = context?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)
            ?.getString("zoneID", "")
        if (Firebase.auth.currentUser == null || name == ""
        ) {
            stopZoneButton.visibility = View.INVISIBLE
        } else stopZoneButton.visibility = View.VISIBLE


        parkingButton = view.findViewById(R.id.pakingBtn)
        if (Firebase.auth.currentUser == null) parkingButton.visibility = View.INVISIBLE


        stopZoneButton.setOnClickListener {
            context?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.edit()
                ?.putString("zoneID", "")?.apply()
            context?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.edit()
                ?.putInt("estadoActual", -1)?.apply()
            stopZoneButton.visibility = View.INVISIBLE
        }

        zoneButton.setOnClickListener {
            activity?.supportFragmentManager?.beginTransaction()
                ?.add(R.id.replacable, mainFragment)
                ?.commit()
        }
        parkingButton.setOnClickListener {
            addOrDeleteParkingSpot()
        }
    }

    private fun putParkingMarker() {
        val sharedPrefs =
            context?.getSharedPreferences("parkingSpot", Context.MODE_PRIVATE)
        val lat = sharedPrefs?.getFloat("latitude", -1000.0f)
        val long = sharedPrefs?.getFloat("longitude", -1000.0f)
        val user = sharedPrefs?.getString("ParkUserID", "")
        if (lat == -1000.0f) {
            // parkingMarker.remove()
        } else {
            if (lat != null) {
                if (long != null) {
                    if (Firebase.auth.currentUser != null && Firebase.auth.currentUser.uid == user) {
                        parkingMarker = mMap.addMarker(
                            MarkerOptions().position(
                                LatLng(
                                    lat.toDouble(),
                                    long.toDouble()
                                )
                            ).title(getString(R.string.parkingSpotHeader))
                            //.icon(fromResource(R.mipmap.parking_marker_icon))
                        )
                        parkingMarker.isVisible = true
                    }
                }
            }
        } // end of else
    }

    private fun addOrDeleteParkingSpot() {
        val sharedPrefs =
            context?.getSharedPreferences("parkingSpot", Context.MODE_PRIVATE)
        val test = sharedPrefs?.getFloat("latitude", -1000.0f)
        var long = currentLocation?.longitude
        var lat = currentLocation?.latitude
        if (test != -1000.0f) {
            val builder = context?.let { AlertDialog.Builder(it) }
            builder?.setTitle(getString(R.string.parkingSpotRemoveHeader))
            builder?.setMessage(getString(R.string.parkingSpotRemoveMessage))
            builder?.setPositiveButton(android.R.string.yes) { dialog, which ->
                long = -1000.0
                lat = -1000.0
                parkingMarker.remove()
                if (sharedPrefs != null) {
                    with(sharedPrefs.edit()) {
                        if (long != null) {
                            putFloat("longitude", -1000.0f)
                        }
                        if (lat != null) {
                            putFloat("latitude", -1000.0f)
                        }
                        putString("ParkUserID", "")
                        apply()
                        putParkingMarker()
                    }
                }
            }
            builder?.setNegativeButton(android.R.string.no) { dialog, which ->
            }

            builder?.show()

        } else {
            val builder = context?.let { AlertDialog.Builder(it) }
            builder?.setTitle(getString(R.string.parkingSpotAddHeader))
            builder?.setMessage(getString(R.string.parkingSpotAddMessage))
            builder?.setPositiveButton(android.R.string.yes) { dialog, which ->
                if (sharedPrefs != null) {
                    with(sharedPrefs.edit()) {
                        if (long != null) {
                            putFloat("longitude", long!!.toFloat())
                        }
                        if (lat != null) {
                            putFloat("latitude", lat!!.toFloat())
                        }
                        putString("ParkUserID", Firebase.auth.currentUser.uid)
                        apply()
                        putParkingMarker()
                    }
                }
            }
            builder?.setNegativeButton(android.R.string.no) { dialog, which ->
            }
            builder?.show()
        }
    }

    private val locationRequest = LocationRequest.create().apply {
        interval = TimeUnit.SECONDS.toMillis(60)
        fastestInterval = TimeUnit.SECONDS.toMillis(30)
        maxWaitTime = TimeUnit.MINUTES.toMillis(2)
        priority = LocationRequest.PRIORITY_HIGH_ACCURACY
    }
    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            super.onLocationResult(locationResult)
            currentLocation = locationResult.lastLocation
            mapViewModel.updateCurrentLocation(
                LatLng(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude
                )
            )
            if (currentLocation != null) {
                val name = sharedPreferences.getString("range", "1")
                val op = sharedPreferences.getBoolean("searchFZ", false)
                val sp = context?.getSharedPreferences("FavZone", Context.MODE_PRIVATE)
                val namefz = sp?.getString("FavZone", "")
                val userfz = sp?.getString("fzuserID", "")
                if (Firebase.auth.currentUser != null && userfz == Firebase.auth.currentUser.uid && op && namefz != "") {
                    Log.e("zones", op.toString())
                    Log.e("zones", "rango: $name")
                    val lat = sp?.getFloat("fzlatitude", 0f)?.toDouble()
                    val long = sp?.getFloat("fzlongitude", 0f)?.toDouble()
                    Log.e("zones", "you get $lat $long")
                    if (name != null) {
                        if (lat != null) {
                            if (long != null) {
                                mapViewModel.updateZonesLoc(name.toInt(), lat, long)
                            }
                        }
                    }
                } else {
                    if (name != null) {
                        mapViewModel.updateZones(name.toInt())
                    }
                }
                Log.e("api", "updating zones in location callback")
                if (getView() != null) {
                    mapViewModel.zones.observe(viewLifecycleOwner, Observer {
                        if (it != null) {
                            for (zone in it) {
                                val markerLocation = LatLng(zone.lat!!, zone.long!!)
                                val title =
                                    "${zone.id} ${zone.plazasLibres}/${zone.plazasTotales}"
                                mMap.addMarker(
                                    MarkerOptions().position(markerLocation).title(title)
                                )
                            }
                            zones = it
                            checkZone()
                        }
                    })
                }
            }
        }
    }

    private fun checkZone() {
        Log.e("watchZone", "checkZone")
        val sp = context?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)
        val nameZone = sp?.getString("zoneID", "")
        Log.e("watchZone", "we are searching for $nameZone")

        val shpf = context?.getSharedPreferences("vehicle", Context.MODE_PRIVATE)
        val typeOfCar = shpf?.getInt("type", -1)
        val sizeOfCar = shpf?.getInt("size", -1)
        val userCar = shpf?.getString("caruserID", "")
        var estadoActual = sp?.getInt("estadoActual", -1)

//0 ocupada 1 vacia
        val watchableZone = zones.filter { zone -> zone.id == nameZone }
        if (watchableZone.isNotEmpty()) {
            if (typeOfCar == -1) {
                if (estadoActual == 1) {
                    if (watchableZone[0].plazasLibres == 0) {
                        initNotis(true)
                        estadoActual = 0
                        sp?.edit()?.putInt("estadoActual", estadoActual)?.apply()
                    }
                } else if (estadoActual == 0) {
                    if (watchableZone[0].plazasLibres != 0) {
                        initNotis(false)
                        estadoActual = 1
                        if (sp != null) {
                            sp.edit()?.putInt("estadoActual", estadoActual)?.apply()
                        }
                    }
                }
            } else {
                if (typeOfCar == 1 && Firebase.auth.currentUser.uid == userCar) {
                    if (estadoActual == 1) {
                        if (watchableZone[0].plazasMl == 0) {
                            initNotis(true)
                            estadoActual = 0
                            sp?.edit()?.putInt("estadoActual", estadoActual)?.apply()
                        }
                    } else if (estadoActual == 0) {
                        if (watchableZone[0].plazasMl != 0) {
                            initNotis(false)
                            estadoActual = 1
                            if (sp != null) {
                                sp.edit()?.putInt("estadoActual", estadoActual)?.apply()
                            }
                        }
                    }
                } else {
                    if (sizeOfCar == 1 && Firebase.auth.currentUser.uid == userCar) {
                        if (estadoActual == 1) {
                            if (watchableZone[0].plazasPl == 0) {
                                initNotis(true)
                                estadoActual = 0
                                sp?.edit()?.putInt("estadoActual", estadoActual)?.apply()
                            }
                        } else if (estadoActual == 0) {
                            if (watchableZone[0].plazasPl != 0) {
                                initNotis(false)
                                estadoActual = 1
                                if (sp != null) {
                                    sp.edit()?.putInt("estadoActual", estadoActual)?.apply()
                                }
                            }
                        }
                    } else if (sizeOfCar == 0 && Firebase.auth.currentUser.uid == userCar) {
                        if (estadoActual == 1) {
                            if (watchableZone[0].plazasGl == 0) {
                                initNotis(true)
                                estadoActual = 0
                                sp?.edit()?.putInt("estadoActual", estadoActual)?.apply()
                            }
                        } else if (estadoActual == 0) {
                            if (watchableZone[0].plazasGl != 0) {
                                initNotis(false)
                                estadoActual = 1
                                if (sp != null) {
                                    sp.edit()?.putInt("estadoActual", estadoActual)?.apply()
                                }
                            }
                        }

                    }
                }
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
    }


    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        fetchLocation()
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            return
        }

        mMap.isMyLocationEnabled = true
        mMap.setOnMyLocationButtonClickListener(this)
        mMap.setOnMyLocationClickListener(this)
        fusedLocationClient.requestLocationUpdates(
            locationRequest,
            locationCallback,
            Looper.getMainLooper()
        )



        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            val success = googleMap.setMapStyle(
                MapStyleOptions.loadRawResourceStyle(
                    context, R.raw.map_style
                )
            )
            if (!success) {
                Log.e(TAG, "Style parsing failed.")
            }
        } catch (e: Resources.NotFoundException) {
            Log.e(TAG, "Can't find style. Error: ", e)
        }
        parkingMarker = mMap.addMarker(MarkerOptions().position(LatLng(-35.2568, -12.367)))
        parkingMarker.isVisible = false
        putParkingMarker()
        mapViewModel.updateGM(mMap)

    }

    private fun initNotis(ocupada: Boolean) {
        Log.e("watchZone", "init notis")
        val intent = Intent(context, NotificationDisabeler::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val resultPendingIntent: PendingIntent? = TaskStackBuilder.create(context).run {
            /*  context?.getSharedPreferences("watchZone", Context.MODE_PRIVATE)?.edit()
                  ?.putString("zoneID", "")?.apply()*/

            // Add the intent, which inflates the back stack
            addNextIntentWithParentStack(intent)
            // Get the PendingIntent containing the entire back stack
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT)
        }
        var builder = context?.let {
            NotificationCompat.Builder(it, 0.toString())
                .setSmallIcon(R.drawable.googleg_standard_color_18)
                .setContentTitle(getString(R.string.notificationHeader))
                .setContentText(getString(R.string.notificationDescription))
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(getString(R.string.notificationDescription))
                )
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setContentIntent(resultPendingIntent)
                .setAutoCancel(true)
        }
        if (!ocupada) {
            builder = context?.let {
                NotificationCompat.Builder(it, 0.toString())
                    .setSmallIcon(R.drawable.googleg_standard_color_18)
                    .setContentTitle(getString(R.string.notificationHeaderFree))
                    .setContentText(getString(R.string.notificationDescriptionFree))
                    .setStyle(
                        NotificationCompat.BigTextStyle()
                            .bigText(getString(R.string.notificationDescriptionFree))
                    )
                    .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                    .setContentIntent(resultPendingIntent)
                    .setAutoCancel(true)
            }
        }
        val time = Timer()
        val p = timerTask {
            with(context?.let { NotificationManagerCompat.from(it) }) {
                // notificationId is a unique int for each notification that you must define
                if (builder != null) {
                    Log.e("watchZone", "notified")
                    this?.notify(1, builder.build())
                }
            }
        }
        time.schedule(p, 100)
    }


    override fun onMyLocationClick(location: Location) {
        Toast.makeText(context, "Current location:\n$location", Toast.LENGTH_LONG)
            .show()
    }

    override fun onMyLocationButtonClick(): Boolean {
        Toast.makeText(context, getString(R.string.locBttnClick), Toast.LENGTH_SHORT)
            .show()
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false
    }

    private fun getLocation(googleMap: GoogleMap) {
        val latLng =
            currentLocation?.let {
                LatLng(
                    currentLocation!!.latitude,
                    currentLocation!!.longitude
                )
            }
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(latLng))
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 18f))

        mapViewModel.updateCurrentLocation(
            LatLng(
                currentLocation!!.latitude,
                currentLocation!!.longitude
            )

        )
        if (currentLocation != null) {
            val range = sharedPreferences.getString("range", "1")
            val op = sharedPreferences.getBoolean("searchFZ", false)
            val sp = context?.getSharedPreferences("FavZone", Context.MODE_PRIVATE)
            val namefz = sp?.getString("FavZone", "")
            val userfz = sp?.getString("fzuserID", "")
            if (Firebase.auth.currentUser != null && userfz == Firebase.auth.currentUser.uid && op && namefz != "") {
                Log.e("zones", op.toString())
                Log.e("zones", "rango: $range")
                val lat = sp?.getFloat("fzlatitude", 0f)?.toDouble()
                val long = sp?.getFloat("fzlongitude", 0f)?.toDouble()
                Log.e("zones", "you get $lat $long")
                if (range != null) {
                    if (lat != null) {
                        if (long != null) {
                            mapViewModel.updateZonesLoc(range.toInt(), lat, long)
                        }
                    }
                }
            } else {
                if (range != null) {
                    mapViewModel.updateZones(range.toInt())
                }
            }
            Log.e("api", "updating zones in getLocation")
            mapViewModel.zones.observe(viewLifecycleOwner, Observer {
                if (it != null) {
                    for (zone in it) {
                        val markerLocation = LatLng(zone.lat!!, zone.long!!)
                        val title =
                            "${zone.id} ${zone.plazasLibres}/${zone.plazasTotales}"
                        mMap.addMarker(
                            MarkerOptions().position(markerLocation).title(title)
                        )
                    }
                }
            })
        }
    }

    private fun fetchLocation() {
        if (context?.let {
                ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.ACCESS_FINE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED && context?.let {
                ActivityCompat.checkSelfPermission(
                    it, Manifest.permission.ACCESS_COARSE_LOCATION
                )
            } !=
            PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ), permissionCode
            )
            return

        }

        val task = fusedLocationClient.lastLocation
        task.addOnSuccessListener { location ->
            if (location != null) {
                currentLocation = location
                Toast.makeText(
                    context, currentLocation!!.latitude.toString() + "" +
                            currentLocation!!.longitude, Toast.LENGTH_SHORT
                ).show()
                getLocation(mMap)
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String?>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            permissionCode -> if (grantResults.isNotEmpty() && grantResults[0] ==
                PackageManager.PERMISSION_GRANTED
            ) {
                Toast.makeText(
                    context,
                    "Permissions Granted, if the map is not loading reload the app",
                    Toast.LENGTH_SHORT
                )
                    .show()
                fetchLocation()
            }
        }
    }

}
