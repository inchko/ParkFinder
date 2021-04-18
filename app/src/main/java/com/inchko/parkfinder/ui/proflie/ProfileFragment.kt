package com.inchko.parkfinder.ui.proflie

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.SignInButton
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.ui.map.MapViewModel
import com.inchko.parkfinder.ui.proflie.rvFavZones.fzAdapter
import com.inchko.parkfinder.ui.proflie.rvPOI.PoiAdapter
import dagger.hilt.android.AndroidEntryPoint
import java.util.*


@AndroidEntryPoint
class ProfileFragment : Fragment() {

    val profileVM: ProfileViewModel by viewModels()
    val mapViewModel: MapViewModel by activityViewModels()
    private lateinit var mGoogleSignInClient: GoogleSignInClient
    private lateinit var auth: FirebaseAuth
    private lateinit var nametext: TextView

// ...
// Initialize Firebase Auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        profileVM.getPOI()
        profileVM.poi.observe(viewLifecycleOwner, {

                value: List<POI>? ->
            value?.let {
                for (p in it) {
                    p.location = getLocation(p.lat.toDouble(), p.long.toDouble())
                }
                view?.let { it1 -> initRVpoi(it1, it) }
            }
        })

        profileVM.getFavZones()
        profileVM.fz.observe(viewLifecycleOwner, {

                value: List<FavZone>? ->
            value?.let {
                for (p in it) {
                    p.location = getLocation(p.lat.toDouble(), p.long.toDouble())
                }
                view?.let { it1 -> initRVfz(it1, it) }
            }
        })

        // Configure sign-in to request the user's ID, email address, and basic
// profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        auth = Firebase.auth
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(getString(R.string.default_web_client_id))
            .requestEmail()
            .build()
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = activity?.let { GoogleSignIn.getClient(it, gso) }!!;

        val root = inflater.inflate(R.layout.fragment_profile, container, false)
        val signButton: SignInButton = root.findViewById(R.id.sign_in_button)//
        signButton.setOnClickListener() {
            signIn()
        }

        val logoutButton: Button = root.findViewById(R.id.logout)
        logoutButton.setOnClickListener() {
            logOut()
        }
        nametext = root.findViewById(R.id.profileName)

        return root
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.
        val account = auth.currentUser
        if (account != null) {
            updateUIStart(account)
        } else {
            nametext.text = "inicia sesion perro"
        }
    }

    private fun updateUIStart(account: FirebaseUser?) {
        if (account != null) {
            nametext.text = account.displayName
            profileVM.getPOI()
        }
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun logOut() {
        Firebase.auth.signOut()
        nametext.text = "No one is logged in"
        if (auth.currentUser != null) {
            nametext.text = auth.currentUser.displayName
        }
    }

    private fun updateUI(account: FirebaseUser) {
        nametext.text = account.email
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                // Google Sign In was successful, authenticate with Firebase
                val account = task.getResult(ApiException::class.java)!!
                Log.d(TAG, "firebaseAuthWithGoogle:" + account.id)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                // Google Sign In failed, update UI appropriately
                Log.w(TAG, "Google sign in failed", e)
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        activity?.let {
            auth.signInWithCredential(credential)
                .addOnCompleteListener(it) { task ->
                    if (task.isSuccessful) {
                        // Sign in success, update UI with the signed-in user's information
                        Log.d(TAG, "signInWithCredential:success")
                        val user = auth.currentUser
                        profileVM.registerOrLoginUser(user)
                        updateUI(user)
                    } else {
                        // If sign in fails, display a message to the user.
                        Log.w(TAG, "signInWithCredential:failure", task.exception)
                        // updateUI(null)
                    }
                }
        }
    }

    private fun getLocation(lat: Double, long: Double): String {
        Log.e("rvpoi", "$lat : $long")
        var addresses: List<Address>
        val gc = Geocoder(context, Locale.getDefault())
        addresses = gc.getFromLocation(lat, long, 2)
        while (addresses.isEmpty()) {
            addresses = gc.getFromLocation(lat, long, 2)
        }
        // Log.e("rvpoi", loc[0].getAddressLine(0))
        var text = "Localizacion no encontrada"
        if (addresses.isNotEmpty()) {
            val address: String = addresses[0].getAddressLine(0)
            // If any additional address line present than only, check with max available address lines by getMaxAddressLineIndex()

            text = address
        }
        return text

    }

    fun initRVpoi(view: View, pois: List<POI>) {
        Log.e("rvpoi", "initRV")
        val rv: RecyclerView = view.findViewById(R.id.rvPOI)
        rv.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = pois?.let { poi ->
                Log.e("rvpoi", "pois loaded")
                mapViewModel.currentLocation?.let { cl ->
                    Log.e("rvpoi", "CurrentLocation on")
                    PoiAdapter(
                        poi,
                        cl
                    ) { it ->//Listener, add your actions here
                        Log.e("rv", "Zone clicked ${it.id}")

                    }
                };
            }
        }
    }

    fun initRVfz(view: View, favzones: List<FavZone>) {
        Log.e("rvpoi", "initRV")
        val rv: RecyclerView = view.findViewById(R.id.rvFavZone)
        rv.apply {
            // set a LinearLayoutManager to handle Android
            // RecyclerView behavior
            layoutManager = LinearLayoutManager(activity)
            // set the custom adapter to the RecyclerView
            adapter = favzones?.let { fz ->
                Log.e("rvfz", "favzones loaded")
                mapViewModel.currentLocation?.let { cl ->
                    Log.e("rvfz", "CurrentLocation on")
                    fzAdapter(
                        fz,
                        cl
                    ) { it ->//Listener, add your actions here
                        Log.e("rv", "Zone clicked ${it.id}")

                    }
                };
            }
        }
    }

    companion object {
        private const val TAG = "GoogleActivity"
        private const val RC_SIGN_IN = 9001
    }
}