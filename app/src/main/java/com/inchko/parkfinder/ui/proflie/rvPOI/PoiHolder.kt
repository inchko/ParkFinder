package com.inchko.parkfinder.ui.proflie.rvPOI

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color.*
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
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.ui.proflie.ProfileViewModel
import com.inchko.parkfinder.ui.proflie.modifyPOI.ModifyPOI
import kotlin.math.asin
import kotlin.math.cos
import kotlin.math.sqrt
import kotlin.math.truncate

class PoiHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    v: View,
    rep: ProfileViewModel,
    cont: Context
) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.poi_card, parent, false
        )
    ), View.OnClickListener {
    private val vm = rep
    private val c = cont
    private var titleView: TextView? = null
    private var distanceView: TextView? = null
    private var locationView: TextView? = null
    private var deletePOI: ImageButton? = null
    private var modifyPOI: ImageButton? = null
    private var favPOI: ImageButton? = null


    init {
        titleView = itemView.findViewById(R.id.poiRVname)
        distanceView = itemView.findViewById(R.id.poiRVDistance)
        locationView = itemView.findViewById(R.id.poiRVloc)
        deletePOI = itemView.findViewById(R.id.poiDelete)
        modifyPOI = itemView.findViewById(R.id.poiModifiy)
        favPOI = itemView.findViewById(R.id.poiFav)
        v.setOnClickListener(this)
    }

    fun bind(poi: POI, loc: LatLng) {
        val sharedPref = c.getSharedPreferences("FavZone", Context.MODE_PRIVATE)
        val favZone = sharedPref.getString("FavZone", "")
        if (favZone != "" && favZone == poi.id)
            favPOI?.setImageResource(android.R.drawable.btn_star_big_on)
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
        modifyPOI?.setOnClickListener {
            val intent = Intent(itemView.context, ModifyPOI::class.java)
            intent.putExtra("userID", Firebase.auth.currentUser.uid)
            intent.putExtra("lat", poi.lat)
            intent.putExtra("long", poi.long)
            intent.putExtra("nombre", poi.nombre)
            intent.putExtra("location", poi.location)
            intent.putExtra("id", poi.id)
            itemView.context.startActivity(intent)
        }
        favPOI?.setOnClickListener {
            var helper = poi.id
            var lat = poi.lat.toFloat()
            var long = poi.long.toFloat()
            Log.e("zones", "$lat, $long")
            if (favZone == poi.id) {
                helper = ""
                lat = 0f
                long = 0f
            }
            val sharedPrefs =
                c.getSharedPreferences("FavZone", Context.MODE_PRIVATE) ?: return@setOnClickListener
            with(sharedPrefs.edit()) {
                putString("FavZone", helper)
                putFloat("fzlatitude", lat)
                putFloat("fzlongitude", long)
                apply()
            }

        }


    }


    override fun onClick(p0: View?) {
    }

    private fun distance(cl: LatLng, zl: LatLng): Double {
        val p = 0.017453292519943295;    // PI / 180
        val a = 0.5 - cos((zl.latitude - cl.latitude) * p) / 2 +
                cos(cl.latitude * p) * cos(zl.latitude * p) *
                (1 - cos((zl.longitude - cl.longitude) * p)) / 2;

        return 12742 * asin(sqrt(a)); // 2 * R; R = 6371 km R is radius of earth
    }
}
