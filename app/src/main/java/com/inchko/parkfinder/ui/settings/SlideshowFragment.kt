package com.inchko.parkfinder.ui.settings

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
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
import com.yariksoffice.lingver.Lingver
import java.util.*


class SlideshowFragment : Fragment(), TextToSpeech.OnInitListener {

    private lateinit var slideshowViewModel: SlideshowViewModel

    //speech to text
    private val recordAuidoCode = 1001
    private lateinit var editText: EditText
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
        );
        speechRecognizerIntent.putExtra(
            RecognizerIntent.EXTRA_LANGUAGE,
            Locale.ENGLISH
        );


        voice = TextToSpeech(context, this)

        soundPool = SoundPool(6, AudioManager.STREAM_MUSIC, 0)
        soundPool.load(context, R.raw.mic_on, 1)
        soundPool.load(context, R.raw.mic_off, 1)


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
                soundPool.play(2, 1F, 1F, 0, 0, 1F)
                val data = bundle.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                editText.setText(data!![0])
                clicked = false
                if (data[0] == "buscar zona") Toast.makeText(
                    context,
                    "Buscando zona",
                    Toast.LENGTH_SHORT
                ).show()
                voice.speak(data[0], TextToSpeech.QUEUE_FLUSH, null, "")
            }

            override fun onPartialResults(bundle: Bundle) {}
            override fun onEvent(i: Int, bundle: Bundle) {}
        })
        micButton.setOnClickListener {
            if (clicked) {
                soundPool.play(2, 1F, 1F, 0, 0, 1F)
                micButton.setImageResource(R.drawable.googleg_standard_color_18)
                speechRecognizer.stopListening()
                clicked = false

            } else {
                soundPool.play(1, 1F, 1F, 0, 0, 1F)
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