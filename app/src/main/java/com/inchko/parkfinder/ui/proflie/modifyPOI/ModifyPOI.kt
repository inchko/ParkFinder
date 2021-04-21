package com.inchko.parkfinder.ui.proflie.modifyPOI

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModel
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.POI
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ModifyPOI : AppCompatActivity() {

    private lateinit var mod: Button
    private lateinit var exit: Button
    private lateinit var name: EditText
    private lateinit var location: EditText
    private lateinit var lat: EditText
    private lateinit var long: EditText
    private val vm: ModifyPOIViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.modifypoi_view)

        mod = findViewById(R.id.ModSend)
        exit = findViewById(R.id.ModCancel)
        name = findViewById(R.id.ModEditName)
        location = findViewById(R.id.ModEditLocation)
        lat = findViewById(R.id.ModEditLat)
        long = findViewById(R.id.ModEditLong)

        mod.setOnClickListener {
            send()
        }
        exit.setOnClickListener {
            exit()
        }
        name.setText(intent.getStringExtra("nombre"))
        lat.setText(intent.getStringExtra("lat"))
        long.setText(intent.getStringExtra("long"))
        location.setText(intent.getStringExtra("location"))


    }

    private fun exit() {
        finish()
    }

    private fun send() {
        val p = POI(
            id = intent.getStringExtra("id"),
            lat = lat.text.toString(),
            long = long.text.toString(),
            nombre = name.text.toString(),
            userID = intent.getStringExtra("userID"),
            location = location.text.toString(),
        )
        vm.send(p)
        finish()
    }

}