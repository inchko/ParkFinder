package com.inchko.parkfinder.ui.proflie.cutomizeProfile.rvcp

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.CustomizeProfile
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.cpViewModel
import com.inchko.parkfinder.utils.inflate

class cpAdapter(
    private val cars: List<Vehicles>,
    private val vm: cpViewModel,
    private val c: Context,
    private val a: CustomizeProfile,
    private val listener: (Vehicles) -> Unit
) : RecyclerView.Adapter<cpHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): cpHolder {
        val inflater = LayoutInflater.from(parent.context)
        val inflatedView = parent.inflate(R.layout.car_card, false)
        Log.e("holder", "Holding poi")
        return cpHolder(inflater, parent, inflatedView, vm, c, a)

    }

    override fun onBindViewHolder(holder: cpHolder, position: Int) {
        val car = cars[position]
        holder.bind(car)
        holder.itemView.setOnClickListener { listener(car) }
    }

    override fun getItemCount(): Int {
        return cars.size
    }
}