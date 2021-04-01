package com.inchko.parkfinder.ui.rvZones.recyView

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.utils.inflate


class ZoneAdapter(private val zones: List<Zone> ,   private val listener: (Zone) -> Unit
) : RecyclerView.Adapter<ZoneHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ZoneHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = parent.inflate(R.layout.zone_card, false)
        return ZoneHolder(inflater, parent, inflatedView)

    }

    override fun onBindViewHolder(holder: ZoneHolder, position: Int) {
        val zone = zones[position]
        holder.bind(zone)
        holder.itemView.setOnClickListener { listener(zone) }
    }

    override fun getItemCount(): Int {
        return zones.size
    }
}


