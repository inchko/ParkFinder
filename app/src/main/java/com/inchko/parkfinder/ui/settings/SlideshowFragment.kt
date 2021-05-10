package com.inchko.parkfinder.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inchko.parkfinder.R
import java.util.*


class SlideshowFragment : Fragment() {

    private lateinit var slideshowViewModel: SlideshowViewModel
    private val recordAuidoCode = 1001
    private lateinit var editText: EditText
    private lateinit var micButton: ImageButton
    private var clicked = false
    private lateinit var speechRecognizer: SpeechRecognizer

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        slideshowViewModel =
            ViewModelProvider(this).get(SlideshowViewModel::class.java)
        val root = inflater.inflate(R.layout.fragment_slideshow, container, false)
        val textView: TextView = root.findViewById(R.id.text_slideshow)
        slideshowViewModel.text.observe(viewLifecycleOwner, Observer {
            textView.text = it
        })
        if (context?.let {
                ContextCompat.checkSelfPermission(
                    it,
                    Manifest.permission.RECORD_AUDIO
                )
            } != PackageManager.PERMISSION_GRANTED) {
            checkPermission();
        }
        editText = root.findViewById(R.id.micTestText)
        micButton = root.findViewById(R.id.micTestButton)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context);
        val speechRecognizerIntent =
            Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH); //activity to recoginze voice
        speechRecognizerIntent.putExtra( //extra options
            RecognizerIntent.EXTRA_LANGUAGE_MODEL,
            RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
        );
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.getDefault()
        ); /*TODO Change this*/

        speechRecognizer.setRecognitionListener(object : RecognitionListener {
            override fun onReadyForSpeech(bundle: Bundle) {}
            override fun onBeginningOfSpeech() {
                editText.setText("")
                editText.setHint("Listening...")
            }

            override fun onRmsChanged(v: Float) {}
            override fun onBufferReceived(bytes: ByteArray) {}
            override fun onEndOfSpeech() {}
            override fun onError(i: Int) {}
            override fun onResults(bundle: Bundle) {
                micButton.setImageResource(R.drawable.googleg_standard_color_18)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                editText.setText(data!![0])
                clicked = false
                if (data[0] == "buscar zona") Toast.makeText(
                    context,
                    "Buscando zona",
                    Toast.LENGTH_SHORT
                ).show()
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        micButton.setOnClickListener {
            if (clicked) {
                micButton.setImageResource(R.drawable.googleg_standard_color_18)
                speechRecognizer.stopListening()
                clicked = false
            } else {
                micButton.setImageResource(R.drawable.common_google_signin_btn_icon_disabled)
                speechRecognizer.startListening(speechRecognizerIntent)
                clicked = true
            }
        }

        return root
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
    }
}