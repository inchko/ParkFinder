package com.inchko.parkfinder.ui.rvZones.recyView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone

class ZoneHolder(inflater: LayoutInflater, parent: ViewGroup, v: View) : RecyclerView.ViewHolder(
inflater.inflate(
R.layout.zone_card, parent, false
)
), View.OnClickListener {

    private var titleView: TextView? = null


    init {
        titleView = itemView.findViewById(R.id.backgroundTop)
        v.setOnClickListener(this)
    }

    fun bind(z: Zone) {
        titleView?.text = z.id


    }

    override fun onClick(p0: View?) {
        Log.d("RecyclerView", "CLICK!")
    }
}