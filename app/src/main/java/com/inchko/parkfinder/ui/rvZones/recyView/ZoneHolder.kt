package com.inchko.parkfinder.ui.rvZones.recyView

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.ui.rvZones.RvZoneViewModel
import kotlin.math.*


class ZoneHolder(inflater: LayoutInflater, parent: ViewGroup, v: View, viewModel: RvZoneViewModel) :
    RecyclerView.ViewHolder(
        inflater.inflate(
            R.layout.zone_card, parent, false
        )
    ), View.OnClickListener {
    private val vm = viewModel
    private var titleView: TextView? = null
    private var distanceView: TextView? = null
    private var typeView: TextView? = null
    private var zlView: TextView? = null
    private var zgView: TextView? = null
    private var zpView: TextView? = null
    private var addFZ: ImageButton? = null
    private var number: TextView? = null
    private var image: ImageView? = null
    private var placeImage: ImageView? = null
    private lateinit var placesClient: PlacesClient


    init {
        titleView = itemView.findViewById(R.id.RVid)
        distanceView = itemView.findViewById(R.id.RVDistance)
        typeView = itemView.findViewById(R.id.RVTipo)
        zlView = itemView.findViewById(R.id.RVzonasLibres)
        zgView = itemView.findViewById(R.id.RVzonasGrandes)
        zpView = itemView.findViewById(R.id.RVzonasPeq)
        addFZ = itemView.findViewById(R.id.RVFav)
        number = itemView.findViewById(R.id.rvNumber)
        image = itemView.findViewById(R.id.typeCar)
        placeImage = itemView.findViewById(R.id.placeImage)
        v.setOnClickListener(this)
        // Initialize the SDK
        placesClient = Places.createClient(itemView.context)

    }

    fun bind(z: Zone, loc: LatLng, num: Int) {
        Log.e("holder", "binding")
        titleView?.text = z.id
        number?.text = num.toString()

        var dis: Double = truncate(z.distancia!! * 1000)
        var text = "$dis m"
        if (dis > 2000) {
            dis /= 1000
            text = "$dis Km"
        }
        distanceView?.text = text

        if (z.tipo == 1) {
            typeView?.text = itemView.context.getString(R.string.motocycle)
            image?.setImageResource(R.drawable.ic_baseline_two_wheeler_24)
            val zonaslibres =
                itemView.context.getString(R.string.motoSpots) + z.plazasMl.toString() + "/" + z.plazasTotales.toString()
            zlView?.text = zonaslibres
            zgView?.text = ""
            zpView?.text = ""
        } else {
            val zonaslibres =
                itemView.context.getString(R.string.motoSpots) + z.plazasMl.toString() + "/" + z.plazasMoto.toString()
            zlView?.text = zonaslibres
            typeView?.text = itemView.context.getString(R.string.automoviles)
            image?.setImageResource(R.drawable.ic_baseline_directions_car_24)
            val zonasGrandes =
                itemView.context.getString(R.string.bigSpots) + z.plazasGl.toString() + "/" + z.plazasGrandes.toString()
            zgView?.text = zonasGrandes
            val zonasPeq =
                itemView.context.getString(R.string.lilSpots) + z.plazasPl.toString() + "/" + z.plazasPeq.toString()
            zpView?.text = zonasPeq
        }
        getPhoto(z)
        if (Firebase.auth.currentUser == null) {
            addFZ?.visibility = View.INVISIBLE
        } else {
            addFZ?.visibility = View.VISIBLE
            addFZ?.setOnClickListener {
                z.id?.let { it1 ->
                    z.plazasTotales?.let { it2 ->
                        z.tipo?.let { it3 ->
                            FavZone(
                                lat = z.lat.toString(),
                                long = z.long.toString(),
                                userID = Firebase.auth.currentUser.uid,
                                zoneID = it1,
                                location = "ph",
                                plazasTotales = it2,
                                tipo = it3,
                                id = ""
                            )
                        }
                    }
                }?.let { it2 -> vm.addZone(it2) }
            }
        }

    }

    override fun onClick(p0: View?) {
        Log.d("RecyclerView", "CLICK!")
    }

    private fun getPhoto(z: Zone) {
        // Define a Place ID.
        val placeId = z.placeID

// Specify fields. Requests for photos must always have the PHOTO_METADATAS field.
        val fields = listOf(Place.Field.PHOTO_METADATAS)

// Get a Place object (this example uses fetchPlace(), but you can also use findCurrentPlace())
        val placeRequest = placeId?.let { FetchPlaceRequest.newInstance(it, fields) }

        if (placeRequest != null) {
            placesClient.fetchPlace(placeRequest)
                .addOnSuccessListener { response: FetchPlaceResponse ->
                    val place = response.place

                    // Get the photo metadata.
                    val metada = place.photoMetadatas
                    if (metada == null || metada.isEmpty()) {
                        Log.e("PHOTO", "No photo metadata.")
                        return@addOnSuccessListener
                    }
                    val photoMetadata = metada.first()

                    // Get the attribution text.
                    val attributions = photoMetadata?.attributions

                    // Create a FetchPhotoRequest.
                    val photoRequest = FetchPhotoRequest.builder(photoMetadata)
                        .setMaxWidth(500) // Optional.
                        .setMaxHeight(300) // Optional.
                        .build()
                    placesClient.fetchPhoto(photoRequest)
                        .addOnSuccessListener { fetchPhotoResponse: FetchPhotoResponse ->
                            val bitmap = fetchPhotoResponse.bitmap
                            placeImage?.setImageBitmap(bitmap)
                        }.addOnFailureListener { exception: Exception ->
                            if (exception is ApiException) {
                                Log.e("PHOTO", "Place not found: " + exception.message)
                                val statusCode = exception.statusCode
                                TODO("Handle error with given status code.")
                            }
                        }
                }
        }
    }
}