package com.inchko.parkfinder.ui.proflie.cutomizeProfile.rvcp

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.cpViewModel

class cpHolder(inflater: LayoutInflater, parent: ViewGroup, v: View, vm: cpViewModel) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.car_card, parent, false
        )
    ), View.OnClickListener {
    private val cpvm = vm
    private var modelView: TextView? = null
    private var typeView: TextView? = null
    private var sizeView: TextView? = null
    private var modifyPOI: Button? = null
    private var delete: ImageButton? = null
    private var edit: ImageButton? = null


    init {
        modelView = itemView.findViewById(R.id.ccModelo)
        typeView = itemView.findViewById(R.id.ccTipoCoche)
        sizeView = itemView.findViewById(R.id.ccTamañoCoche)
        modifyPOI = itemView.findViewById(R.id.ccFav)
        edit = itemView.findViewById(R.id.ccEdit)
        delete = itemView.findViewById(R.id.ccDelete)
        v.setOnClickListener(this)
        modifyPOI?.setOnClickListener {
            Log.e("cp", "favorite clicked")
        }
    }

    fun bind(car: Vehicles) {
        modelView?.text = car.model
        if (car.type == 0) {//0 = car, 1 = moto
            typeView?.text = itemView.context.getString(R.string.car)
            if (car.size == 0) //0= Grande 1= pequeño
                sizeView?.text = itemView.context.getString(R.string.big)
            else sizeView?.text = itemView.context.getString(R.string.little)
        } else {
            typeView?.text = itemView.context.getString(R.string.moto)
            sizeView?.text = itemView.context.getString(R.string.little)
        }
        edit?.setOnClickListener {
            edit()
        }
        delete?.setOnClickListener {
            cpvm.deleteVehicles(Firebase.auth.currentUser.uid, car.id)
        }
    }


    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    fun edit() {
        Log.e("no", "ya si eso mañana")
    }

}