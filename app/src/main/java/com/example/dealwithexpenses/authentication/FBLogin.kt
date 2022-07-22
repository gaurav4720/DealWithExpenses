package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.SharedPreferences
import androidx.fragment.app.Fragment
import android.widget.Toast
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.*

class FBLogin {

    companion object {
        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var sharedPreferences: SharedPreferences

        private lateinit var fragment: Fragment


        @DelicateCoroutinesApi
        fun login(fragment: Fragment, callBackManager: CallbackManager) {
            // fixing the fragment of this file as the previous one.
            this.fragment = fragment

            // login with facebook using the logInWithReadPermissions method.
            LoginManager.getInstance()
                .logInWithReadPermissions(fragment, listOf("public_profile", "email"))

            // callback manager is used to get the result of the login.
            LoginManager.getInstance()
                .registerCallback(
                    callBackManager,
                    object : FacebookCallback<LoginResult> {
                        // if the user cancels this option for login, login cancelled will be shown
                        override fun onCancel() {
                            Toast.makeText(fragment.activity, "Login Cancelled", Toast.LENGTH_SHORT)
                                .show()
                        }

                        // if the login is unsuccessful, the error is shown.
                        override fun onError(error: FacebookException) {
                            Toast.makeText(
                                fragment.activity, "Login Failed", Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onSuccess(result: LoginResult) {
                            //on success, backend of FB handle works
                            handleFBLogin(result)
                        }

                    }
                )
        }

        @DelicateCoroutinesApi
        private fun handleFBLogin(result: LoginResult) {

            // initializing the firebase auth and the shared preferences.
            firebaseAuth = FirebaseAuth.getInstance()
            sharedPreferences =
                fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)

            // getting credentials from the facebook provider
            val credentials = FacebookAuthProvider.getCredential(result.accessToken.token)
            GlobalScope.launch(Dispatchers.IO) {
                // logging in the user with the credentials.
                val auth = firebaseAuth.signInWithCredential(credentials)

                // when the task is complete
                auth.addOnCompleteListener {
                    // if the task is successful
                    if (it.isSuccessful) {
                        sharedPreferences.edit().putString("user_id", firebaseAuth.currentUser?.uid).apply()
                        // showing a toast message
                        Toast.makeText(fragment.activity, "Login Successful", Toast.LENGTH_SHORT)
                            .show()
                    } else {
                        // if the task is unsuccessful, showing the error by FB Handle as a toast message
                        Toast.makeText(fragment.activity, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
                withContext(Dispatchers.Main) {
                    if (auth.isSuccessful) {
                        Navigate.action(fragment)
                        sharedPreferences.edit().putBoolean("isLoggedIn", true).apply()
                        sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                    }
                }
            }
        }
    }
}