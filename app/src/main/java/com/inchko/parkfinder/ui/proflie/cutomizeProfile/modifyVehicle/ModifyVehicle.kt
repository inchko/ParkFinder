package com.inchko.parkfinder.ui.proflie.cutomizeProfile.modifyVehicle

import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SwitchCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Vehicles
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class ModifyVehicle : AppCompatActivity() {

    private lateinit var back: Button
    private lateinit var mod: Button
    private lateinit var model: EditText
    private lateinit var switchSize: SwitchCompat
    private lateinit var switchType: SwitchCompat
    private val vm: ModifyVehicleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.addvehicle_view)

        back = findViewById(R.id.acExit)
        mod = findViewById(R.id.acAdd)
        model = findViewById(R.id.avEditModel)
        switchSize = findViewById(R.id.acSizeSwitch)
        switchType = findViewById(R.id.acTypeSwitch)

        model?.setText(intent.getStringExtra("model"))
        switchSize.isChecked = true
        switchType.isChecked = true
        val type = intent.getIntExtra("type", 0)
        if (type == 0) switchType.isChecked = false
        val size = intent.getIntExtra("size", 0)
        if (size == 0) switchSize.isChecked = false


        back.setOnClickListener {
            goBack()
        }
        mod.setOnClickListener {
            modCar()
        }
    }

    private fun goBack() {
        finish()
    }

    private fun modCar() {
        var type = 0
        var size = 0
        if (switchType.isChecked) type = 1
        if (type == 1) size = 1
        if (switchSize.isChecked) size = 1
        vm.modCar(
            Firebase.auth.currentUser.uid,
            Vehicles(
                model = model.text.toString(),
                id = intent.getStringExtra("id"),
                type = type,
                size = size
            )
        )
        finish()
    }

}