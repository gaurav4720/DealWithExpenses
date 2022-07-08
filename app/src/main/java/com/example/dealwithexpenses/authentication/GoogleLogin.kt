package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.SharedPreferences
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.dealwithexpenses.R
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GoogleLogin {

    companion object {
        private lateinit var googleSignInClient: GoogleSignInClient
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var sharedPreferences: SharedPreferences

        private lateinit var fragment: Fragment

        private const val SIGN_IN_CODE = 12345

        fun initialisation(fragment: Fragment) {
            this.fragment = fragment

            sharedPreferences =
                fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
            login(sharedPreferences)
        }
        fun login(sharedPreferences: SharedPreferences){
            val googleSignInOptions =
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                    .requestIdToken(fragment.getString(R.string.default_web_client_id))
                    .requestEmail()
                    .build()

            googleSignInClient =
                GoogleSignIn.getClient(fragment.requireActivity(), googleSignInOptions)
            val intent = googleSignInClient.signInIntent
            fragment.startActivityForResult(
                intent,
                SIGN_IN_CODE,
            )
        }

        fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
            firebaseAuth = FirebaseAuth.getInstance()
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            GlobalScope.launch(Dispatchers.IO) {

                val auth = firebaseAuth.signInWithCredential(credentials).addOnSuccessListener {
                    Toast.makeText(fragment.requireContext(), "Google sign in success 🎉", Toast.LENGTH_SHORT)
                        .show()

                    val user = it.user

                    if (user != null) {
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                        Navigate.action(fragment)
                    }
                }
            }
        }

    }
}