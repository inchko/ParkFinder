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
import android.text.method.LinkMovementMethod
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.inchko.parkfinder.R
import com.yariksoffice.lingver.Lingver
import java.util.*


class InformationFragment : Fragment() {

    private lateinit var informationViewModel: InformationViewModel
    private lateinit var polLink: TextView

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        informationViewModel =
            ViewModelProvider(this).get(InformationViewModel::class.java)

        val root = inflater.inflate(R.layout.fragment_information, container, false)
        polLink = root.findViewById(R.id.InformationLink)
        polLink.movementMethod = LinkMovementMethod.getInstance();
        return root
    }
}