package com.inchko.parkfinder.ui.proflie.rvPOI

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.ui.proflie.ProfileViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.truncate

class PoiHolder(inflater: LayoutInflater, parent: ViewGroup, v: View, rep: ProfileViewModel) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.poi_card, parent, false
        )
    ), View.OnClickListener {
    private val vm = rep
    private var titleView: TextView? = null
    private var distanceView: TextView? = null
    private var locationView: TextView? = null
    private var deletePOI: ImageButton? = null


    init {
        titleView = itemView.findViewById(R.id.poiRVname)
        distanceView = itemView.findViewById(R.id.poiRVDistance)
        locationView = itemView.findViewById(R.id.poiRVloc)
        deletePOI = itemView.findViewById(R.id.poiDelete)
        v.setOnClickListener(this)
    }

    fun bind(poi: POI, loc: LatLng) {
        Log.e("holder", "binding")
        titleView?.text = poi.nombre
        val text = truncate(
            (distance(
                loc,
                LatLng(poi.lat.toDouble(), poi.long.toDouble())
            ) * 1000)
        ).toString() + " m"
        distanceView?.text = text
        locationView?.text = poi.location
        deletePOI?.setOnClickListener {
            vm.removePOI(poi.userID, poi.id)
            Log.e("rvpoi", "delete button clicked")
        }

    }


    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    private fun distance(cl: LatLng, zl: LatLng): Double {
        val p = 0.017453292519943295;    // PI / 180
        val a = 0.5 - cos((zl.latitude - cl.latitude) * p) / 2 +
                cos(cl.latitude * p) * cos(zl.latitude * p) *
                (1 - cos((zl.longitude - cl.longitude) * p)) / 2;

        return 12742 * asin(sqrt(a)); // 2 * R; R = 6371 km R is radius of earth
    }
}
