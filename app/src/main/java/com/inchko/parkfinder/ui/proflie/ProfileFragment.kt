package com.inchko.parkfinder.ui.proflie

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import android.widget.Toast.LENGTH_SHORT
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
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R
import com.inchko.parkfinder.domainModels.FavZone
import com.inchko.parkfinder.domainModels.POI
import com.inchko.parkfinder.ui.map.MapViewModel
import com.inchko.parkfinder.ui.proflie.SingUpEmail.SignInEmail
import com.inchko.parkfinder.ui.proflie.addPoi.AddPoiActivity
import com.inchko.parkfinder.ui.proflie.cutomizeProfile.CustomizeProfile
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
    private lateinit var addPOIButton: FloatingActionButton
    private lateinit var logButton: Button
    private lateinit var signIn: SignInButton
    private lateinit var signInEmail: Button
    private lateinit var customize: ImageButton
    private lateinit var loadPOI: ProgressBar
    private lateinit var loadFZ: ProgressBar

// ...
// Initialize Firebase Auth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {


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

        loadPOI = root.findViewById(R.id.loadingPOI)
        loadPOI.visibility = View.VISIBLE
        loadFZ = root.findViewById(R.id.loadingFavZones)
        loadFZ.visibility = View.VISIBLE

        signIn = root.findViewById(R.id.sign_in)//
        signIn.setOnClickListener() {
            signIn()
        }

        logButton = root.findViewById(R.id.logout)
        logButton.setOnClickListener() {
            logOut()
        }

        signInEmail = root.findViewById(R.id.Sign_Email)
        signInEmail.setOnClickListener() {
            signEmail()
        }
        customize = root.findViewById(R.id.profileCustomize)
        customize.setOnClickListener {
            customProfile()
        }
        if (auth.currentUser == null) logButton.visibility = View.INVISIBLE
        nametext = root.findViewById(R.id.profileName)
        if (auth.currentUser != null) {
            profileVM.getPOI(auth.currentUser.uid)
        }
        profileVM.poi.observe(viewLifecycleOwner, { value: List<POI>? ->
            value?.let {
                for (p in it) {
                    if (p.location == null) p.location =
                        getLocation(p.lat.toDouble(), p.long.toDouble())
                }
                view?.let { it1 -> initRVpoi(it1, it) }
            }
        })

        if (auth.currentUser != null) {
            profileVM.getFavZones(auth.currentUser.uid)
        }
        profileVM.fz.observe(viewLifecycleOwner, { value: List<FavZone>? ->
            value?.let {
                for (p in it) {
                    p.location = getLocation(p.lat.toDouble(), p.long.toDouble())
                }
                view?.let { it1 -> initRVfz(it1, it) }
            }
        })
        return root
    }

    private fun customProfile() {
        val intent = Intent(context, CustomizeProfile::class.java)
        startActivity(intent)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        addPOIButton = view.findViewById(R.id.addPoiButton)

        addPOIButton.setOnClickListener {
            if (auth.currentUser != null) {
                val intent = Intent(context, AddPoiActivity::class.java)
                intent.putExtra("user", Firebase.auth.currentUser.uid)
                startActivity(intent)
                profileVM.getPOI(auth.currentUser.uid)
            } else {
                Toast.makeText(
                    context,
                    context?.getString(R.string.notLogged),
                    LENGTH_SHORT
                ).show()
                Log.e("pf", "You have to log in")
            }
        }
    }

    override fun onStart() {
        super.onStart()
        // Check for existing Google Sign In account, if the user is already signed in the GoogleSignInAccount will be non-null.
        val account = auth.currentUser
        if (account != null) {
            updateUIStart(account)

        } else {
            nametext.text = getString(R.string.loginOrRegister)
            loadFZ.visibility = View.INVISIBLE
            loadPOI.visibility = View.INVISIBLE
        }
    }

    private fun updateUIStart(account: FirebaseUser?) {
        if (account != null) {
            nametext.text = account.displayName
            profileVM.updateGeneralUser(account)
            profileVM.getPOI(account.uid)
            profileVM.getFavZones(account.uid)
            signIn.visibility = View.INVISIBLE
            logButton.visibility = View.VISIBLE
            customize.visibility = View.VISIBLE
            loadFZ.visibility = View.VISIBLE
            loadPOI.visibility = View.VISIBLE
            signInEmail.visibility = View.INVISIBLE
        }
    }

    private fun signEmail() {
        val intent = Intent(context, SignInEmail::class.java)
        startActivity(intent)
        // updateUI(Firebase.auth.currentUser!!)
    }


    private fun signIn() {
        val signInIntent = mGoogleSignInClient.signInIntent
        startActivityForResult(signInIntent, RC_SIGN_IN)
    }

    private fun logOut() {
        Firebase.auth.signOut()
        nametext.text = getString(R.string.Logged_out)
        if (auth.currentUser != null) {
            nametext.text = auth.currentUser.displayName
        }
        val rvpoi: RecyclerView? = view?.findViewById(R.id.rvPOI)
        rvpoi?.visibility = View.INVISIBLE
        val rvfz: RecyclerView? = view?.findViewById(R.id.rvFavZone)
        rvfz?.visibility = View.INVISIBLE
        signIn.visibility = View.VISIBLE
        logButton.visibility = View.INVISIBLE
        customize.visibility = View.INVISIBLE
        signInEmail.visibility = View.VISIBLE

    }

    private fun updateUI(account: FirebaseUser) {
        nametext.text = account.email
        profileVM.updateGeneralUser(account)
        profileVM.getPOI(account.uid)
        profileVM.getFavZones(account.uid)
        val rvpoi: RecyclerView? = view?.findViewById(R.id.rvPOI)
        rvpoi?.visibility = View.VISIBLE
        val rvfz: RecyclerView? = view?.findViewById(R.id.rvFavZone)
        rvfz?.visibility = View.VISIBLE
        loadFZ.visibility = View.VISIBLE
        loadPOI.visibility = View.VISIBLE
        signIn.visibility = View.INVISIBLE
        logButton.visibility = View.VISIBLE
        customize.visibility = View.VISIBLE
        signInEmail.visibility = View.INVISIBLE
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
        if (auth.currentUser != null) {
            val rv: RecyclerView = view.findViewById(R.id.rvPOI)
            rv.apply {
                // set a LinearLayoutManager to handle Android
                // RecyclerView behavior
                layoutManager = LinearLayoutManager(activity)
                // set the custom adapter to the RecyclerView
                adapter = pois?.let { poi ->
                    Log.e("rvpoi", "pois loaded")
                    loadPOI.visibility = View.INVISIBLE
                    mapViewModel.currentLocation?.let { cl ->
                        Log.e("rvpoi", "CurrentLocation on")
                        PoiAdapter(
                            poi, profileVM,
                            cl,
                            context,
                        ) { it ->//Listener, add your actions here
                            Log.e("rv", "Zone clicked ${it.id}")

                        }
                    };
                }
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
                loadFZ.visibility = View.INVISIBLE
                mapViewModel.currentLocation?.let { cl ->
                    Log.e("rvfz", "CurrentLocation on")
                    fzAdapter(
                        fz, profileVM,
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