package com.inchko.parkfinder.ui.proflie.SingUpEmail

import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.inchko.parkfinder.R

class SignInEmail : AppCompatActivity() {

    private lateinit var ok: Button
    private lateinit var no: Button
    private lateinit var recover: Button
    private lateinit var email: EditText
    private lateinit var pass: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.signinemail_view)

        ok = findViewById(R.id.leOk)
        no = findViewById(R.id.leNo)
        email = findViewById(R.id.leEmail)
        pass = findViewById(R.id.lePassword)
        recover = findViewById(R.id.leRecover)
        ok.setOnClickListener {
            signin()
        }
        no.setOnClickListener {
            finish()
        }
        recover.setOnClickListener {
            sendMail()
        }
    }

    private fun sendMail() {
        val emailAddress = email.text.toString()

        val builder = AlertDialog.Builder(this)
        builder.setTitle(getString(R.string.sendMailTitle))
        builder.setMessage(getString(R.string.sendMailMess1) + emailAddress + getString(R.string.sendMailMess2))

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

    private fun signin() {
        Firebase.auth.signInWithEmailAndPassword(email.text.toString(), pass.text.toString())
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    Toast.makeText(
                        this, getString(R.string.emailsignok),
                        Toast.LENGTH_SHORT
                    ).show()
                    finish()
                } else {
                    // If sign in fails, display a message to the user.
                    Toast.makeText(
                        this, getString(R.string.emailsignNo),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
    }
}