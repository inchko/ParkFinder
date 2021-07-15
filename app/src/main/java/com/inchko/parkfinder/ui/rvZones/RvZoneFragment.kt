package com.inchko.parkfinder.ui.rvZones

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.media.AudioManager
import android.media.SoundPool
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.speech.tts.TextToSpeech
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.preference.PreferenceManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.PolylineOptions
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.google.maps.android.PolyUtil
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Zone
import com.inchko.parkfinder.network.models.DirectionsResponse
import com.inchko.parkfinder.ui.map.MapViewModel
import com.inchko.parkfinder.ui.rvZones.recyView.ZoneAdapter
import com.yariksoffice.lingver.Lingver
import dagger.hilt.android.AndroidEntryPoint
import retrofit2.Response
import java.util.*


@AndroidEntryPoint
class RvZoneFragment : Fragment(), TextToSpeech.OnInitListener {

    val rzViewModel: RvZoneViewModel by viewModels()
    val mapViewModel: MapViewModel by activityViewModels()
    private var polyline: PolylineOptions? = null
    private var zones: List<Zone>? = null
    private lateinit var button: ImageButton

    private var num = 0
    private var sort: Boolean = true //false = distance true = plazas libres

    //voice to text
    private val recordAuidoCode = 1001
    private lateinit var micButton: ImageButton
    private var clicked = false
    private lateinit var speechRecognizer: SpeechRecognizer


    //text to speech
    private lateinit var voice: TextToSpeech

    //sound
    private lateinit var soundPool: SoundPool

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // mapViewModel.updateZones()
        mapViewModel.zones.observe(viewLifecycleOwner, Observer { value: List<Zone>? ->
            value?.let {
                zones = it
                view?.let { it1 -> initRV(it1) }
            }
        })
        val root = inflater.inflate(R.layout.fragment_rv, container, false)
        return root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        button = view.findViewById(R.id.RVReload)
        button.setOnClickListener {
            closeFragment()
        }
        micButton = view.findViewById(R.id.rvVoice)

        voice = TextToSpeech(context, this)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool.load(context, R.raw.mic_on, 1)
        soundPool.load(context, R.raw.mic_off, 1)

        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        val speechRecognizerIntent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //activity to recoginze voice
        speechRecognizerIntent.putExtra( //extra options
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            Locale.getDefault()
        );
        Log.e("voice", "${Locale.getDefault()}")
        /*
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE,
            Lingver.getInstance().getLocale()
        );*/
        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {}
            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
                sort = sharedPreferences.getBoolean("orderZones", false)
                var tempZones = zones?.sortedBy { it.distancia }
                if (sort) tempZones = zones?.sortedByDescending { it.plazasLibres }

