package com.inchko.parkfinder.ui.proflie.rvFavZones

import com.inchko.parkfinder.ui.proflie.rvPOI.PoiHolder
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.ui.proflie.ProfileViewModel
import com.inchko.parkfinder.utils.inflate


class fzAdapter(
    private val favZones: List<FavZone>,
    private val vm: ProfileViewModel,
    private val loc: LatLng,
    private val listener: (FavZone) -> Unit
) : RecyclerView.Adapter<fzHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): fzHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = parent.inflate(R.layout.poi_card, false)
        return fzHolder(inflater, parent, inflatedView,vm)


    }

    override fun onBindViewHolder(holder: fzHolder, position: Int) {
        val fz = favZones[position]
        holder.bind(fz, loc)
        holder.itemView.setOnClickListener { listener(fz) }
    }

    override fun getItemCount(): Int {
        return favZones.size
    }
}


