package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.dealwithexpenses.R
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
import kotlinx.coroutines.withContext
import kotlinx.coroutines.*

class FBLogin {

    companion object {

        private lateinit var firebaseAuth: FirebaseAuth
        private lateinit var sharedPreferences: SharedPreferences

        private lateinit var fragment: Fragment

        fun login(fragment: Fragment, callBackManager: CallbackManager) {
            this.fragment = fragment

            LoginManager.getInstance()
                .logInWithReadPermissions(fragment, listOf("public_profile", "email"))

            LoginManager.getInstance()
                .registerCallback(
                    callBackManager,
                    object : FacebookCallback<LoginResult> {
                        override fun onCancel() {
                            Toast.makeText(fragment.activity, "Login Cancelled", Toast.LENGTH_SHORT)
                                .show()
                        }

                        override fun onError(error: FacebookException) {
                            Toast.makeText(
                                fragment.activity, "Login Failed", Toast.LENGTH_SHORT
                            ).show()
                        }

                        override fun onSuccess(result: LoginResult) {
                            handleFBLogin(result)
                        }

                    }
                )
        }

        @OptIn(DelicateCoroutinesApi::class)
        private fun handleFBLogin(result: LoginResult) {

            firebaseAuth = FirebaseAuth.getInstance()
            sharedPreferences =
                fragment.requireActivity().getSharedPreferences("user_auth", Context.MODE_PRIVATE)


            val credentials = FacebookAuthProvider.getCredential(result.accessToken.token)
            GlobalScope.launch(Dispatchers.IO) {
                val auth = firebaseAuth.signInWithCredential(credentials)

                auth.addOnCompleteListener {
                    if (it.isSuccessful) {
                        Log.d("FBLogin", "${it.result?.user?.displayName} logged in")
                        Toast.makeText(fragment.activity, "Login Successful", Toast.LENGTH_SHORT)
                            .show()
                        val user = auth.result?.user
                            sharedPreferences.edit().putBoolean("isRegistered", true).apply()
                            Navigate.action(fragment)
                    } else {
                        Toast.makeText(fragment.activity, it.exception?.message.toString(), Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
    }
}