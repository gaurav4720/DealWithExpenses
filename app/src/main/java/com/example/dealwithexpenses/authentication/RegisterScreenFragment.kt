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
import com.example.dealwithexpenses.databinding.FragmentRegisterScreenBinding
import com.example.dealwithexpenses.entities.User
import com.facebook.CallbackManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth

class RegisterScreenFragment : Fragment() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    private lateinit var binding: FragmentRegisterScreenBinding
    private lateinit var firebaseAuth: FirebaseAuth
    private lateinit var sharedPreferences : SharedPreferences
    private lateinit var callBackManager: CallbackManager

    private val SIGN_IN_CODE = 12345

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        // Initialize Firebase Auth
        firebaseAuth = FirebaseAuth.getInstance()

        // Initialize SharedPreferences
        sharedPreferences = activity?.getSharedPreferences("user_auth", Context.MODE_PRIVATE)!!


        // Inflate the layout for this fragment
        binding = FragmentRegisterScreenBinding.inflate(inflater, container, false)

        binding.registerButton.setOnClickListener {
            handleCustomRegistration()
        }

        binding.googleLoginButton.setOnClickListener {
            GoogleLogin.login(this)
        }
        binding.fbLoginButton.setOnClickListener {
            callBackManager = CallbackManager.Factory.create()
            FBLogin.login(this, callBackManager)
        }

        return binding.root
    }

    private fun handleCustomRegistration() {
        val email = binding.userName.text.toString()
        val password = binding.passWord.text.toString()
        val confirmation = binding.confirmPassWord.text.toString()

        if (email.isEmpty() or password.isEmpty() or confirmation.isEmpty()) {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        } else if (password != confirmation) {
            Toast.makeText(context, "Passwords do not match", Toast.LENGTH_SHORT).show()
        } else {
            firebaseAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { auth ->
                    if (auth.isSuccessful) {
                        Toast.makeText(context, "Successfully registered", Toast.LENGTH_SHORT)
                            .show()
                        val user = User(email, password, "0")
                        firebaseAuth.currentUser?.uid?.let {
                            user.user_id = it
                        }
                    } else {
                        Toast.makeText(context, "Registration failed", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == SIGN_IN_CODE) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                GoogleLogin.firebaseAuthWithGoogle(account!!)
            } catch (e: ApiException) {
                Toast.makeText(context, "Google sign in failed ☹", Toast.LENGTH_SHORT)
                    .show()
            }
            super.onActivityResult(requestCode, resultCode, data)
        } else
            callBackManager.onActivityResult(requestCode, resultCode, data)
    }

}