                micButton.setImageResource(R.drawable.googleg_standard_color_18)
                soundPool.play(2, 1F, 1F, 0, 0, 1F)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                clicked = false
                if (data?.get(0)?.contains(getString(R.string.voiceTellZones)) == true ||
                    data?.get(0)?.contains(getString(R.string.wherePark)) == true ||
                    data?.get(0)?.contains(getString(R.string.voiceNearby)) == true
                ) {
                    if (tempZones != null) {
                        var count = 1;
                        for (z in tempZones) {
                            Log.e("voice", "${z.id}")
                            val d = (z.distancia?.times(1000))?.toInt()
                            var text =
                                " $count. ${z.id} ${getString(R.string.voiceAt)} $d ${getString(R.string.voiceMetersW)} ${z.plazasLibres} ${
                                    getString(R.string.voiceFreeSpots)
                                }"
                            if (z.plazasLibres == 1) text =
                                " $count. ${z.id} ${getString(R.string.voiceAt)} $d ${getString(R.string.voiceMetersW)} ${
                                    getString(R.string.voiceFreeSpot)
                                }"
                            voice.speak(
                                text,
                                TextToSpeech.QUEUE_ADD,
                                null,
                                ""
                            )
                            count++
                            //   while (voice.isSpeaking) {//wait to each zone is said, it seems its not needed with queue_add}
                            //  }
                        }
                    }
                } else if (data?.get(0)?.contains(getString(R.string.voiceFirst), true) == true ||
                    data?.get(0)?.contains(getString(R.string.one), true) == true
                ) {
                    if (zones?.size!! > 0) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(0)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(0)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(0)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceSecond), true) == true || data?.get(0)
                        ?.contains(getString(R.string.two), true) == true
                ) {
                    if (zones?.size!! > 1) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(1)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(1)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(1)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceThird), true) == true || data?.get(0)
                        ?.contains(getString(R.string.three), true) == true
                ) {
                    if (zones?.size!! > 2) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(2)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(2)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(2)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceFourth), true) == true || data?.get(0)
                        ?.contains(getString(R.string.four), true) == true
                ) {
                    if (zones?.size!! > 3) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(3)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(3)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(3)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceFitfh), true) == true || data?.get(0)
                        ?.contains(getString(R.string.five), true) == true
                ) {
                    if (zones?.size!! > 4) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(4)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(4)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(4)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceSixth), true) == true || data?.get(0)
                        ?.contains(getString(R.string.six), true) == true
                ) {
                    if (zones?.size!! > 5) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(5)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(5)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(5)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceSeventh), true) == true || data?.get(0)
                        ?.contains(getString(R.string.seven), true) == true
                ) {
                    if (zones?.size!! > 6) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(6)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(6)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(6)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceEight), true) == true || data?.get(0)
                        ?.contains(getString(R.string.eight), true) == true
                ) {
                    if (zones?.size!! > 7) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(7)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(7)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(7)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceNineth), true) == true || data?.get(0)
                        ?.contains(getString(R.string.nine), true) == true
                ) {
                    if (zones?.size!! > 8) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(8)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(8)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(8)?.let { listenerFun(it) }
                    }
                } else if (data?.get(0)
                        ?.contains(getString(R.string.voiceTenth), true) == true || data?.get(0)
                        ?.contains(getString(R.string.ten), true) == true
                ) {
                    if (zones?.size!! > 9) {
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(9)?.id}",
                            Toast.LENGTH_SHORT
                        ).show()
                        voice.speak(
                            "${getString(R.string.voiceCreateRoute)} ${tempZones?.get(9)?.id}",
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        tempZones?.get(9)?.let { listenerFun(it) }
                    }
                } else {
                    var understood = false;
                    for (z in zones!!) {
                        if (z.id?.let { data?.get(0)?.compareTo(it, true) } == 0) {
                            understood = true
                            Toast.makeText(
                                context,
                                "${getString(R.string.voiceCreateRoute)} ${z.id}",
                                Toast.LENGTH_SHORT
                            ).show()
                            voice.speak(
                                "${getString(R.string.voiceCreateRoute)} ${z.id}",
                                TextToSpeech.QUEUE_ADD,
                                null,
                                ""
                            )
                            listenerFun(z)
                        }
                    }
                    if (!understood)
                        Toast.makeText(
                            context,
                            "${getString(R.string.voiceIDK)} ${
                                data?.get(0)?.toLowerCase(Locale.ROOT)
                            }",
                            Toast.LENGTH_LONG
                        )
                            .show()
                }

            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        micButton.setOnClickListener {
            if (context?.let {
                    ContextCompat.checkSelfPermission(
                        it,
                        Manifest.permission.RECORD_AUDIO
                    )
                } != PackageManager.PERMISSION_GRANTED) {
                checkPermission();
            }
            if (clicked) {
                soundPool.play(2, 1F, 1F, 0, 0, 1F)
                micButton.setImageResource(R.drawable.googleg_standard_color_18)
                speechRecognizer.stopListening()
                clicked = false

            } else {
                soundPool.play(1, 1F, 1F, 0, 0, 1F)
                micButton.setImageResource(R.drawable.googleg_disabled_color_18)
                speechRecognizer.startListening(speechRecognizerIntent)
                clicked = true
            }
        }

        //  initRV(view)
        // RecyclerView node initialized here
    }

    fun initRV(view: View) {
        val rv: RecyclerView = view.findViewById(R.id.recyclerViewZones)
        rv.apply {

            layoutManager = LinearLayoutManager(activity,LinearLayoutManager.HORIZONTAL,false)

            val sharedPreferences = PreferenceManager.getDefaultSharedPreferences(activity)
            val sp = context?.getSharedPreferences("vehicle", Context.MODE_PRIVATE)
            sort = sharedPreferences.getBoolean("orderZones", false)
            val vehicle = sharedPreferences.getBoolean("showVehicle", false)
            val userCar = sp?.getString("caruserID", "")
            val typeVehicle = sp?.getInt("type", -1)

            if (!sort) {
                adapter = zones?.let { lz ->
                    Log.e("holder", "Zones loaded")
                    val zonesFinal = mutableListOf<Zone>()
                    zonesFinal.addAll(lz)
                    if (Firebase.auth.currentUser != null && userCar == Firebase.auth.currentUser.uid && vehicle && typeVehicle != -1) {
                        zonesFinal.clear()
                        for (z in lz) {
                            if (z.tipo == typeVehicle)
                                zonesFinal.add(z)
                        }
                    }
                    mapViewModel.currentLocation?.let { cl ->
                        ZoneAdapter(
                            zonesFinal.sortedBy { it.distancia }, rzViewModel,
                            cl
                        ) { zone ->//Listener, add your actions here
                            listenerFun(zone)
                        }
                    }
                };
            } else {
                adapter = zones?.let { lz ->
                    Log.e("holder", "Zones loaded")
                    val zonesFinal = mutableListOf<Zone>()
                    zonesFinal.addAll(lz)
                    if (Firebase.auth.currentUser != null && userCar == Firebase.auth.currentUser.uid && vehicle && typeVehicle != -1) {
                        zonesFinal.clear()
                        for (z in lz) {
                            if (z.tipo == typeVehicle)
                                zonesFinal.add(z)
                        }
                    }
                    mapViewModel.currentLocation?.let {
                        ZoneAdapter(
                            zonesFinal.sortedByDescending { it.plazasLibres }, rzViewModel,
                            it
                        ) { zone ->//Listener, add your actions here
                            listenerFun(zone)
                        }
                    }
                };
            }
        }
    }


    //closes the fragment
    fun closeFragment() {
        activity?.supportFragmentManager?.saveFragmentInstanceState(this)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit();
        rzViewModel.response.value = null
    }


    private fun drawPolyline(response: Response<DirectionsResponse>) {
        val shape = response.body()?.routes?.get(0)?.overviewPolyline?.points
        polyline = PolylineOptions()
            .addAll(PolyUtil.decode(shape))
            .width(8f)
            .color(Color.RED)
        mapViewModel.mMap?.addPolyline(polyline)
        //mapViewModel.mMap?.clear()
    }

    private fun listenerFun(zone: Zone) {
        mapViewModel.mMap?.animateCamera(CameraUpdateFactory.newLatLng(zone.lat?.let { it1 ->
            zone.long?.let { it2 ->
                LatLng(
                    it1, it2
                )
            }
        }))
        mapViewModel.mMap?.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                zone.lat?.let { it1 ->
                    zone.long?.let { it2 ->
                        LatLng(
                            it1, it2
                        )
                    }
                },
                19f
            )
        )
        zone.lat?.let { it1 ->
            zone.long?.let { it2 ->
                LatLng(
                    it1,
                    it2
                )
            }
        }?.let { it2 ->
            rzViewModel.getDirections(
                mapViewModel.currentLocation!!,
                it2
            )
        }
        rzViewModel.response.observe(
            viewLifecycleOwner,
            Observer { value: Response<DirectionsResponse>? ->
                value?.let {
                    // 1 zona libre, 0 zona ocupada
                    var ocupada = 1
                    if (zone.plazasLibres == 0) {
                        ocupada = 0
                        voice.speak(
                            getString(R.string.fullZoneAnnounce),
                            TextToSpeech.QUEUE_FLUSH,
                            null,
                            ""
                        )
                        val builder = context?.let { it1 -> AlertDialog.Builder(it1) }
                        builder?.setTitle(getString(R.string.fullZoneHead))
                        builder?.setMessage(getString(R.string.fullZoneDesc))
                        builder?.setPositiveButton(android.R.string.yes) { dialog, which ->
                            drawPolyline(it)
                            val spw = context?.getSharedPreferences(
                                "watchZone",
                                Context.MODE_PRIVATE
                            ) ?: return@setPositiveButton
                            with(spw.edit()) {
                                putString("zoneID", zone.id)
                                putString("zoneUserID", Firebase.auth.currentUser.uid)
                                putInt("estadoActual", ocupada)
                                apply()
                            }
                            closeFragment()
                        }
                        builder?.setNegativeButton(android.R.string.no) { dialog, which ->
                            val loc = mapViewModel.currentLocation
                            if (loc != null) {
                                mapViewModel.mMap?.animateCamera(
                                    CameraUpdateFactory.newLatLng(
                                        LatLng(loc.latitude, loc.longitude)
                                    )
                                )
                            }
                            if (loc != null) {
                                mapViewModel.mMap?.animateCamera(
                                    CameraUpdateFactory.newLatLngZoom(
                                        LatLng(loc.latitude, loc.longitude), 19f
                                    )
                                )
                            }
                        }
                        builder?.show()
                    } else {
                        drawPolyline(it)
                        val spw = context?.getSharedPreferences(
                            "watchZone",
                            Context.MODE_PRIVATE
                        ) ?: return@Observer
                        if (Firebase.auth.currentUser != null) {
                            with(spw.edit()) {
                                putString("zoneID", zone.id)
                                putString("zoneUserID", Firebase.auth.currentUser.uid)
                                putInt("estadoActual", ocupada)
                                apply()
                            }
                        }
                        val test = spw.getString("zoneID", "")
                        Log.e(
                            "watchZone",
                            "Watching zone : ${zone.id} value of the zone: $test"
                        )
                        while (voice.isSpeaking) { //Allow for the voice to stop speaking before closing the fragment
                        }
                        closeFragment()
                    }
                }
            })

    }


    private fun checkPermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            activity?.let {
                ActivityCompat.requestPermissions(
                    it,
                    arrayOf(Manifest.permission.RECORD_AUDIO),
                    recordAuidoCode
                )
            }
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == recordAuidoCode && grantResults.isNotEmpty()) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED)
                Toast.makeText(context, "Permission Granted", Toast.LENGTH_SHORT).show();
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        speechRecognizer.destroy()
        voice.stop()
        voice.shutdown()
    }

    override fun onInit(p0: Int) {
        var len = Lingver.getInstance().getLocale()
        if (len.toString() == "es") len = Locale("es", "es")
        voice.language = len
        Log.e("voice", "$len")
        Log.e("voice", "language = ${voice.language}")
    }
}
