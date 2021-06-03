package com.inchko.parkfinder.ui.settings

import android.os.Bundle
import android.widget.ImageButton
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceFragmentCompat
import androidx.preference.SwitchPreferenceCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R

class SettingsActivity : AppCompatActivity() {
    private lateinit var back: ImageButton

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.settings_activity)
        if (savedInstanceState == null) {
            supportFragmentManager
                .beginTransaction()
                .replace(R.id.settings, SettingsFragment())
                .commit()
        }
        back = findViewById(R.id.backSettings)
        back.setOnClickListener {
            backFun()
        }
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

    }

    fun backFun() {
        finish()
    }

    class SettingsFragment : PreferenceFragmentCompat() {
        override fun onCreatePreferences(savedInstanceState: Bundle?, rootKey: String?) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey)
            if (Firebase.auth.currentUser == null) {
                preferenceScreen.findPreference<SwitchPreferenceCompat>("searchFZ")?.isChecked =
                    false
                preferenceScreen.findPreference<SwitchPreferenceCompat>("searchFZ")?.isEnabled =
                    false
                preferenceScreen.findPreference<SwitchPreferenceCompat>("showVehicle")?.isChecked =
                    false
                preferenceScreen.findPreference<SwitchPreferenceCompat>("showVehicle")?.isEnabled =
                    false
            }
        }
    }
}