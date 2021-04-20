package com.inchko.parkfinder.ui.rvZones.recyView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.ui.rvZones.RvZoneViewModel
import kotlin.math.*


class ZoneHolder(inflater: LayoutInflater, parent: ViewGroup, v: View, viewModel: RvZoneViewModel) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.zone_card, parent, false
        )
    ), View.OnClickListener {
    private val vm = viewModel
    private var titleView: TextView? = null
    private var distanceView: TextView? = null
    private var typeView: TextView? = null
    private var zlView: TextView? = null
    private var zgView: TextView? = null
    private var zpView: TextView? = null
    private var addFZ: ImageButton? = null


    init {
        titleView = itemView.findViewById(R.id.RVid)
        distanceView = itemView.findViewById(R.id.RVDistance)
        typeView = itemView.findViewById(R.id.RVTipo)
        zlView = itemView.findViewById(R.id.RVzonasLibres)
        zgView = itemView.findViewById(R.id.RVzonasGrandes)
        zpView = itemView.findViewById(R.id.RVzonasPeq)
        addFZ = itemView.findViewById(R.id.RVFav)
        v.setOnClickListener(this)
    }

    fun bind(z: Zone, loc: LatLng) {
        Log.e("holder", "binding")
        titleView?.text = z.id
        //   val text = z.lat?.let { z.long?.let { it1 -> LatLng(it, it1) } }
        //    ?.let { truncate((distance(loc, it) * 1000)).toString() } + " m"
        distanceView?.text = truncate(z.distancia!! * 1000).toString()

        if (z.tipo == 1) {
            typeView?.text = itemView.context.getString(R.string.motocycle)
            val zonaslibres = "Plazas Motos: ${z.plazasMl}/${z.plazasTotales}"
            zlView?.text = zonaslibres
            zgView?.text = ""
            zpView?.text = ""
        } else {
            val zonaslibres = "Plazas Motos: ${z.plazasMl}/${z.plazasMoto}"
            zlView?.text = zonaslibres
            typeView?.text = itemView.context.getString(R.string.automoviles)
            val zonasGrandes = "Plazas Grandes: ${z.plazasGl}/${z.plazasGrandes}"
            zgView?.text = zonasGrandes
            val zonasPeq = "Plazas Pequeñas: ${z.plazasPl}/${z.plazasPeq}"
            zpView?.text = zonasPeq
        }
        if (Firebase.auth.currentUser == null) {
            addFZ?.visibility = View.INVISIBLE
        } else {
            addFZ?.visibility = View.VISIBLE
            addFZ?.setOnClickListener {
                z.id?.let { it1 ->
                    z.plazasTotales?.let { it2 ->
                        z.tipo?.let { it3 ->
                            FavZone(
                                lat = z.lat.toString(),
                                long = z.long.toString(),
                                userID = Firebase.auth.currentUser.uid,
                                zoneID = it1,
                                location = "ph",
                                plazasTotales = it2,
                                tipo = it3,
                                id = ""
                            )
                        }
                    }
                }?.let { it2 -> vm.addZone(it2) }
            }
        }

    }

    override fun onClick(p0: View?) {
        Log.d("RecyclerView", "CLICK!")
    }

    //calculates distance given two coords
    fun distance(cl: LatLng, zl: LatLng): Double {
        val p = 0.017453292519943295;    // PI / 180
        val a = 0.5 - cos((zl.latitude - cl.latitude) * p) / 2 +
                cos(cl.latitude * p) * cos(zl.latitude * p) *
                (1 - cos((zl.longitude - cl.longitude) * p)) / 2;

        return 12742 * asin(sqrt(a)); // 2 * R; R = 6371 km R is radius of earth
    }
}