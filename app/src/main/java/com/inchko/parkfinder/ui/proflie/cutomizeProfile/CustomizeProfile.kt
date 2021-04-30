package com.inchko.parkfinder.ui.proflie.cutomizeProfile

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.Vehicles
import com.inchko.parkfinder.ui.proflie.ProfileFragment
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.addVehicle.AddVehicle
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.rvcp.cpAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CustomizeProfile : AppCompatActivity() {


    private lateinit var back: ImageButton
    private lateinit var add: Button
    private lateinit var changePass: Button
    private lateinit var recoverPass: Button
    private lateinit var link: Button

    private lateinit var loading: ProgressBar
    private val vm: cpViewModel by viewModels()
    private lateinit var password: EditText
    private lateinit var currPass: EditText
    private lateinit var newPass: EditText
    private lateinit var confPass: EditText

    private lateinit var mGoogleSignInClient: GoogleSignInClient


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.customprofile_view)

        back = findViewById(R.id.cpBack)
        add = findViewById(R.id.cpAddCar)
        password = findViewById(R.id.cpPassword)
        newPass = findViewById(R.id.cpEditNP)
        currPass = findViewById(R.id.cpEditCP)
        confPass = findViewById(R.id.cpEditCC)
        changePass = findViewById(R.id.cpChangeContra)
        recoverPass = findViewById(R.id.cpRecuperarContraseña)
        loading = findViewById(R.id.cpLoading)

        recoverPass.setOnClickListener {
            sendEmail()
        }


        loading.visibility = View.VISIBLE
        back.setOnClickListener {
            goBack()
        }
        add.setOnClickListener {
            addCar()
        }
        link = findViewById(R.id.cpLinkEmail)
        link.setOnClickListener {
            linkFun()
        }
        changePass.setOnClickListener {
            changePassword()
        }

        vm.getVehicles(Firebase.auth.currentUser.uid)
        vm.cars.observe(this, { value: List<Vehicles>? ->
            value?.let {
                initRVcars(it)
            }

        })
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)
    }

    private fun sendEmail() {

        val emailAddress = Firebase.auth.currentUser.email

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sendMailTitle))
        builder.setMessage(getString(R.string.sendMailMess1) + emailAddress)

        builder.setPositiveButton(android.R.string.yes) { dialog, which ->
            Firebase.auth.sendPasswordResetEmail(emailAddress!!)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Log.d("email", "Email sent.")
                    }
                }
        }
        builder.setNegativeButton(android.R.string.no) { dialog, which ->
        }

        builder.show()

    }

    private fun changePassword() {

        var user = Firebase.auth.currentUser!!
        var currentPassword = currPass.text.toString()
        if (currentPassword == "") currentPassword =
            "zdfknldfjvnaslisdfbjapisj mlsdanapv ahsnvbjOLBafhwrEÑ"
        val mail = user.email
        Firebase.auth.signInWithEmailAndPassword(mail, currentPassword)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d("cp", "signInWithEmail:success")
                    user = Firebase.auth.currentUser
                    val newPassword = confPass.text.toString()
                    val checkPassword = newPass.text.toString()
                    val currrentPassword = currPass.text.toString()
                    if (currrentPassword == newPassword || checkPassword == currrentPassword) {
                        Toast.makeText(
                            this, getString(R.string.samePass),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else if (newPassword != checkPassword) {
                        Toast.makeText(
                            this, getString(R.string.differentPass),
                            Toast.LENGTH_SHORT
                        ).show()
                    } else {
                        user.updatePassword(newPassword)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    Toast.makeText(
                                        this, getString(R.string.updatedPass),
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            }
                    }
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w("cp", "signInWithEmail:failure", task.exception)
                    Toast.makeText(
                        this, getString(R.string.badCurrentPass),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

    }


    private fun linkFun() {
        val user = Firebase.auth.currentUser!!
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, 9001)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == 9001) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d("link", "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w("link", "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        this.let {
            Firebase.auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        val user = Firebase.auth.currentUser
                        val pass = password.text.toString()
                        if (pass != "") {
                            val credentialE =
                                EmailAuthProvider.getCredential(
                                    user.email,
                                    password.text.toString()
                                )
                            Firebase.auth.currentUser!!.linkWithCredential(credentialE)
                                .addOnCompleteListener(this) { link ->
                                    if (link.isSuccessful) {
                                        Log.d("link", "linkWithCredential:success")
                                        Toast.makeText(
                                            baseContext, "Link successful",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        Log.w("link", "linkWithCredential:failure", task.exception)
                                        Toast.makeText(
                                            baseContext, "Link fail.",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    }
                                }
                        } else
                            Toast.makeText(this, getString(R.string.emptyPass), Toast.LENGTH_SHORT)
                                .show()
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w("Link", "signInWithCredential:failure", task.exception)
                    }
                }
        }
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
                loading.visibility = View.INVISIBLE
                cpAdapter(
                    fz, vm, context,
                ) { it ->//Listener, add your actions here
                    Log.e("rv", "Zone clicked ${it.model}")

                }
            };
        }
    }
}
