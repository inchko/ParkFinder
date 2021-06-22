package com.inchko.parkfinder.ui.proflie.rvFavZones


import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.ui.proflie.ProfileViewModel
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.truncate

class fzHolder(inflater: LayoutInflater, parent: ViewGroup, v: View, rep: ProfileViewModel) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.favzone_card, parent, false
        )
    ), View.OnClickListener {
    private val vm = rep
    private var titleView: TextView? = null
    private var distanceView: TextView? = null
    private var locationView: TextView? = null
    private var plazasView: TextView? = null
    private var tipoView: TextView? = null
    private var deleteFZ: ImageButton? = null


    init {
        titleView = itemView.findViewById(R.id.fzRVname)
        distanceView = itemView.findViewById(R.id.fzRVDistance)
        locationView = itemView.findViewById(R.id.fzRVloc)
        plazasView = itemView.findViewById(R.id.fzZonaslibres)
        tipoView = itemView.findViewById(R.id.fzRVtipo)
        deleteFZ = itemView.findViewById(R.id.fzDelete)

        v.setOnClickListener(this)
    }

    fun bind(fz: FavZone, loc: LatLng) {
        Log.e("holder", "binding")
        titleView?.text = fz.zoneID
        var dis = 0.0
        dis = truncate(
            (distance(
                loc,
                LatLng(fz.lat.toDouble(), fz.long.toDouble())
            ) * 1000)
        )
        var text = dis.toString() + " m"
        if (dis > 2000) {
            dis /= 1000
            text = dis.toString() + " Km"
        }

        distanceView?.text = text
        locationView?.text = fz.location
        val plazas = itemView.context.getString(R.string.totalSpots) + fz.plazasTotales.toString()
        plazasView?.text = plazas
        tipoView?.text = itemView.context.getString(R.string.automoviles)
        if (fz.tipo == 1) tipoView?.text = itemView.context.getString(R.string.motocycle)
        deleteFZ?.setOnClickListener {
            vm.removeFZ(fz.userID, fz.id)

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
