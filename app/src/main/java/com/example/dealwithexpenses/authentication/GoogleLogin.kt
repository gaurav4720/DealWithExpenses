package com.example.dealwithexpenses.authentication

import android.annotation.SuppressLint
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
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class GoogleLogin {

    companion object {
        @SuppressLint("StaticFieldLeak")
        private lateinit var googleSignInClient: GoogleSignInClient
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var sharedPreferences: SharedPreferences
        private lateinit var fragment: Fragment

        // sign in code to sign in with google
        private const val SIGN_IN_CODE = 12345

        // function called from login screen
        fun initialisation(fragment: Fragment) {
            // initialise the fragment
            this.fragment = fragment
            // initialise the shared preferences
            sharedPreferences =
                fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
            // calling the login function below
            login()
        }

        // function to login with google
        private fun login(){

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

        @DelicateCoroutinesApi
        fun firebaseAuthWithGoogle(account: GoogleSignInAccount) {
            firebaseAuth = FirebaseAuth.getInstance()
            val credentials = GoogleAuthProvider.getCredential(account.idToken, null)
            GlobalScope.launch(Dispatchers.IO) {

                firebaseAuth.signInWithCredential(credentials).addOnSuccessListener {
                    Toast.makeText(fragment.requireContext(), "Google sign in success 🎉", Toast.LENGTH_SHORT)
                        .show()

                    val user = it.user
                    sharedPreferences.edit().putString("user_id", user?.uid).apply()
                    sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                    sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                    Navigate.action(fragment)
                }
            }
        }

    }
}