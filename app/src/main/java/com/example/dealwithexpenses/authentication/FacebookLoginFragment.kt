package com.example.dealwithexpenses.authentication

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
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

class FacebookLoginFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

    }
    private lateinit var callbackManager: CallbackManager
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences: SharedPreferences

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        sharedPreferences= activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!
        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Inflate the layout for this fragment
        callbackManager= CallbackManager.Factory.create()
        LoginManager.getInstance().logInWithReadPermissions(this, listOf("public_profile","email"))
        LoginManager.getInstance().registerCallback(
            callbackManager, object : FacebookCallback<LoginResult> {
                override fun onCancel() {
                    Toast.makeText(requireContext(),"Login Cancelled", Toast.LENGTH_SHORT).show()
                }
                override fun onError(error: FacebookException) {
                    Toast.makeText(requireContext(),"Login Failed", Toast.LENGTH_SHORT).show()
                }
                override fun onSuccess(result: LoginResult) {
                    handleFbLogin(result)
                }
            }
        )
        return inflater.inflate(R.layout.fragment_facebook_login, container, false)
    }

    private fun handleFbLogin(result: LoginResult) {
        GlobalScope.launch(Dispatchers.IO){
            val credential= FacebookAuthProvider.getCredential(result.accessToken.token)
            val auth= firebaseAuth.signInWithCredential(credential)
            val allCheck: Boolean= sharedPreferences.getBoolean("allCheck",false)
            auth.addOnCompleteListener {
                if(it.isSuccessful){
                    val user= auth.result?.user
                }
                else{
                    Toast.makeText(requireContext(),"Login Failed", Toast.LENGTH_SHORT).show()
                }
            }
            withContext(Dispatchers.Main){
                sharedPreferences.edit().putBoolean("isRegistered",true).apply()
                var action =
                    LoginScreenFragmentDirections.actionLoginScreenFragmentToMainScreenFragment()
                if (!allCheck)
                    action =
                        LoginScreenFragmentDirections.actionLoginScreenFragmentToUserBudgetDetailsFragment()

                findNavController().navigate(action)
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        callbackManager.onActivityResult(requestCode, resultCode, data)
    }
}