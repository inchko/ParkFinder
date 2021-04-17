package com.inchko.parkfinder.ui.proflie.rvPOI

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.utils.inflate


class PoiAdapter(private val POIS: List<POI> , private val loc:LatLng,   private val listener: (POI) -> Unit
) : RecyclerView.Adapter<PoiHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PoiHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = parent.inflate(R.layout.poi_card, false)
        Log.e("holder", "Holding poi")
        return PoiHolder(inflater, parent, inflatedView)

    }

    override fun onBindViewHolder(holder: PoiHolder, position: Int) {
        val poi = POIS[position]
        holder.bind(poi,loc)
        holder.itemView.setOnClickListener { listener(poi) }
    }

    override fun getItemCount(): Int {
        return POIS.size
    }
}


