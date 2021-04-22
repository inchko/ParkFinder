package com.inchko.parkfinder.ui.proflie.cutomizeProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.ImageButton
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.addVehicle.AddVehicle
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.rvcp.cpAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomizeProfile : AppCompatActivity() {


    private lateinit var back: ImageButton
    private lateinit var add: Button
    private val vm: cpViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customprofile_view)
        back = findViewById(R.id.cpBack)
        add = findViewById(R.id.cpAddCar)
        back.setOnClickListener {
            goBack()
        }
        add.setOnClickListener {
            addCar()
        }

        vm.getVehicles(Firebase.auth.currentUser.uid)
        vm.cars.observe(this, { value: List<Vehicles>? ->
            value?.let {
                initRVcars(it)
            }

        })
    }


    private fun goBack() {
        finish()
    }

    private fun addCar() {
        Log.e("cp", "holis")
        val intent = Intent(this, AddVehicle::class.java)
        startActivity(intent)
    }

    private fun initRVcars(cars: List<Vehicles>) {
        Log.e("cp", "initRV")
        val rv: RecyclerView = findViewById(R.id.cpRVcoches)
        rv.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(context)
            // set the custom adapter to the RecyclerView
            adapter = cars.let { fz ->
                Log.e("rvfz", "vehicles loaded")
                cpAdapter(
                    fz, vm,
                ) { it ->//Listener, add your actions here
                    Log.e("rv", "Zone clicked ${it.model}")

                }
            };
        }
    }
}
