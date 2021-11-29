package com.inchko.parkfinder.ui.proflie.addPoi

import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.viewModels
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI

class AddPoiDialog : DialogFragment() {

    private val apViewModel: addPoiViewModel by activityViewModels()
    private lateinit var button: Button
    private lateinit var name: EditText
    private lateinit var dir: EditText

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val root = inflater.inflate(R.layout.add_poi_dialog, container, false)
        name = root.findViewById(R.id.apdName)
        dir = root.findViewById(R.id.apdDirection)
        button = root.findViewById(R.id.addPoiDialogButton)
        return root
    }

    override fun onStart() {
        super.onStart()

        button.setOnClickListener {
            createPOI()
        }
        val width = (resources.displayMetrics.widthPixels * 0.85).toInt()
        getDialog()!!.getWindow()?.setBackgroundDrawableResource(R.drawable.cardback);
        dialog!!.window?.setLayout(width, ViewGroup.LayoutParams.WRAP_CONTENT)
    }

    private fun createPOI() {

        Log.e("addpoi", "add clicked")
        val poiDirection = dir.text
        val poiName = name.text.toString()
        val coords = getCoords(dir.text.toString())
        val poi = POI(
            id = "0,",
            nombre = poiName,
            userID = Firebase.auth.currentUser.uid,
            location = poiDirection.toString(),
            lat = coords.latitude.toString(),
            long = coords.longitude.toString()
        )
        apViewModel.addPOI(poi)
        finish()
    }

    private fun getCoords(dir: String): LatLng {
        val gc = Geocoder(activity)
        var result = gc.getFromLocationName(dir, 1)
        var count = 0
        while (result.isEmpty() || count < 20) {
            count++
            result = gc.getFromLocationName(dir, 1)
        }
        if (result.isEmpty()) {
            return (LatLng(2.41, 48.59))
        }
        return (LatLng(result.get(0).latitude, result.get(0).longitude))
    }

    private fun finish() {
        Toast.makeText(context, R.string.zonaAñadida, Toast.LENGTH_SHORT).show()
        activity?.recreate()!!
        activity?.supportFragmentManager?.saveFragmentInstanceState(this)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
    }
}