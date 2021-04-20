package com.inchko.parkfinder.ui.rvZones.recyView

import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.ui.rvZones.RvZoneViewModel
import com.inchko.parkfinder.utils.inflate


class ZoneAdapter(
    private val zones: List<Zone>,
    private val vm: RvZoneViewModel,
    private val loc: LatLng,
    private val listener: (Zone) -> Unit
) : RecyclerView.Adapter<ZoneHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = parent.inflate(R.layout.zone_card, false)
        Log.e("holder", "Holding")
        return ZoneHolder(inflater, parent, inflatedView, vm)

    }

    override fun onBindViewHolder(holder: ZoneHolder, position: Int) {
        val zone = zones[position]
        holder.bind(zone, loc)
        holder.itemView.setOnClickListener { listener(zone) }
    }

    override fun getItemCount(): Int {
        return zones.size
    }
}


