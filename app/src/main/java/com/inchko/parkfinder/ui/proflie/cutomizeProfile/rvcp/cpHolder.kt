package com.inchko.parkfinder.ui.proflie.cutomizeProfile.rvcp

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.FragmentManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.ui.proflie.ProfileFragment
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.CustomizeProfile
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.cpViewModel
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.modifyVehicle.ModifyVehicle

class cpHolder(
    inflater: LayoutInflater,
    parent: ViewGroup,
    v: View,
    vm: cpViewModel,
    con: Context,
    ac: CustomizeProfile
) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.car_card, parent, false
        )
    ), View.OnClickListener {
    private val c = con
    private val a = ac
    private val cpvm = vm
    private var modelView: TextView? = null
    private var typeView: TextView? = null
    private var sizeView: TextView? = null
    private var fav: ImageButton? = null
    private var delete: ImageButton? = null
    private var edit: ImageButton? = null


    init {
        modelView = itemView.findViewById(R.id.ccModelo)
        typeView = itemView.findViewById(R.id.ccTipoCoche)
        sizeView = itemView.findViewById(R.id.ccTamañoCoche)
        fav = itemView.findViewById(R.id.ccFav)
        edit = itemView.findViewById(R.id.ccEdit)
        delete = itemView.findViewById(R.id.ccDelete)
        v.setOnClickListener(this)
    }

    fun bind(car: Vehicles) {
        val sharedPref = c.getSharedPreferences("vehicle", Context.MODE_PRIVATE)
        val favCar = sharedPref.getString("FavCar", "")
        if (favCar != "" && favCar == car.id)
            fav?.setImageResource(android.R.drawable.btn_star_big_on)
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
            edit(car)
        }
        delete?.setOnClickListener {
            cpvm.deleteVehicles(Firebase.auth.currentUser.uid, car.id)
            a.recreate()
        }
        fav?.setOnClickListener {

            var helper = car.id
            var type = car.type
            var siz = car.size
            if (favCar == car.id) {
                helper = ""
                type = -1
                siz = -1
            }
            val sharedPrefs =
                c.getSharedPreferences("vehicle", Context.MODE_PRIVATE) ?: return@setOnClickListener
            with(sharedPrefs.edit()) {
                putString("FavCar", helper)
                putInt("type", type)
                putInt("size", siz)
                putString("caruserID", Firebase.auth.currentUser.uid)
                apply()
            }
            a.recreate()
        }
    }


    override fun onClick(p0: View?) {
        TODO("Not yet implemented")
    }

    private fun edit(car: Vehicles) {
        val intent = Intent(itemView.context, ModifyVehicle::class.java)
        intent.putExtra("id", car.id)
        intent.putExtra("model", car.model)
        intent.putExtra("size", car.size)
        intent.putExtra("type", car.type)
        itemView.context.startActivity(intent)

    }

}