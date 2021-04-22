package com.inchko.parkfinder.ui.proflie.cutomizeProfile.addVehicle

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
class AddVehicle : AppCompatActivity() {

    private lateinit var back: Button
    private lateinit var add: Button
    private lateinit var model: EditText
    private lateinit var switchSize: SwitchCompat
    private lateinit var switchType: SwitchCompat
    private val vm: AddVehicleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.addvehicle_view)
        back = findViewById(R.id.acExit)
        add = findViewById(R.id.acAdd)
        model = findViewById(R.id.avEditModel)
        switchSize = findViewById(R.id.acSizeSwitch)
        switchType = findViewById(R.id.acTypeSwitch)
        back.setOnClickListener {
            goBack()
        }
        add.setOnClickListener {
            addCar()
        }
    }

    private fun goBack() {
        finish()
    }

    private fun addCar() {
        var type = 0
        var size = 0
        if (switchType.isChecked) type = 1
        if (type == 1) size = 1
        if (switchSize.isChecked) size = 1
        vm.addCar(
            Firebase.auth.currentUser.uid,
            Vehicles(
                model = model.text.toString(),
                id = " ",
                type = type,
                size = size
            )
        )
        finish()
    }

}